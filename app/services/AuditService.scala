/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models.UserAnswers
import pages._
import play.api.libs.json.{Format, JsString, Json}
import play.api.mvc.Request
import services.JobRetentionSchemeCalculatorEvent.JobRetentionSchemeCalculatorEvent
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.DataEvent
import viewmodels.ConfirmationViewBreakdown

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object JobRetentionSchemeCalculatorEvent extends Enumeration {
  type JobRetentionSchemeCalculatorEvent = Value

  val CalculationPerformed = Value
}

case class AuditPeriodBreakdown(grant: BigDecimal, payPeriodEndDate: LocalDate)

object AuditPeriodBreakdown {
  implicit val defaultFormat: Format[AuditPeriodBreakdown] = Json.format
}

case class AuditCalculationResult(total: BigDecimal, periodBreakdowns: Seq[AuditPeriodBreakdown])

object AuditCalculationResult {
  implicit val defaultFormat: Format[AuditCalculationResult] = Json.format
}

case class AuditBreakdown(furlough: AuditCalculationResult, nic: AuditCalculationResult, pension: AuditCalculationResult)

object AuditBreakdown {
  implicit val defaultFormat: Format[AuditBreakdown] = Json.format
}

@Singleton
class AuditService @Inject()(auditConnector: AuditConnector, config: FrontendAppConfig) {

  def sendCalculationPerformed(
    userAnswers: UserAnswers,
    breakdown: ConfirmationViewBreakdown)(implicit hc: HeaderCarrier, request: Request[Any], ec: ExecutionContext): Future[Unit] =
    auditEvent(
      JobRetentionSchemeCalculatorEvent.CalculationPerformed,
      "calculation-performed",
      Seq(
        "userAnswers"       -> userAnswersTransformer(userAnswers),
        "calculationResult" -> breakdownTransformer(breakdown)
      )
    )

  private def userAnswersTransformer(userAnswers: UserAnswers) =
    Json.obj(
      "claimPeriodStartDate"               -> JsString(userAnswers.get(ClaimPeriodStartPage).fold("")(_.toString)),
      "claimPeriodEndDate"                 -> JsString(userAnswers.get(ClaimPeriodEndPage).fold("")(_.toString)),
      "employeeFurloughStartDate"          -> JsString(userAnswers.get(FurloughStartDatePage).fold("")(_.toString)),
      "hasTheEmployeeFurloughEnded"        -> JsString(userAnswers.get(FurloughOngoingPage).fold("")(_.toString)),
      "employeeFurloughEndDate"            -> JsString(userAnswers.get(FurloughEndDatePage).fold("")(_.toString)),
      "employeePayFrequency"               -> JsString(userAnswers.get(PaymentFrequencyPage).fold("")(_.toString)),
      "employeePayMethod"                  -> JsString(userAnswers.get(PayQuestionPage).fold("")(_.toString)),
      "employeeSalary"                     -> JsString(userAnswers.get(SalaryQuestionPage).fold("")(_.amount.toString)),
      "employeeEmployedOnOrBefore1Feb2019" -> JsString(userAnswers.get(VariableLengthEmployedPage).fold("")(_.toString)),
      "employeeStartDate"                  -> JsString(userAnswers.get(EmployeeStartDatePage).fold("")(_.toString)),
      "employeeGrossPayForYear"            -> JsString(userAnswers.get(VariableGrossPayPage).fold("")(_.amount.toString)),
      "employeePayPeriodEndDates"          -> Json.toJson(userAnswers.getList(PayDatePage)),
      "employeePayDayForLastPeriod"        -> JsString(userAnswers.get(LastPayDatePage).fold("")(_.toString)),
      "employeeLastYearPay"                -> Json.toJson(userAnswers.getList(LastYearPayPage)),
      "employeePartialPayBeforeFurlough"   -> JsString(userAnswers.get(PartialPayBeforeFurloughPage).fold("")(_.value.toString)),
      "employeePartialPayAfterFurlough"    -> JsString(userAnswers.get(PartialPayAfterFurloughPage).fold("")(_.value.toString)),
      "employeeNationalInsuranceCategory"  -> JsString(userAnswers.get(NicCategoryPage).fold("")(_.toString)),
      "employerPensionContributions"       -> JsString(userAnswers.get(PensionContributionPage).fold("")(_.toString))
    )

  private def breakdownTransformer(breakdown: ConfirmationViewBreakdown) = {
    val furlough = AuditCalculationResult(
      breakdown.furlough.total,
      breakdown.furlough.payPeriodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.periodWithPaymentDate.period.period.end))
    )

    val nic = AuditCalculationResult(
      breakdown.nic.total,
      breakdown.nic.payPeriodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.periodWithPaymentDate.period.period.end)))

    val pension = AuditCalculationResult(
      breakdown.pension.total,
      breakdown.pension.payPeriodBreakdowns
        .map(ppb => AuditPeriodBreakdown(ppb.grant.value, ppb.periodWithPaymentDate.period.period.end))
    )

    val result = AuditBreakdown(furlough, nic, pension)

    Json.toJson(result)
  }

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
