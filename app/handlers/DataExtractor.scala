/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.FurloughQuestion.{No, Yes}
import models.PayQuestion.{Regularly, Varies}
import models.{Amount, FurloughQuestion, NicCategory, NonFurloughPay, PayQuestion, PaymentFrequency, PaymentWithPeriod, PensionStatus, Period, PeriodWithPaymentDate, Periods, UserAnswers}
import pages._
import services.ReferencePayCalculator

case class MandatoryData(
  claimPeriod: Period,
  paymentFrequency: PaymentFrequency,
  nicCategory: NicCategory,
  pensionStatus: PensionStatus,
  payQuestion: PayQuestion,
  furloughQuestion: FurloughQuestion,
  payDates: Seq[LocalDate],
  furloughStart: LocalDate,
  lastPayDay: LocalDate)

trait DataExtractor extends ReferencePayCalculator {

  def extract(userAnswers: UserAnswers): Option[MandatoryData] =
    for {
      claimStart    <- userAnswers.get(ClaimPeriodStartPage)
      claimEnd      <- userAnswers.get(ClaimPeriodEndPage)
      frequency     <- userAnswers.get(PaymentFrequencyPage)
      nic           <- userAnswers.get(NicCategoryPage)
      pension       <- userAnswers.get(PensionAutoEnrolmentPage)
      payQuestion   <- userAnswers.get(PayQuestionPage)
      furlough      <- userAnswers.get(FurloughQuestionPage)
      furloughStart <- userAnswers.get(FurloughStartDatePage)
      lastPayDay    <- userAnswers.get(LastPayDatePage)
      payDate = userAnswers.getList(PayDatePage)
    } yield MandatoryData(Period(claimStart, claimEnd), frequency, nic, pension, payQuestion, furlough, payDate, furloughStart, lastPayDay)

  def extractFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    extract(userAnswers).flatMap { data =>
      data.furloughQuestion match {
        case Yes => patchEndDate(userAnswers)
        case No  => Some(Period(data.furloughStart, data.claimPeriod.end))
      }
    }

  def extractPayments(userAnswers: UserAnswers, furloughPeriod: Period): Option[Seq[PaymentWithPeriod]] =
    for {
      data     <- extract(userAnswers)
      grossPay <- extractGrossPay(userAnswers)
      periods: Seq[Periods] = generatePeriods(data.payDates, furloughPeriod)
      periodsWithPayDay = assignPayDates(data.paymentFrequency, periods, data.lastPayDay)
    } yield {
      data.payQuestion match {
        case Regularly => periodsWithPayDay.map(p => PaymentWithPeriod(Amount(0.0), grossPay, p, Regularly))
        case Varies =>
          extractVariablePayments(userAnswers, periodsWithPayDay).fold(Seq[PaymentWithPeriod]())(payments => payments)
      }
    }

  protected def extractGrossPay(userAnswers: UserAnswers): Option[Amount] =
    extract(userAnswers).flatMap { data =>
      data.payQuestion match {
        case Regularly => userAnswers.get(SalaryQuestionPage).map(v => Amount(v.amount))
        case Varies    => userAnswers.get(VariableGrossPayPage).map(v => Amount(v.amount))
      }
    }

  protected def extractPriorFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      data              <- extract(userAnswers)
      employeeStartDate <- userAnswers.get(EmployeeStartDatePage)
    } yield endDateOrTaxYearEnd(Period(employeeStartDate, data.claimPeriod.start.minusDays(1)))

  private def extractVariablePayments(userAnswers: UserAnswers, periods: Seq[PeriodWithPaymentDate]): Option[Seq[PaymentWithPeriod]] =
    for {
      grossPay            <- extractGrossPay(userAnswers)
      priorFurloughPeriod <- extractPriorFurloughPeriod(userAnswers)
      preFurloughPay = userAnswers.get(PartialPayBeforeFurloughPage)
      postFurloughPay = userAnswers.get(PartialPayAfterFurloughPage)
      nonFurloughPay = NonFurloughPay(preFurloughPay.map(v => Amount(v.value)), postFurloughPay.map(v => Amount(v.value)))
    } yield {
      calculateVariablePay(nonFurloughPay, priorFurloughPeriod, periods, grossPay)
    }

  private def patchEndDate(userAnswers: UserAnswers): Option[Period] =
    for {
      data <- extract(userAnswers)
      end  <- userAnswers.get(FurloughEndDatePage)
    } yield Period(data.furloughStart, end)

}
