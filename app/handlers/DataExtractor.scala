/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.PayQuestion.{Regularly, Varies}
import models.{Amount, CylbEligibility, CylbPayment, MandatoryData, NonFurloughPay, PaymentFrequency, PaymentWithPeriod, Period, PeriodWithPaymentDate, Periods, UserAnswers}
import pages._
import services.{FurloughPeriodHelper, ReferencePayCalculator}
import utils.LocalDateHelpers

trait DataExtractor extends ReferencePayCalculator with LocalDateHelpers with FurloughPeriodHelper {

  def extract(userAnswers: UserAnswers): Option[MandatoryData] =
    for {
      claimStart    <- userAnswers.get(ClaimPeriodStartPage)
      claimEnd      <- userAnswers.get(ClaimPeriodEndPage)
      frequency     <- userAnswers.get(PaymentFrequencyPage)
      nic           <- userAnswers.get(NicCategoryPage)
      pension       <- userAnswers.get(PensionContributionPage)
      payQuestion   <- userAnswers.get(PayQuestionPage)
      furlough      <- userAnswers.get(FurloughQuestionPage)
      furloughStart <- userAnswers.get(FurloughStartDatePage)
      lastPayDay    <- userAnswers.get(LastPayDatePage)
      payDate = userAnswers.getList(PayDatePage)
    } yield MandatoryData(Period(claimStart, claimEnd), frequency, nic, pension, payQuestion, furlough, payDate, furloughStart, lastPayDay)

  def extractRelevantFurloughPeriod(data: MandatoryData, userAnswers: UserAnswers): Option[Period] =
    Some(
      extractRelevantFurloughPeriod(data.furloughStart, userAnswers.get(FurloughEndDatePage), data.claimPeriod.start, data.claimPeriod.end))

  def extractPayments(userAnswers: UserAnswers, furloughPeriod: Period): Option[Seq[PaymentWithPeriod]] =
    for {
      data     <- extract(userAnswers)
      grossPay <- extractGrossPay(userAnswers)
      periods: Seq[Periods] = generatePeriods(data.payDates, furloughPeriod)
      periodsWithPayDay = assignPayDates(data.paymentFrequency, periods, data.lastPayDay)
    } yield processPayAnswer(userAnswers, data, grossPay, periodsWithPayDay)

  protected def extractGrossPay(userAnswers: UserAnswers): Option[Amount] =
    extract(userAnswers).flatMap { data =>
      data.payQuestion match {
        case Regularly => userAnswers.get(SalaryQuestionPage).map(v => Amount(v.amount))
        case Varies    => userAnswers.get(VariableGrossPayPage).map(v => Amount(v.amount))
      }
    }

  protected def extractPriorFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      data <- extract(userAnswers)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage).fold(LocalDate.of(2019, 4, 6))(v => v)
    } yield endDateOrTaxYearEnd(Period(employeeStartDate, data.furloughStart.minusDays(1)))

  private def processPayAnswer(
    userAnswers: UserAnswers,
    data: MandatoryData,
    grossPay: Amount,
    periodsWithPayDay: Seq[PeriodWithPaymentDate]): Seq[PaymentWithPeriod] =
    data.payQuestion match {
      case Regularly => periodsWithPayDay.map(p => PaymentWithPeriod(Amount(0.0), grossPay, p, Regularly))
      case Varies    => processVaries(userAnswers, data, periodsWithPayDay)
    }

  private def processVaries(
    userAnswers: UserAnswers,
    data: MandatoryData,
    periodsWithPayDay: Seq[PeriodWithPaymentDate]): Seq[PaymentWithPeriod] = {
    val cylbAmounts: Seq[CylbPayment] =
      if (cylbEligible(userAnswers).fold(CylbEligibility(false))(v => v).eligible)
        userAnswers.getList(LastYearPayPage)
      else
        Seq.empty

    extractVariablePayments(userAnswers, periodsWithPayDay, cylbAmounts, data.paymentFrequency)
      .fold(Seq[PaymentWithPeriod]())(payments => payments)
  }

  private def cylbEligible(userAnswers: UserAnswers): Option[CylbEligibility] =
    for {
      priorFurloughPeriod <- extractPriorFurloughPeriod(userAnswers)
      variableLength      <- userAnswers.get(VariableLengthEmployedPage)
    } yield cylbCalculationPredicate(variableLength, priorFurloughPeriod.start)

  private def extractVariablePayments(
    userAnswers: UserAnswers,
    periods: Seq[PeriodWithPaymentDate],
    cylbs: Seq[CylbPayment],
    frequency: PaymentFrequency): Option[Seq[PaymentWithPeriod]] =
    for {
      grossPay            <- extractGrossPay(userAnswers)
      priorFurloughPeriod <- extractPriorFurloughPeriod(userAnswers)
      preFurloughPay = userAnswers.get(PartialPayBeforeFurloughPage)
      postFurloughPay = userAnswers.get(PartialPayAfterFurloughPage)
      nonFurloughPay = NonFurloughPay(preFurloughPay.map(v => Amount(v.value)), postFurloughPay.map(v => Amount(v.value)))
    } yield calculateVariablePay(nonFurloughPay, priorFurloughPeriod, periods, grossPay, cylbs, frequency)

}
