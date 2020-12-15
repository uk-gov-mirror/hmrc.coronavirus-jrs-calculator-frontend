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

package controllers

import assets.BaseITConstants
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{EmployeeRTISubmission, FullPeriod, FurloughStatus, Hours, PartTimeHours, PartTimeQuestion, PartialPeriod, PayMethod, PayPeriodsList, Period, RegularLengthEmployed, UserAnswers, UsualHours}
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

object DecemberConfirmationScenarios extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers
  with BaseITConstants with ITCoreTestData {

  val decemberVariableFourWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-10-29")
      .withFurloughEndDate("2020-12-28")
      .withPaymentFrequency(FourWeekly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-05".toLocalDate,"2020-12-02".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-02".toLocalDate)), PartialPeriod(Period("2020-12-03".toLocalDate,"2020-12-30".toLocalDate),Period("2020-12-03".toLocalDate,"2020-12-28".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(1500)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-04", "2020-12-02", "2020-12-30"))
      .withUsualHours(List(UsualHours("2020-12-02".toLocalDate,Hours(40.0)), UsualHours("2020-12-30".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-02".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-30".toLocalDate,Hours(15.0))))
      -> 709.02,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-04-04")
      .withFurloughEndDate("2020-12-31")
      .withPaymentFrequency(FourWeekly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-31")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-30".toLocalDate,"2021-01-26".toLocalDate),Period("2020-12-31".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(19564.4)
      .withFurloughStartDate("2020-12-29")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-12-29", "2021-01-26"))
      .withUsualHours(List(UsualHours("2021-01-26".toLocalDate,Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2021-01-26".toLocalDate,Hours(14.0))))
      -> 38.10,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-28")
      .withPaymentFrequency(FourWeekly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List("2019-12-30"-> 2800))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(36000)
      .withFurloughStartDate("2020-12-04")
      .withClaimPeriodEnd("2020-12-28")
      .withPayDate(List("2020-11-30", "2020-12-28"))
      .withUsualHours(List())
      .withPartTimeHours(List())
      -> 2000.00
  )

  val decemberVariableMonthlyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-08-01")
      .withFurloughEndDate("2020-12-31")
      .withPaymentFrequency(Monthly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-01".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(10000)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-31".toLocalDate,Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-31".toLocalDate,Hours(14.0))))
      -> 1321.36,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-04-01")
      .withFurloughEndDate("2020-12-31")
      .withPaymentFrequency(Monthly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-01".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(10000)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-31".toLocalDate,Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-31".toLocalDate,Hours(14.0))))
      -> 674.46,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-31")
      .withPaymentFrequency(Monthly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List("2019-12-31" -> 2000))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(26000)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List())
      .withPartTimeHours(List())
      -> 1953.99
  )

  val decemberVariableTwoWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-08-27")
      .withFurloughEndDate("2020-12-31")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-13")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-14".toLocalDate),Period("2020-12-13".toLocalDate,"2020-12-14".toLocalDate)), FullPeriod(Period("2020-12-15".toLocalDate,"2020-12-28".toLocalDate)), PartialPeriod(Period("2020-12-29".toLocalDate,"2021-01-11".toLocalDate),Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(34000)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28", "2021-01-11"))
      .withUsualHours(List(UsualHours("2020-12-14".toLocalDate,Hours(40.0)), UsualHours("2020-12-28".toLocalDate,Hours(50.0)), UsualHours("2021-01-11".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-14".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-28".toLocalDate,Hours(15.0)), PartTimeHours("2021-01-11".toLocalDate,Hours(15.0))))
      -> 1081.91,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-03-20")
      .withFurloughEndDate("2020-12-25")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-14".toLocalDate),Period("2020-12-05".toLocalDate,"2020-12-14".toLocalDate)), PartialPeriod(Period("2020-12-15".toLocalDate,"2020-12-28".toLocalDate),Period("2020-12-15".toLocalDate,"2020-12-25".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(19564.4)
      .withFurloughStartDate("2020-12-05")
      .withClaimPeriodEnd("2020-12-29")
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28"))
      .withUsualHours(List(UsualHours("2020-12-14".toLocalDate,Hours(40.0)), UsualHours("2020-12-28".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-14".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-28".toLocalDate,Hours(15.0))))
      -> 914.60,
    emptyUserAnswers
      .withRtiSubmission(EmployeeRTISubmission.Yes)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-03-19")
      .withFurloughEndDate("2020-12-25")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-14".toLocalDate),Period("2020-12-05".toLocalDate,"2020-12-14".toLocalDate)), PartialPeriod(Period("2020-12-15".toLocalDate,"2020-12-28".toLocalDate),Period("2020-12-15".toLocalDate,"2020-12-25".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(1000)
      .withFurloughStartDate("2020-12-05")
      .withClaimPeriodEnd("2020-12-29")
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28"))
      .withUsualHours(List(UsualHours("2020-12-14".toLocalDate,Hours(40.0)), UsualHours("2020-12-28".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-14".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-28".toLocalDate,Hours(15.0))))
      -> 631.16,
    emptyUserAnswers
      .withRtiSubmission(EmployeeRTISubmission.No)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-03-19")
      .withFurloughEndDate("2020-12-25")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-14".toLocalDate),Period("2020-12-05".toLocalDate,"2020-12-14".toLocalDate)), PartialPeriod(Period("2020-12-15".toLocalDate,"2020-12-28".toLocalDate),Period("2020-12-15".toLocalDate,"2020-12-25".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(1000)
      .withFurloughStartDate("2020-12-05")
      .withClaimPeriodEnd("2020-12-29")
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28"))
      .withUsualHours(List(UsualHours("2020-12-14".toLocalDate,Hours(40.0)), UsualHours("2020-12-28".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-14".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-28".toLocalDate,Hours(15.0))))
      -> 46.80,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-28")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List("2019-12-16"->840, "2019-12-30"->980))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(28000)
      .withFurloughStartDate("2020-12-04")
      .withClaimPeriodEnd("2020-12-28")
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28"))
      .withUsualHours(List())
      .withPartTimeHours(List())
      -> 1530.00
  )

  val decemberVariableWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-05-05")
      .withFurloughEndDate("2020-12-14")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-07".toLocalDate),Period("2020-12-04".toLocalDate,"2020-12-07".toLocalDate)), FullPeriod(Period("2020-12-08".toLocalDate,"2020-12-14".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(19564.4)
      .withFurloughStartDate("2020-12-04")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-07", "2020-12-14"))
      .withUsualHours(List(UsualHours("2020-12-07".toLocalDate,Hours(40.0)), UsualHours("2020-12-14".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-07".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-14".toLocalDate,Hours(15.0))))
      -> 551.11,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-04-01")
      .withFurloughEndDate("2020-12-14")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-07".toLocalDate),Period("2020-12-04".toLocalDate,"2020-12-07".toLocalDate)), FullPeriod(Period("2020-12-08".toLocalDate,"2020-12-14".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(26000)
      .withFurloughStartDate("2020-12-04")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-07", "2020-12-14"))
      .withUsualHours(List(UsualHours("2020-12-07".toLocalDate,Hours(40.0)), UsualHours("2020-12-14".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-07".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-14".toLocalDate,Hours(15.0))))
      -> 613.53,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-04-01")
      .withFurloughEndDate("2020-12-14")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-07".toLocalDate),Period("2020-12-04".toLocalDate,"2020-12-07".toLocalDate)), FullPeriod(Period("2020-12-08".toLocalDate,"2020-12-14".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(1000)
      .withFurloughStartDate("2020-12-04")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-07", "2020-12-14"))
      .withUsualHours(List(UsualHours("2020-12-07".toLocalDate,Hours(40.0)), UsualHours("2020-12-14".toLocalDate,Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-07".toLocalDate,Hours(14.0)), PartTimeHours("2020-12-14".toLocalDate,Hours(15.0))))
      -> 24.78,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-07")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2020-12-01")
      .withLastYear(List("2019-12-02"->420, "2019-12-09"->490))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-01".toLocalDate,"2020-12-07".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(26000)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withPayDate(List("2020-11-30", "2020-12-07"))
      .withUsualHours(List(UsualHours("2020-12-07".toLocalDate,Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-07".toLocalDate,Hours(14.0))))
      -> 258.58,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-24")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2020-12-03")
      .withLastYear(List("2019-12-09" -> 420,"2019-12-16" -> 490, "2019-12-23" -> 560, "2019-12-30" -> 630))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(26000)
      .withFurloughStartDate("2020-12-04")
      .withClaimPeriodEnd("2020-12-28")
      .withPayDate(List("2020-11-30", "2020-12-07", "2020-12-14", "2020-12-21", "2020-12-28"))
      .withUsualHours(List())
      .withPartTimeHours(List())
      -> 1257.15
  )

  val decemberWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-01".toLocalDate,"2020-12-07".toLocalDate)), FullPeriod(Period("2020-12-08".toLocalDate,"2020-12-14".toLocalDate)), FullPeriod(Period("2020-12-15".toLocalDate,"2020-12-21".toLocalDate)), FullPeriod(Period("2020-12-22".toLocalDate,"2020-12-28".toLocalDate)), PartialPeriod(Period("2020-12-29".toLocalDate,"2021-01-04".toLocalDate),Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(600)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-30", "2020-12-07", "2020-12-14", "2020-12-21", "2020-12-28", "2021-01-04"))
      .withUsualHours(List(UsualHours("2020-12-07".toLocalDate,Hours(37.0)), UsualHours("2020-12-14".toLocalDate,Hours(37.0)), UsualHours("2020-12-21".toLocalDate,Hours(37.0)), UsualHours("2020-12-28".toLocalDate,Hours(37.0)), UsualHours("2021-01-04".toLocalDate,Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020-12-07".toLocalDate,Hours(10.0)), PartTimeHours("2020-12-14".toLocalDate,Hours(12.0)), PartTimeHours("2020-12-21".toLocalDate,Hours(10.0)), PartTimeHours("2020-12-28".toLocalDate,Hours(15.0)), PartTimeHours("2021-01-04".toLocalDate,Hours(1.06))))
      -> 1502.24,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-02")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-04".toLocalDate,"2020-12-10".toLocalDate),Period("2020-12-06".toLocalDate,"2020-12-10".toLocalDate)), FullPeriod(Period("2020-12-11".toLocalDate,"2020-12-17".toLocalDate)), FullPeriod(Period("2020-12-18".toLocalDate,"2020-12-24".toLocalDate)), PartialPeriod(Period("2020-12-25".toLocalDate,"2020-12-31".toLocalDate),Period("2020-12-25".toLocalDate,"2020-12-29".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1200)
      .withFurloughStartDate("2020-12-06")
      .withClaimPeriodEnd("2020-12-29")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-12-03", "2020-12-10", "2020-12-17", "2020-12-24", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-10".toLocalDate,Hours(37.0)), UsualHours("2020-12-17".toLocalDate,Hours(37.0)), UsualHours("2020-12-24".toLocalDate,Hours(37.0)), UsualHours("2020-12-31".toLocalDate,Hours(37.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-10".toLocalDate,Hours(10.0)), PartTimeHours("2020-12-17".toLocalDate,Hours(12.0)), PartTimeHours("2020-12-24".toLocalDate,Hours(10.0)), PartTimeHours("2020-12-31".toLocalDate,Hours(15.0))))
      -> 1344.84,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-27")
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-02")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-04".toLocalDate,"2020-12-10".toLocalDate),Period("2020-12-06".toLocalDate,"2020-12-10".toLocalDate)), PartialPeriod(Period("2020-12-25".toLocalDate,"2020-12-31".toLocalDate),Period("2020-12-25".toLocalDate,"2020-12-27".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1200)
      .withFurloughStartDate("2020-12-06")
      .withClaimPeriodEnd("2020-12-27")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-12-03", "2020-12-10", "2020-12-17", "2020-12-24", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-10".toLocalDate,Hours(37.0)), UsualHours("2020-12-31".toLocalDate,Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020-12-10".toLocalDate,Hours(10.0)), PartTimeHours("2020-12-31".toLocalDate,Hours(1.06))))
      -> 1673.88,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-15".toLocalDate,"2020-12-21".toLocalDate)), FullPeriod(Period("2020-12-22".toLocalDate,"2020-12-28".toLocalDate)), PartialPeriod(Period("2020-12-29".toLocalDate,"2021-01-04".toLocalDate),Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(600)
      .withFurloughStartDate("2020-12-15")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-12-14", "2020-12-21", "2020-12-28", "2021-01-04"))
      .withUsualHours(List(UsualHours("2020-12-21".toLocalDate,Hours(37.0)), UsualHours("2020-12-28".toLocalDate,Hours(37.0)), UsualHours("2021-01-04".toLocalDate,Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020-12-21".toLocalDate,Hours(10.0)), PartTimeHours("2020-12-28".toLocalDate,Hours(15.0)), PartTimeHours("2021-01-04".toLocalDate,Hours(1.06))))
      -> 827.64,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-29")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-29".toLocalDate,"2021-01-04".toLocalDate),Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(550)
      .withFurloughStartDate("2020-12-29")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-12-28", "2021-01-04"))
      .withUsualHours(List(UsualHours("2021-01-04".toLocalDate,Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2021-01-04".toLocalDate,Hours(14.0))))
      -> 122.57,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-08")
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-01".toLocalDate,"2020-12-07".toLocalDate)), PartialPeriod(Period("2020-12-08".toLocalDate,"2020-12-14".toLocalDate),Period("2020-12-08".toLocalDate,"2020-12-08".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(600)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-21")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-30", "2020-12-07", "2020-12-14"))
      .withUsualHours(List(UsualHours("2020-12-07".toLocalDate,Hours(37.0)), UsualHours("2020-12-14".toLocalDate,Hours(37.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-07".toLocalDate,Hours(10.0)), PartTimeHours("2020-12-14".toLocalDate,Hours(12.0))))
      -> 396.60,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-07".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(666.12)
      .withFurloughStartDate("2020-11-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-30", "2020-12-07"))
      .withUsualHours(List(UsualHours("2020-12-07".toLocalDate,Hours(34.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-07".toLocalDate,Hours(11.4))))
      -> 101.94,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-26".toLocalDate,"2020-12-02".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(890.11)
      .withFurloughStartDate("2020-11-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-25", "2020-12-02"))
      .withUsualHours(List(UsualHours("2020-12-02".toLocalDate,Hours(34.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-02".toLocalDate,Hours(11.4))))
      -> 108.00,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-01")
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-29".toLocalDate,"2020-12-05".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-01".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(500)
      .withFurloughStartDate("2020-11-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-28", "2020-12-05"))
      .withUsualHours(List(UsualHours("2020-12-05".toLocalDate,Hours(10.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-05".toLocalDate,Hours(6.5))))
      -> 20.00,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-02")
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-25".toLocalDate,"2020-12-01".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-01".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(754.44)
      .withFurloughStartDate("2020-11-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-24", "2020-12-01", "2020-12-08"))
      .withUsualHours(List(UsualHours("2020-12-01".toLocalDate,Hours(7.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-01".toLocalDate,Hours(3.0))))
      -> 129.04
  )

  val decemberTwoWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-01".toLocalDate,"2020-12-14".toLocalDate)),
        FullPeriod(Period("2020-12-15".toLocalDate,"2020-12-28".toLocalDate)),
        PartialPeriod(Period("2020-12-29".toLocalDate,"2021-01-11".toLocalDate),
          Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(650)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28", "2021-01-11"))
      .withUsualHours(List(UsualHours("2020-12-14".toLocalDate,Hours(98.0)),
        UsualHours("2020-12-28".toLocalDate,Hours(98.0)), UsualHours("2021-01-11".toLocalDate,Hours(21.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-14".toLocalDate,Hours(48.0)),
        PartTimeHours("2020-12-28".toLocalDate,Hours(48.0)), PartTimeHours("2021-01-11".toLocalDate,Hours(6.0))))
      -> 610.19,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2020-12-01".toLocalDate,"2020-12-14".toLocalDate)),
        FullPeriod(Period("2020-12-15".toLocalDate,"2020-12-28".toLocalDate)),
        PartialPeriod(Period("2020-12-29".toLocalDate,"2021-01-11".toLocalDate),
          Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2300.12)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28", "2021-01-11"))
      .withUsualHours(List(UsualHours("2020-12-14".toLocalDate,Hours(98.0)),
        UsualHours("2020-12-28".toLocalDate,Hours(98.0)), UsualHours("2021-01-11".toLocalDate,Hours(21.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-14".toLocalDate,Hours(48.0)),
        PartTimeHours("2020-12-28".toLocalDate,Hours(48.0)), PartTimeHours("2021-01-11".toLocalDate,Hours(6.0))))
      -> 1350.20,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-13")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020-12-29".toLocalDate,"2021-01-11".toLocalDate),
        Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(650)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28", "2021-01-11"))
      .withUsualHours(List(UsualHours("2021-01-11".toLocalDate,Hours(21.0))))
      .withPartTimeHours(List(PartTimeHours("2021-01-11".toLocalDate,Hours(6.0))))
      -> 673.88,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-11")
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-03")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020-11-29".toLocalDate,"2020-12-12".toLocalDate),Period("2020-12-03".toLocalDate,"2020-12-11".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(464.28)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-11")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-28", "2020-12-12"))
      .withUsualHours(List(UsualHours("2020-12-12".toLocalDate,Hours(70.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-12".toLocalDate,Hours(43.0))))
      -> 92.10,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-27")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020-12-28".toLocalDate,"2021-01-10".toLocalDate),Period("2020-12-29".toLocalDate,"2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(789.12)
      .withFurloughStartDate("2020-12-29")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-12-27", "2021-01-10"))
      .withUsualHours(List(UsualHours("2021-01-10".toLocalDate,Hours(140.0))))
      .withPartTimeHours(List(PartTimeHours("2021-01-10".toLocalDate,Hours(50.0))))
      -> 86.97,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-25")
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020-12-01".toLocalDate,"2020-12-14".toLocalDate),
        Period("2020-12-05".toLocalDate,"2020-12-14".toLocalDate)),
        PartialPeriod(Period("2020-12-15".toLocalDate,"2020-12-28".toLocalDate),
          Period("2020-12-15".toLocalDate,"2020-12-25".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(643.12)
      .withFurloughStartDate("2020-12-05")
      .withClaimPeriodEnd("2020-12-29")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-30", "2020-12-14", "2020-12-28"))
      .withUsualHours(List(UsualHours("2020-12-14".toLocalDate,Hours(63.0)),
        UsualHours("2020-12-28".toLocalDate,Hours(77.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-14".toLocalDate,Hours(8.0)),
        PartTimeHours("2020-12-28".toLocalDate,Hours(12.0))))
      -> 662.08,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020-11-19".toLocalDate,"2020-12-02".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1000.34)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-18", "2020-12-02"))
      .withUsualHours(List(UsualHours("2020-12-02".toLocalDate,Hours(34.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-02".toLocalDate,Hours(11.4))))
      -> 76.55,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020-11-22".toLocalDate,"2020-12-05".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1500)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-21", "2020-12-05"))
      .withUsualHours(List(UsualHours("2020-12-05".toLocalDate,Hours(34.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-05".toLocalDate,Hours(11.4))))
      -> 108.00,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-01")
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020-11-23".toLocalDate,"2020-12-06".toLocalDate),Period("2020-12-01".toLocalDate,"2020-12-01".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1010.11)
      .withFurloughStartDate("2020-11-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-22", "2020-12-06"))
      .withUsualHours(List(UsualHours("2020-12-06".toLocalDate,Hours(10.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-06".toLocalDate,Hours(6.5))))
      -> 20.20,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-02")
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-18".toLocalDate,"2020-12-01".toLocalDate),
        Period("2020-12-01".toLocalDate,"2020-12-01".toLocalDate)),
        PartialPeriod(Period("2020-12-02".toLocalDate,"2020-12-15".toLocalDate),
          Period("2020-12-02".toLocalDate,"2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1465.55)
      .withFurloughStartDate("2020-11-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-17", "2020-12-01", "2020-12-15"))
      .withUsualHours(List(UsualHours("2020-12-01".toLocalDate,Hours(7.5)),
        UsualHours("2020-12-15".toLocalDate,Hours(7.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-01".toLocalDate,Hours(3.0)),
        PartTimeHours("2020-12-15".toLocalDate,Hours(3.0))))
      -> 96.78
  )

  val decemberMonthlyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020, 12, 1")
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 12, 1".toLocalDate, "2020, 12, 31".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2400)
      .withFurloughStartDate("2020, 12, 1")
      .withClaimPeriodEnd("2020, 12, 31")
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(
        UsualHours("2020, 12, 31".toLocalDate, Hours(160.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 31".toLocalDate, Hours(40.0))
      ))
      -> 1440.00,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020, 12, 1")
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withRegularPayAmount(3126)
      .withFurloughStartDate("2020, 12, 1")
      .withClaimPeriodEnd("2020, 12, 31")
      .withPayDate(List("2020-11-30", "2020-12-31"))
      -> 2500.00,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-21")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020, 12, 1")
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 12, 1".toLocalDate, "2020, 12, 31".toLocalDate),
          Period("2020, 12, 5".toLocalDate, "2020, 12, 21".toLocalDate)
        )
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withRegularPayAmount(2400)
      .withFurloughStartDate("2020, 12, 5")
      .withClaimPeriodEnd("2020, 12, 31")
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(
        UsualHours("2020, 12, 31".toLocalDate, Hours(127.5))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 31".toLocalDate, Hours(52.5))
      ))
      -> 619.35,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-21")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate,"2020-12-31".toLocalDate),Period("2020-12-05".toLocalDate,"2020-12-21".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(6500)
      .withFurloughStartDate("2020-12-05")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-31".toLocalDate,Hours(127.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-31".toLocalDate,Hours(52.5))))
      -> 806.50,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-11")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate, "2020-12-31".toLocalDate), Period("2020-12-02".toLocalDate, "2020-12-11".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2400)
      .withFurloughStartDate("2020-12-02")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-31".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-31".toLocalDate, Hours(40.0))))
      -> 464.51,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-20")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-02")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate, "2020-12-31".toLocalDate), Period("2020-12-02".toLocalDate, "2020-12-20".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(5555)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-20")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-31".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-31".toLocalDate, Hours(40.0))))
      -> 1149.26,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-26".toLocalDate, "2020-12-25".toLocalDate), Period("2020-12-01".toLocalDate, "2020-12-25".toLocalDate)),
        PartialPeriod(Period("2020-12-26".toLocalDate, "2021-01-25".toLocalDate), Period("2020-12-26".toLocalDate, "2020-12-31".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(4900)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-31")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-25", "2020-12-25", "2021-01-25"))
      .withUsualHours(List(UsualHours("2020-12-25".toLocalDate, Hours(160.0)), UsualHours("2021-01-25".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-25".toLocalDate, Hours(95.0)), PartTimeHours("2021-01-25".toLocalDate, Hours(95.0))))
      -> 1015.68,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate, "2020-12-31".toLocalDate), Period("2020-12-01".toLocalDate, "2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2654.11)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-31".toLocalDate, Hours(34.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-31".toLocalDate, Hours(11.4))))
      -> 91.72,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-26".toLocalDate, "2020-12-25".toLocalDate), Period("2020-12-01".toLocalDate, "2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2654.11)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-25", "2020-12-25"))
      .withUsualHours(List(UsualHours("2020-12-25".toLocalDate, Hours(34.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-25".toLocalDate, Hours(11.4))))
      -> 94.78,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-03".toLocalDate, "2020-12-02".toLocalDate), Period("2020-12-01".toLocalDate, "2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(3200.11)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-02", "2020-12-02"))
      .withUsualHours(List(UsualHours("2020-12-02".toLocalDate, Hours(34.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-02".toLocalDate, Hours(11.4))))
      -> 108.00,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-01")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-09".toLocalDate, "2020-12-08".toLocalDate), Period("2020-12-01".toLocalDate, "2020-12-01".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2322.11)
      .withFurloughStartDate("2020-11-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-08", "2020-12-08"))
      .withUsualHours(List(UsualHours("2020-12-08".toLocalDate, Hours(10.0))))
      .withPartTimeHours(List(PartTimeHours("2020-12-08".toLocalDate, Hours(6.5))))
      -> 21.67,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-11-02".toLocalDate, "2020-12-01".toLocalDate), Period("2020-12-01".toLocalDate, "2020-12-01".toLocalDate)),
        PartialPeriod(Period("2020-12-02".toLocalDate, "2021-01-01".toLocalDate), Period("2020-12-02".toLocalDate, "2020-12-02".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1465.55)
      .withFurloughStartDate("2020-12-01")
      .withClaimPeriodEnd("2020-12-02")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-01", "2020-12-01", "2021-01-01"))
      .withUsualHours(List(UsualHours("2020-12-01".toLocalDate, Hours(7.5)), UsualHours("2021-01-01".toLocalDate, Hours(7.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-01".toLocalDate, Hours(3.0)), PartTimeHours("2021-01-01".toLocalDate, Hours(3.0))))
      -> 46.15,

    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2020-12-01")
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2020-12-01".toLocalDate, "2020-12-31".toLocalDate), Period("2020-12-01".toLocalDate, "2020-12-01".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(3222)
      .withFurloughStartDate("2020-11-30")
      .withClaimPeriodEnd("2020-12-01")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-11-30", "2020-12-31"))
      .withUsualHours(List(UsualHours("2020-12-31".toLocalDate, Hours(7.5))))
      .withPartTimeHours(List(PartTimeHours("2020-12-31".toLocalDate, Hours(3.0))))
      -> 48.39,

  )

  val decemberFourWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-12-28")
      .withFurloughEndDate("2020, 11, 14")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 12, 1".toLocalDate, "2020, 12, 28".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2000)
      .withFurloughStartDate("2020, 12, 1")
      .withClaimPeriodEnd("2020, 12, 31")
      .withPayDate(List("2020-11-30", "2020-12-28"))
      .withUsualHours(List(
        UsualHours("2020, 12, 28".toLocalDate, Hours(148.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 28".toLocalDate, Hours(40.0))
      ))
      -> 1167.57,
    emptyUserAnswers
      .withRegularPayAmount(3300)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughStartDate("2020, 12, 1")
      .withClaimPeriodEnd("2020, 12, 31")
      .withFurloughEndDate("2020, 12, 28")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withFurloughInLastTaxYear(false)
      .withPayDate(List("2020-11-04", "2020-12-02", "2020-12-30"))
      -> 2258.20,
    emptyUserAnswers
      .withRegularPayAmount(3300)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withFurloughStartDate("2020, 12, 1")
      .withClaimPeriodEnd("2020, 12, 28")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withFurloughInLastTaxYear(false)
      .withPayDate(List("2020-11-30", "2020-12-28"))
      -> 2307.68,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 12, 31")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withRegularPayAmount(3500)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 12, 1".toLocalDate, "2020, 12, 28".toLocalDate)),
        PartialPeriod(
          Period("2020, 12, 29".toLocalDate, "2021, 1, 25".toLocalDate),
          Period("2020, 12, 29".toLocalDate, "2020, 12, 31".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 3, 1")
      .withClaimPeriodEnd("2020, 12, 31")
      .withPayDate(List("2020-11-30", "2020-12-28", "2021-01-25"))
      .withUsualHours(List(
        UsualHours("2020, 12, 28".toLocalDate, Hours(148.0)),
        UsualHours("2021, 1, 25".toLocalDate, Hours(15.86))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 28".toLocalDate, Hours(40.0)),
        PartTimeHours("2021, 1, 25".toLocalDate, Hours(1.86))
      ))
      -> 1897.56,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 31")
      .withRegularPayAmount(2200)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 12, 31".toLocalDate, "2021, 1, 27".toLocalDate),
          Period("2020, 12, 31".toLocalDate, "2020, 12, 31".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 12, 29")
      .withClaimPeriodEnd("2020, 12, 31")
      .withPayDate(List("2020-12-30", "2021-01-27"))
      .withUsualHours(List(
        UsualHours("2021, 1, 27".toLocalDate, Hours(148.00))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2021, 1, 27".toLocalDate, Hours(25.0))
      ))
      -> 52.24,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 12, 31")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withRegularPayAmount(3500)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 12, 29".toLocalDate, "2021, 1, 25".toLocalDate),
          Period("2020, 12, 29".toLocalDate, "2020, 12, 31".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 12, 1")
      .withClaimPeriodEnd("2020, 12, 31")
      .withPayDate(List("2020-11-30","2020-12-28", "2021-01-25"))
      .withUsualHours(List(
        UsualHours("2021, 1, 25".toLocalDate, Hours(15.86))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2021, 1, 25".toLocalDate, Hours(1.86))
      ))
      -> 2521.26,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withRegularPayAmount(3500)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 12, 29".toLocalDate, "2021, 1, 25".toLocalDate),
          Period("2020, 12, 29".toLocalDate, "2020, 12, 29".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 12, 1")
      .withClaimPeriodEnd("2020, 12, 29")
      .withPayDate(List("2020-11-30","2020-12-28", "2021-01-25"))
      .withUsualHours(List(
        UsualHours("2021, 1, 25".toLocalDate, Hours(15.86))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2021, 1, 25".toLocalDate, Hours(1.86))
      ))
      -> 2378.87,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withRegularPayAmount(2654.11)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 06".toLocalDate, "2020, 12, 3".toLocalDate),
          Period("2020, 12, 1".toLocalDate, "2020, 12, 2".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 3, 1")
      .withClaimPeriodEnd("2020, 12, 2")
      .withPayDate(List("2020-11-05","2020-12-03"))
      .withUsualHours(List(
        UsualHours("2020, 12, 3".toLocalDate, Hours(34.5))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 3".toLocalDate, Hours(11.4))
      ))
      -> 101.55,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withRegularPayAmount(3200.11)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 8".toLocalDate, "2020, 12, 5".toLocalDate),
          Period("2020, 12, 1".toLocalDate, "2020, 12, 2".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 3, 1")
      .withClaimPeriodEnd("2020, 12, 2")
      .withPayDate(List("2020-11-07","2020-12-05"))
      .withUsualHours(List(
        UsualHours("2020, 12, 5".toLocalDate, Hours(34.5))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 5".toLocalDate, Hours(11.4))
      ))
      -> 108.00,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020-12-01")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2020, 12, 1")
      .withRegularPayAmount(2322.11)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 10".toLocalDate, "2020, 12, 7".toLocalDate),
          Period("2020, 12, 1".toLocalDate, "2020, 12, 1".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 11, 1")
      .withClaimPeriodEnd("2020, 12, 2")
      .withPayDate(List("2020-11-09","2020-12-07"))
      .withUsualHours(List(
        UsualHours("2020, 12, 7".toLocalDate, Hours(10.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 7".toLocalDate, Hours(6.5))
      ))
      -> 23.22,
    emptyUserAnswers
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(FourWeekly)
      .withFurloughEndDate("2020-12-02")
      .withClaimPeriodStart("2020, 12, 1")
      .withRegularPayAmount(1465.55)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 4".toLocalDate, "2020, 12, 1".toLocalDate),
          Period("2020, 12, 1".toLocalDate, "2020, 12, 1".toLocalDate)),
        PartialPeriod(
          Period("2020, 12, 2".toLocalDate, "2020, 12, 29".toLocalDate),
          Period("2020, 12, 2".toLocalDate, "2020, 12, 2".toLocalDate))
      ))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withFurloughStartDate("2020, 11, 1")
      .withClaimPeriodEnd("2020, 12, 2")
      .withPayDate(List("2020-11-03","2020-12-01","2020-12-29"))
      .withUsualHours(List(
        UsualHours("2020, 12, 1".toLocalDate, Hours(7.5)),
        UsualHours("2020, 12, 29".toLocalDate, Hours(7.5))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 1".toLocalDate, Hours(3.0)),
        PartTimeHours("2020, 12, 29".toLocalDate, Hours(3.0))
      ))
      -> 50.24
  )
}
