/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import java.time.LocalDate

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models.UserAnswers
import models.UserAnswers.AnswerV
import pages._
import play.api.libs.json.{Format, JsString, Json, Writes}
import play.api.mvc.Request
import services.JobRetentionSchemeCalculatorEvent.JobRetentionSchemeCalculatorEvent
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.DataEvent
import viewmodels.ViewBreakdown

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object JobRetentionSchemeCalculatorEvent extends Enumeration {
  type JobRetentionSchemeCalculatorEvent = Value

  val CalculationPerformed, CalculationFailed = Value
}

case class AuditPeriodBreakdown(grant: BigDecimal, payPeriodEndDate: LocalDate)

object AuditPeriodBreakdown {
  implicit val defaultFormat: Format[AuditPeriodBreakdown] = Json.format
}

case class AuditCalculationResult(total: BigDecimal, periodBreakdowns: Seq[AuditPeriodBreakdown])

object AuditCalculationResult {
  implicit val defaultFormat: Format[AuditCalculationResult] = Json.format
}

case class AuditBreakdown(furlough: AuditCalculationResult, nic: Option[AuditCalculationResult], pension: Option[AuditCalculationResult])

object AuditBreakdown {
  implicit val defaultFormat: Format[AuditBreakdown] = Json.format
}

@Singleton
class AuditService @Inject()(auditConnector: AuditConnector, config: FrontendAppConfig) {

  def sendCalculationPerformed(
    userAnswers: UserAnswers,
    breakdown: ViewBreakdown)(implicit hc: HeaderCarrier, request: Request[Any], ec: ExecutionContext): Future[Unit] =
    auditEvent(
      JobRetentionSchemeCalculatorEvent.CalculationPerformed,
      "calculation-performed",
      Seq(
        "userAnswers"       -> userAnswersTransformer(userAnswers),
        "calculationResult" -> Json.toJson(breakdown.toAuditBreakdown)
      )
    )

  def sendCalculationFailed(
    userAnswers: UserAnswers)(implicit hc: HeaderCarrier, request: Request[Any], ec: ExecutionContext): Future[Unit] =
    auditEvent(
      JobRetentionSchemeCalculatorEvent.CalculationFailed,
      "calculation-failed",
      Seq(
        "userAnswers" -> userAnswersTransformer(userAnswers)
      )
    )

  implicit class AnswerRender[A](val answer: AnswerV[A]) {
    def render: String = answer.fold(nel => "", _.toString)

    def json: JsString = JsString(render)
  }

  implicit def answerEncoder[A]: Writes[AnswerV[A]] = (o: AnswerV[A]) => JsString(o.render)

  private def userAnswersTransformer(userAnswers: UserAnswers) =
    Json.obj(
      "claimPeriodStartDate"               -> userAnswers.getV(ClaimPeriodStartPage).json,
      "claimPeriodEndDate"                 -> userAnswers.getV(ClaimPeriodEndPage).json,
      "employeeFurloughStartDate"          -> userAnswers.getV(FurloughStartDatePage).json,
      "hasTheEmployeeFurloughEnded"        -> userAnswers.getV(FurloughStatusPage).json,
      "employeeFurloughEndDate"            -> userAnswers.getV(FurloughEndDatePage).json,
      "employeePayFrequency"               -> userAnswers.getV(PaymentFrequencyPage).json,
      "employeePayMethod"                  -> userAnswers.getV(PayMethodPage).json,
      "employeeRegularPay"                 -> userAnswers.getV(RegularPayAmountPage).map(_.amount).json,
      "employeeEmployedOnOrBefore1Feb2019" -> userAnswers.getV(EmployeeStartedPage).json,
      "employeeStartDate"                  -> userAnswers.getV(EmployeeStartDatePage).json,
      "employeeAnnualPayForYear"           -> userAnswers.getV(AnnualPayAmountPage).map(_.amount).json,
      "employeePayPeriodEndDates"          -> Json.toJson(userAnswers.getList(PayDatePage)),
      "employeePayDayForLastPeriod"        -> userAnswers.getV(LastPayDatePage).json,
      "employeeLastYearPay"                -> Json.toJson(userAnswers.getList(LastYearPayPage)),
      "employeePartialPayBeforeFurlough"   -> userAnswers.getV(PartialPayBeforeFurloughPage).json,
      "employeePartialPayAfterFurlough"    -> userAnswers.getV(PartialPayAfterFurloughPage).json,
      "employerTopUpAmounts"               -> Json.toJson(userAnswers.getList(TopUpAmountPage)),
      "employerAdditionalPayments"         -> Json.toJson(userAnswers.getList(AdditionalPaymentAmountPage)),
      "employeeNationalInsuranceCategory"  -> userAnswers.getV(NicCategoryPage).json,
      "employerPensionStatus"              -> userAnswers.getV(PensionStatusPage).json,
      "employeePartTimeStatus"             -> userAnswers.getV(PartTimeQuestionPage).json,
      "employeeActualHours"                -> Json.toJson(userAnswers.getList(PartTimeHoursPage)),
      "employeeUsualHours"                 -> Json.toJson(userAnswers.getList(PartTimeNormalHoursPage))
    )

  private def auditEvent(event: JobRetentionSchemeCalculatorEvent, transactionName: String, details: Seq[(String, Any)])(
    implicit hc: HeaderCarrier,
    request: Request[Any],
    ec: ExecutionContext): Future[Unit] =
    send(createEvent(event, transactionName, details: _*))

  private def createEvent(event: JobRetentionSchemeCalculatorEvent, transactionName: String, details: (String, Any)*)(
    implicit hc: HeaderCarrier,
    request: Request[Any]): DataEvent = {

    val detail = hc.toAuditDetails(details.map(pair => pair._1 -> pair._2.toString): _*)
    val tags = hc.toAuditTags(transactionName, request.path)
    DataEvent(auditSource = config.appName, auditType = event.toString, tags = tags, detail = detail)
  }

  private def send(events: DataEvent*)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    Future {
      events.foreach { event =>
        Try(auditConnector.sendEvent(event))
      }
    }

}
