/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.{Amount, BranchingQuestions, CylbPayment, JourneyCoreData, MandatoryData, NonFurloughPay, Period, UserAnswers}
import pages._
import services.{FurloughPeriodExtractor, PeriodHelper}

trait DataExtractor extends FurloughPeriodExtractor with PeriodHelper {

  def extract(userAnswers: UserAnswers): Option[MandatoryData] =
    for {
      claimStart    <- userAnswers.get(ClaimPeriodStartPage)
      claimEnd      <- userAnswers.get(ClaimPeriodEndPage)
      frequency     <- userAnswers.get(PaymentFrequencyPage)
      nic           <- userAnswers.get(NicCategoryPage)
      pension       <- userAnswers.get(PensionStatusPage)
      payMethod     <- userAnswers.get(PayMethodPage)
      furlough      <- userAnswers.get(FurloughStatusPage)
      furloughStart <- userAnswers.get(FurloughStartDatePage)
      lastPayDay    <- userAnswers.get(LastPayDatePage)
      payDate = userAnswers.getList(PayDatePage)
    } yield MandatoryData(Period(claimStart, claimEnd), frequency, nic, pension, payMethod, furlough, payDate, furloughStart, lastPayDay)

  def extractPriorFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      data <- extract(userAnswers)
      employeeStartDate = userAnswers.get(EmployeeStartDatePage).fold(LocalDate.of(2019, 4, 6))(v => v)
    } yield endDateOrTaxYearEnd(Period(employeeStartDate, data.furloughStart.minusDays(1)))

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

  def extractJourneyCoreData(userAnswers: UserAnswers): Option[JourneyCoreData] =
    for {
      data           <- extract(userAnswers)
      furloughPeriod <- extractFurloughWithinClaim(userAnswers)
      periods = generatePeriods(data.payDates, furloughPeriod)
      assigned = assignPayDates(data.paymentFrequency, periods, data.lastPayDay)
    } yield JourneyCoreData(furloughPeriod, assigned, data.paymentFrequency, data.nicCategory, data.pensionStatus)

}
