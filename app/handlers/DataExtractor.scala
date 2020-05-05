/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.{Amount, BranchingQuestions, CylbPayment, NicCategory, NonFurloughPay, PaymentFrequency, PensionStatus, Period, ReferencePayData, UserAnswers}
import pages._
import services.{FurloughPeriodExtractor, PeriodHelper}

trait DataExtractor extends FurloughPeriodExtractor with PeriodHelper {

  def extractPriorFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart <- userAnswers.get(FurloughStartDatePage)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage).getOrElse(LocalDate.of(2019, 4, 6))
    } yield endDateOrTaxYearEnd(Period(employeeStartDate, furloughStart.minusDays(1)))

  def extractNonFurlough(userAnswers: UserAnswers): NonFurloughPay = {
    val preFurloughPay = userAnswers.get(PartialPayBeforeFurloughPage)
    val postFurloughPay = userAnswers.get(PartialPayAfterFurloughPage)

    NonFurloughPay(preFurloughPay.map(v => Amount(v.value)), postFurloughPay.map(v => Amount(v.value)))
  }

  def extractBranchingQuestions(userAnswers: UserAnswers): Option[BranchingQuestions] =
    for {
      payMethod <- userAnswers.get(PayMethodPage)
      employeeStarted = userAnswers.get(EmployedStartedPage)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage)
    } yield BranchingQuestions(payMethod, employeeStarted, employeeStartDate)

  def extractSalary(userAnswers: UserAnswers): Option[Amount] =
    userAnswers.get(SalaryQuestionPage).map(v => Amount(v.amount))

  def extractVariableGrossPay(userAnswers: UserAnswers): Option[Amount] =
    userAnswers.get(VariableGrossPayPage).map(v => Amount(v.amount))

  def extractCylbPayments(userAnswers: UserAnswers): Seq[CylbPayment] =
    userAnswers.getList(LastYearPayPage)

  def extractNicCategory(userAnswers: UserAnswers): Option[NicCategory] =
    userAnswers.get(NicCategoryPage)

  def extractPensionStatus(userAnswers: UserAnswers): Option[PensionStatus] =
    userAnswers.get(PensionStatusPage)

  def extractClaimPeriodStart(userAnswers: UserAnswers): Option[LocalDate] =
    userAnswers.get(ClaimPeriodStartPage)

  def extractClaimPeriodEnd(userAnswers: UserAnswers): Option[LocalDate] =
    userAnswers.get(ClaimPeriodEndPage)

  def extractPaymentFrequency(userAnswers: UserAnswers): Option[PaymentFrequency] =
    userAnswers.get(PaymentFrequencyPage)

  def extractReferencePayData(userAnswers: UserAnswers): Option[ReferencePayData] =
    for {
      furloughPeriod <- extractFurloughWithinClaim(userAnswers)
      payDates = userAnswers.getList(PayDatePage)
      periods = generatePeriods(payDates, furloughPeriod)
      frequency  <- extractPaymentFrequency(userAnswers)
      lastPayDay <- userAnswers.get(LastPayDatePage)
      assigned = assignPayDates(frequency, periods, lastPayDay)
    } yield ReferencePayData(furloughPeriod, assigned, frequency)

}
