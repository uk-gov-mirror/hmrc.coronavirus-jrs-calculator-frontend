/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import javax.inject.{Inject, Singleton}
import models.UserAnswers
import pages._
import play.api.libs.json.{Format, Json}
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
class AuditService @Inject()(auditConnector: AuditConnector) {

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
    Seq(
      "claimPeriodStartDate"               -> userAnswers.get(ClaimPeriodStartPage).getOrElse(""),
      "claimPeriodEndDate"                 -> userAnswers.get(ClaimPeriodEndPage).getOrElse(""),
      "employeeFurloughStartDate"          -> userAnswers.get(FurloughStartDatePage).getOrElse(""),
      "hasTheEmployeeFurloughEnded"        -> userAnswers.get(FurloughQuestionPage).getOrElse(""),
      "employeeFurloughEndDate"            -> userAnswers.get(FurloughEndDatePage).getOrElse(""),
      "employeePayFrequency"               -> userAnswers.get(PaymentFrequencyPage).getOrElse(""),
      "employeePayMethod"                  -> userAnswers.get(PayQuestionPage).getOrElse(""),
      "employeeSalary"                     -> userAnswers.get(SalaryQuestionPage).getOrElse(""),
      "employeeEmployedOnOrBefore1Feb2019" -> userAnswers.get(VariableLengthEmployedPage).getOrElse(""),
      "employeeStartDate"                  -> userAnswers.get(EmployeeStartDatePage).getOrElse(""),
      "employeeGrossPayForYear"            -> userAnswers.get(VariableGrossPayPage).getOrElse(""),
      "employeePayPeriodEndDates"          -> userAnswers.getList(PayDatePage),
      "employeePayDayForLastPeriod"        -> userAnswers.get(LastPayDatePage).getOrElse(""),
      "employeeLastYearPay"                -> userAnswers.getList(LastYearPayPage),
      "employeePartialPayBeforeFurlough"   -> userAnswers.get(PartialPayBeforeFurloughPage).getOrElse(""),
      "employeePartialPayAfterFurlough"    -> userAnswers.get(PartialPayAfterFurloughPage).getOrElse(""),
      "employeeNationalInsuranceCategory"  -> userAnswers.get(NicCategoryPage).getOrElse(""),
      "employerPensionContributions"       -> userAnswers.get(PensionAutoEnrolmentPage).getOrElse("")
    )

  private def breakdownTransformer(breakdown: ConfirmationViewBreakdown): String = {
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

    Json.prettyPrint(Json.toJson(result))
  }

  private def auditEvent(event: JobRetentionSchemeCalculatorEvent, transactionName: String, details: Seq[(String, Any)] = Seq.empty)(
    implicit hc: HeaderCarrier,
    request: Request[Any],
    ec: ExecutionContext): Future[Unit] =
    send(createEvent(event, transactionName, details: _*))

  private def createEvent(event: JobRetentionSchemeCalculatorEvent, transactionName: String, details: (String, Any)*)(
    implicit hc: HeaderCarrier,
    request: Request[Any]): DataEvent = {

    val detail = hc.toAuditDetails(details.map(pair => pair._1 -> pair._2.toString): _*)
    val tags = hc.toAuditTags(transactionName, request.path)
    DataEvent(auditSource = "agent-client-authorisation", auditType = event.toString, tags = tags, detail = detail)
  }

  private def send(events: DataEvent*)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    Future {
      events.foreach { event =>
        Try(auditConnector.sendEvent(event))
      }
    }

}
