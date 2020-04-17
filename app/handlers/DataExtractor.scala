/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.FurloughDates.{EndedInClaim, StartedAndEndedInClaim, StartedInClaim}
import models.FurloughQuestion.{No, Yes}
import models.PayQuestion.{Regularly, Varies}
import models.{Amount, FurloughQuestion, NicCategory, PayQuestion, PaymentFrequency, PensionStatus, Period, RegularPayment, Salary, UserAnswers}
import pages._
import services.ReferencePayCalculator

case class MandatoryData(
  claimPeriod: Period,
  paymentFrequency: PaymentFrequency,
  nicCategory: NicCategory,
  pensionStatus: PensionStatus,
  payQuestion: PayQuestion,
  furloughQuestion: FurloughQuestion,
  payDates: Seq[LocalDate]) //TODO make it a NonEmptyList

trait DataExtractor extends ReferencePayCalculator {

  def extract(userAnswers: UserAnswers): Option[MandatoryData] =
    for {
      claimStart  <- userAnswers.get(ClaimPeriodStartPage)
      claimEnd    <- userAnswers.get(ClaimPeriodEndPage)
      frequency   <- userAnswers.get(PaymentFrequencyPage)
      nic         <- userAnswers.get(NicCategoryPage)
      pension     <- userAnswers.get(PensionAutoEnrolmentPage)
      payQuestion <- userAnswers.get(PayQuestionPage)
      furlough    <- userAnswers.get(FurloughQuestionPage)
      payDate = userAnswers.getList(PayDatePage)
    } yield MandatoryData(Period(claimStart, claimEnd), frequency, nic, pension, payQuestion, furlough, payDate)

  def extractFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    extract(userAnswers).flatMap { data =>
      data.furloughQuestion match {
        case Yes => Some(Period(data.claimPeriod.start, data.claimPeriod.end))
        case No  => processFurloughDates(userAnswers)
      }
    }

  def extractRegularPayments(userAnswers: UserAnswers): Option[Seq[RegularPayment]] =
    for {
      data     <- extract(userAnswers)
      grossPay <- extractGrossPay(userAnswers)
      periods = generatePayPeriods(data.payDates)
    } yield {
      data.payQuestion match {
        case Regularly => periods.map(p => RegularPayment(grossPay, p))
        case Varies =>
          extractVariableRegularPayments(userAnswers).fold(Seq[RegularPayment]())(payments => payments)
      }
    }

  protected def extractGrossPay(userAnswers: UserAnswers): Option[Salary] =
    extract(userAnswers).flatMap { data =>
      data.payQuestion match {
        case Regularly => userAnswers.get(SalaryQuestionPage)
        case Varies =>
          userAnswers.get(VariableGrossPayPage).map(v => Salary(v.amount)) //TODO Should merge Salary and VariableGrossPay models
      }
    }

  protected def extractPriorFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      data              <- extract(userAnswers)
      employeeStartDate <- userAnswers.get(EmployeeStartDatePage)
    } yield endDateOrTaxYearEnd(Period(employeeStartDate, data.claimPeriod.start.minusDays(1)))

  private def extractVariableRegularPayments(userAnswers: UserAnswers): Option[Seq[RegularPayment]] =
    for {
      data                <- extract(userAnswers)
      grossPay            <- extractGrossPay(userAnswers)
      priorFurloughPeriod <- extractPriorFurloughPeriod(userAnswers)
      periods = generatePayPeriods(data.payDates)
    } yield calculateVariablePay(priorFurloughPeriod, periods, Amount(grossPay.amount))

  private def processFurloughDates(userAnswers: UserAnswers): Option[Period] =
    userAnswers.get(FurloughDatesPage).flatMap { furloughDates =>
      furloughDates match {
        case StartedInClaim         => patchStartDate(userAnswers)
        case EndedInClaim           => patchEndDate(userAnswers)
        case StartedAndEndedInClaim => patchStartAndEndDate(userAnswers)
      }
    }

  private def patchStartDate(userAnswers: UserAnswers): Option[Period] =
    for {
      data  <- extract(userAnswers)
      start <- userAnswers.get(FurloughStartDatePage)
    } yield Period(start, data.claimPeriod.end)

  private def patchEndDate(userAnswers: UserAnswers): Option[Period] =
    for {
      data <- extract(userAnswers)
      end  <- userAnswers.get(FurloughEndDatePage)
    } yield Period(data.claimPeriod.start, end)

  private def patchStartAndEndDate(userAnswers: UserAnswers): Option[Period] =
    for {
      start <- userAnswers.get(FurloughStartDatePage)
      end   <- userAnswers.get(FurloughEndDatePage)
    } yield Period(start, end)
}
