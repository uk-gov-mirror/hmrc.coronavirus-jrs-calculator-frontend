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
import models.PaymentFrequency._
import models._
import play.api.test.Helpers._
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

class ConfirmationControllerISpec extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers
  with BaseITConstants with ITCoreTestData {

  val novemberVariableTwoWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-08-27")
      .withFurloughEndDate("2020, 11, 30")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020, 11, 13")
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 01".toLocalDate, "2020, 11, 14".toLocalDate),
          Period("2020, 11, 13".toLocalDate, "2020, 11, 14".toLocalDate)
        ),
        FullPeriod(Period("2020, 11, 15".toLocalDate, "2020, 11, 28".toLocalDate)),
        PartialPeriod(
          Period("2020, 11, 29".toLocalDate, "2020, 12, 12".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate)
        )
      ))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(34000.00)
      .withFurloughStartDate("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withPayDate(List("2020-10-31", "2020-11-14", "2020-11-28", "2020-12-12"))
      .withUsualHours(List(
        UsualHours("2020, 11, 14".toLocalDate, Hours(40.0)),
        UsualHours("2020, 11, 28".toLocalDate, Hours(50.0)),
        UsualHours("2020, 12, 12".toLocalDate, Hours(50.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 14".toLocalDate, Hours(14.0)),
        PartTimeHours("2020, 11, 28".toLocalDate, Hours(15.0)),
        PartTimeHours("2020, 12, 12".toLocalDate, Hours(15.0))
      ))
      -> 1032.71,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-03-20")
      .withFurloughEndDate("2020, 11, 25")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020, 11, 1")
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 01".toLocalDate, "2020, 11, 14".toLocalDate),
          Period("2020, 11, 5".toLocalDate, "2020, 11, 14".toLocalDate)
        ),
        PartialPeriod(
          Period("2020, 11, 15".toLocalDate, "2020, 11, 28".toLocalDate),
          Period("2020, 11, 15".toLocalDate, "2020, 11, 25".toLocalDate)
        )
      ))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(19564.40)
      .withFurloughStartDate("2020, 11, 5")
      .withClaimPeriodEnd("2020, 11, 29")
      .withPayDate(List("2020-10-31", "2020-11-14", "2020-11-28"))
      .withUsualHours(List(
        UsualHours("2020, 11, 14".toLocalDate, Hours(40.0)),
        UsualHours("2020, 11, 28".toLocalDate, Hours(50.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 14".toLocalDate, Hours(14.0)),
        PartTimeHours("2020, 11, 28".toLocalDate, Hours(15.0))
      ))
      -> 1043.42
  )

  val novemberVariableMonthlyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughEndDate("2020, 11, 30")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Variable)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withAnnualPayAmount(10000.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 30".toLocalDate))
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 30".toLocalDate, Hours(40.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 30".toLocalDate, Hours(14.0))
      ))
      .withEmployeeStartDate("2020-08-01")
      .withEmployeeStartedAfter1Feb2019()
      .withFurloughInLastTaxYear(false)
      -> 1625.00,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughEndDate("2020, 11, 30")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Variable)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withAnnualPayAmount(10000.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 30".toLocalDate))
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 30".toLocalDate, Hours(40.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 30".toLocalDate, Hours(14.0))
      ))
      .withEmployeeStartDate("2020-04-01")
      .withEmployeeStartedAfter1Feb2019()
      .withFurloughInLastTaxYear(false)
      -> 746.46,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 30")
      .withPaymentFrequency(Monthly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2020, 11, 1")
      .withLastYear(List("2019-11-30" -> 2000))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(26000.00)
      .withFurloughStartDate("2020, 3, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withPayDate(List("2020-10-31", "2020-11-30"))
      ->1890.96,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2019-12-01")
      .withFurloughEndDate("2020, 11, 30")
      .withPaymentFrequency(Monthly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2020, 11, 1")
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(12000.00)
      .withFurloughStartDate("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withPayDate(List("2020-10-31", "2020-11-30"))
      ->2267.76
  )

  val novemberVariableFourWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughEndDate("2020, 11, 28")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Variable)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-04", "2020-11-01", "2020-11-29"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withAnnualPayAmount(1500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(Period("2020, 10, 5".toLocalDate, "2020, 11, 1".toLocalDate),
          Period("2020, 11, 1".toLocalDate, "2020, 11, 1".toLocalDate)),
        PartialPeriod(Period("2020, 11, 2".toLocalDate, "2020, 11, 29".toLocalDate),
          Period("2020, 11, 2".toLocalDate, "2020, 11, 28".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 1".toLocalDate, Hours(40.0)),
        UsualHours("2020, 11, 29".toLocalDate, Hours(50.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 1".toLocalDate, Hours(14.0)),
        PartTimeHours("2020, 11, 29".toLocalDate, Hours(15.0))
      ))
      .withEmployeeStartDate("2020-10-29")
      .withEmployeeStartedAfter1Feb2019()
      .withFurloughInLastTaxYear(false)
      -> 1629.30,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 28")
      .withFurloughStartDate("2020, 11, 4")
      .withFurloughEndDate("2020, 11, 28")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Variable)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withAnnualPayAmount(36000)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withLastYear(List("2019-11-30" -> 2800))
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withFurloughInLastTaxYear(false)
      -> 2000.00
  )

  val novemberWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-07", "2020-11-14", "2020-11-21", "2020-11-28", "2020-12-05"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(600.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 7".toLocalDate)),
        FullPeriod(Period("2020, 11, 8".toLocalDate, "2020, 11, 14".toLocalDate)),
        FullPeriod(Period("2020, 11, 15".toLocalDate, "2020, 11, 21".toLocalDate)),
        FullPeriod(Period("2020, 11, 22".toLocalDate, "2020, 11, 28".toLocalDate)),
        PartialPeriod(Period("2020, 11, 29".toLocalDate, "2020, 12, 05".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 7".toLocalDate, Hours(37.0)),
        UsualHours("2020, 11, 14".toLocalDate, Hours(37.0)),
        UsualHours("2020, 11, 21".toLocalDate, Hours(37.0)),
        UsualHours("2020, 11, 28".toLocalDate, Hours(37.0)),
        UsualHours("2020, 12, 5".toLocalDate, Hours(15.86))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 7".toLocalDate, Hours(10.0)),
        PartTimeHours("2020, 11, 14".toLocalDate, Hours(12.0)),
        PartTimeHours("2020, 11, 21".toLocalDate, Hours(10.0)),
        PartTimeHours("2020, 11, 28".toLocalDate, Hours(15.0)),
        PartTimeHours("2020, 12, 5".toLocalDate, Hours(1.06)),
      ))
      -> 1438.26,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 2")
      .withClaimPeriodEnd("2020, 11, 29")
      .withFurloughStartDate("2020, 11, 6")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-03", "2020-11-10", "2020-11-17", "2020-11-24", "2020-12-01"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(1200.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 11".toLocalDate, "2020, 11, 17".toLocalDate)),
        FullPeriod(Period("2020, 11, 18".toLocalDate, "2020, 11, 24".toLocalDate)),
        PartialPeriod(Period("2020, 11, 4".toLocalDate, "2020, 11, 10".toLocalDate),
          Period("2020, 11, 6".toLocalDate, "2020, 11, 10".toLocalDate)),
        PartialPeriod(Period("2020, 11, 25".toLocalDate, "2020, 12, 1".toLocalDate),
          Period("2020, 11, 25".toLocalDate, "2020, 11, 29".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 10".toLocalDate, Hours(37.0)),
        UsualHours("2020, 11, 17".toLocalDate, Hours(37.0)),
        UsualHours("2020, 11, 24".toLocalDate, Hours(37.0)),
        UsualHours("2020, 12, 1".toLocalDate, Hours(37.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 10".toLocalDate, Hours(10.0)),
        PartTimeHours("2020, 11, 17".toLocalDate, Hours(12.0)),
        PartTimeHours("2020, 11, 24".toLocalDate, Hours(10.0)),
        PartTimeHours("2020, 12, 1".toLocalDate, Hours(15.0)),
      ))
      -> 1362.66,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 2")
      .withClaimPeriodEnd("2020, 11, 29")
      .withFurloughStartDate("2020, 11, 6")
      .withFurloughEndDate("2020, 11, 27")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Weekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-03", "2020-11-10", "2020-11-17", "2020-11-24", "2020-12-01"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(1200.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(Period("2020, 11, 4".toLocalDate, "2020, 11, 10".toLocalDate),
          Period("2020, 11, 6".toLocalDate, "2020, 11, 10".toLocalDate)),
        PartialPeriod(Period("2020, 11, 25".toLocalDate, "2020, 12, 1".toLocalDate),
          Period("2020, 11, 25".toLocalDate, "2020, 11, 27".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 10".toLocalDate, Hours(37.0)),
        UsualHours("2020, 12, 1".toLocalDate, Hours(15.86))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 10".toLocalDate, Hours(10.0)),
        PartTimeHours("2020, 12, 1".toLocalDate, Hours(1.06)),
      ))
      -> 1691.23,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 15")
      .withFurloughEndDate("2020, 11, 27")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-14", "2020-11-21", "2020-11-28", "2020-12-05"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(600.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 15".toLocalDate, "2020, 11, 21".toLocalDate)),
        FullPeriod(Period("2020, 11, 22".toLocalDate, "2020, 11, 28".toLocalDate)),
        PartialPeriod(Period("2020, 11, 29".toLocalDate, "2020, 12, 5".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate))
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 21".toLocalDate, Hours(37.0)),
        UsualHours("2020, 11, 28".toLocalDate, Hours(37.0)),
        UsualHours("2020, 12, 5".toLocalDate, Hours(15.86))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 21".toLocalDate, Hours(10.0)),
        PartTimeHours("2020, 11, 28".toLocalDate, Hours(15.0)),
        PartTimeHours("2020, 12, 5".toLocalDate, Hours(1.06)),
      ))
      -> 763.66,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 29")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 29")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-28", "2020-12-05"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(550.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(Period("2020, 11, 29".toLocalDate, "2020, 12, 5".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate))
      ))
      .withUsualHours(List(
        UsualHours("2020, 12, 5".toLocalDate, Hours(40.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 5".toLocalDate, Hours(14.0))
      ))
      -> 81.71,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 21")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughEndDate("2020, 11, 8")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Weekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-07", "2020-11-14"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(600.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 7".toLocalDate)),
        PartialPeriod(Period("2020, 11, 8".toLocalDate, "2020, 11, 14".toLocalDate),
          Period("2020, 11, 8".toLocalDate, "2020, 11, 8".toLocalDate))
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 7".toLocalDate, Hours(37.0)),
        UsualHours("2020, 11, 14".toLocalDate, Hours(37.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 7".toLocalDate, Hours(10.0)),
        PartTimeHours("2020, 11, 14".toLocalDate, Hours(12.0))
      ))
      -> 396.60
  )

  val novemberTwoWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-14", "2020-11-28", "2020-12-12"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(650.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 14".toLocalDate)),
        FullPeriod(Period("2020, 11, 15".toLocalDate, "2020, 11, 28".toLocalDate)),
        PartialPeriod(Period("2020, 11, 29".toLocalDate, "2020, 12, 12".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 14".toLocalDate, Hours(98.0)),
        UsualHours("2020, 11, 28".toLocalDate, Hours(98.0)),
        UsualHours("2020, 12, 12".toLocalDate, Hours(21.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 14".toLocalDate, Hours(48.0)),
        PartTimeHours("2020, 11, 28".toLocalDate, Hours(48.0)),
        PartTimeHours("2020, 12, 12".toLocalDate, Hours(6.0))
      ))
      -> 583.66,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-14", "2020-11-28", "2020-12-12"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2300.12)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 14".toLocalDate)),
        FullPeriod(Period("2020, 11, 15".toLocalDate, "2020, 11, 28".toLocalDate)),
        PartialPeriod(Period("2020, 11, 29".toLocalDate, "2020, 12, 12".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 14".toLocalDate, Hours(98.0)),
        UsualHours("2020, 11, 28".toLocalDate, Hours(98.0)),
        UsualHours("2020, 12, 12".toLocalDate, Hours(21.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 14".toLocalDate, Hours(48.0)),
        PartTimeHours("2020, 11, 28".toLocalDate, Hours(48.0)),
        PartTimeHours("2020, 12, 12".toLocalDate, Hours(6.0))
      ))
      -> 1296.44,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 13")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-14", "2020-11-28", "2020-12-12"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(650.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(Period("2020, 11, 29".toLocalDate, "2020, 12, 12".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 12, 12".toLocalDate, Hours(21.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 12".toLocalDate, Hours(6.0))
      ))
      -> 647.35,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 03")
      .withClaimPeriodEnd("2020, 11, 11")
      .withFurloughStartDate("2020, 3, 1")
      .withFurloughEndDate("2020, 11, 11")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(FortNightly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-28", "2020-11-11"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(464.28)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(Period("2020, 10, 29".toLocalDate, "2020, 11, 11".toLocalDate),
          Period("2020, 11, 03".toLocalDate, "2020, 11, 11".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 11".toLocalDate, Hours(70.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 11".toLocalDate, Hours(43.0))
      ))
      -> 92.10,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 27")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 29")
      .withFurloughEndDate("2020, 11, 11")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-27", "2020-12-11"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(789.12)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(Period("2020, 11, 28".toLocalDate, "2020, 12, 11".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 12, 11".toLocalDate, Hours(140.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 12, 11".toLocalDate, Hours(50.0))
      ))
      -> 57.98,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 29")
      .withFurloughStartDate("2020, 11, 5")
      .withFurloughEndDate("2020, 11, 25")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(FortNightly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-14", "2020-11-28"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(643.12)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 14".toLocalDate),
          Period("2020, 11, 05".toLocalDate, "2020, 11, 14".toLocalDate)),
        PartialPeriod(Period("2020, 11, 15".toLocalDate, "2020, 11, 28".toLocalDate),
          Period("2020, 11, 15".toLocalDate, "2020, 11, 25".toLocalDate)),
      ))
      .withUsualHours(List(
        UsualHours("2020, 11, 14".toLocalDate, Hours(63.0)),
        UsualHours("2020, 11, 28".toLocalDate, Hours(77.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 14".toLocalDate, Hours(8.0)),
        PartTimeHours("2020, 11, 28".toLocalDate, Hours(12.0))
      ))
      -> 662.08
  )

  val novemberMonthlyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2400.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 30".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 11, 30".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 30".toLocalDate, Hours(40.0))))
      -> 1440.00,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(3126.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      -> 2500.00,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 05")
      .withFurloughEndDate("2020, 11, 21")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2400.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020, 11, 1".toLocalDate, "2020, 11, 30".toLocalDate),
        Period("2020, 11, 05".toLocalDate, "2020, 11, 21".toLocalDate)
      )))
      .withUsualHours(List(UsualHours("2020, 11, 30".toLocalDate, Hours(127.50))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 30".toLocalDate, Hours(52.50))))
      -> 640.00,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 05")
      .withFurloughEndDate("2020, 11, 21")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(6500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020, 11, 1".toLocalDate, "2020, 11, 30".toLocalDate),
        Period("2020, 11, 05".toLocalDate, "2020, 11, 21".toLocalDate)
      )))
      .withUsualHours(List(UsualHours("2020, 11, 30".toLocalDate, Hours(127.50))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 30".toLocalDate, Hours(52.50))))
      -> 833.40,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 02")
      .withFurloughEndDate("2020, 11, 11")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2400.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020, 11, 1".toLocalDate, "2020, 11, 30".toLocalDate),
        Period("2020, 11, 02".toLocalDate, "2020, 11, 11".toLocalDate)
      )))
      .withUsualHours(List(UsualHours("2020, 11, 30".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 30".toLocalDate, Hours(40.00))))
      -> 480.00,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 02")
      .withClaimPeriodEnd("2020, 11, 20")
      .withFurloughStartDate("2020, 11, 01")
      .withFurloughEndDate("2020, 11, 20")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-10-31", "2020-11-30"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(5555.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020, 11, 1".toLocalDate, "2020, 11, 30".toLocalDate),
        Period("2020, 11, 02".toLocalDate, "2020, 11, 20".toLocalDate)
      )))
      .withUsualHours(List(UsualHours("2020, 11, 30".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 30".toLocalDate, Hours(40.00))))
      -> 1187.60,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 01")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 03, 01")
      .withFurloughEndDate("2020, 11, 20")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2020-10-25", "2020-11-25", "2020-12-25"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(4900.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(PartialPeriod(
        Period("2020, 11, 26".toLocalDate, "2020, 12, 25".toLocalDate),
        Period("2020, 11, 26".toLocalDate, "2020, 11, 30".toLocalDate)
      )))
      .withUsualHours(List(
        UsualHours("2020, 11, 25".toLocalDate, Hours(160.0)),
        UsualHours("2020, 12, 25".toLocalDate, Hours(160.0))
      ))
      .withPartTimeHours(List(
        PartTimeHours("2020, 11, 25".toLocalDate, Hours(95.00)),
        PartTimeHours("2020, 12, 25".toLocalDate, Hours(95.00))
      ))
      -> 1015.70,
  )
  val novemberFourWeeklyScenarios: Seq[(UserAnswers, BigDecimal)] = Seq(
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 28")
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2000.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 28".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 11, 28".toLocalDate, Hours(148.0))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 28".toLocalDate, Hours(40.0))))
      -> 1167.57,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 28")
      .withPaymentFrequency(FourWeekly)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-04", "2020-11-01", "2020-11-29"))
      .withRegularPayAmount(3300.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      -> 2333.52,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 28")
      .withFurloughStartDate("2020, 11, 1")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28"))
      .withRegularPayAmount(3300.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      -> 2307.68,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 03, 01")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 30")
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28", "2020-12-26"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(3500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(FullPeriod(Period("2020, 11, 1".toLocalDate, "2020, 11, 28".toLocalDate)),
        PartialPeriod(
          Period("2020, 11, 29".toLocalDate, "2020, 12, 26".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 11, 28".toLocalDate, Hours(148.0)),
        UsualHours("2020, 12, 26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020, 11, 28".toLocalDate, Hours(40.0)),
        PartTimeHours("2020, 12, 26".toLocalDate, Hours(1.86))))
      -> 1831.11,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 30")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 29")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-11-29", "2020-12-27"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(2200.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 30".toLocalDate, "2020, 12, 27".toLocalDate),
          Period("2020, 11, 30".toLocalDate, "2020, 11, 30".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 12, 27".toLocalDate, Hours(148.0))))
      .withPartTimeHours(List(PartTimeHours("2020, 12, 27".toLocalDate, Hours(25.0))))
      -> 52.24,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 01")
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2020, 11, 30")
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28", "2020-12-26"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(3500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 29".toLocalDate, "2020, 12, 26".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 30".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 12, 26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020, 12, 26".toLocalDate, Hours(1.86))))
      -> 2454.81,
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 29")
      .withFurloughStartDate("2020, 11, 01")
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withPayMethod(PayMethod.Regular)
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2020-10-31", "2020-11-28", "2020-12-26"))
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withRegularPayAmount(3500.00)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withPartTimePeriods(List(
        PartialPeriod(
          Period("2020, 11, 29".toLocalDate, "2020, 12, 26".toLocalDate),
          Period("2020, 11, 29".toLocalDate, "2020, 11, 29".toLocalDate))))
      .withUsualHours(List(UsualHours("2020, 12, 26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2020, 12, 26".toLocalDate, Hours(1.86))))
      -> 2381.25
  )

  val scenarios: Seq[(UserAnswers, BigDecimal)] = {
    novemberFourWeeklyScenarios ++
      novemberMonthlyScenarios ++
      novemberTwoWeeklyScenarios ++
      novemberWeeklyScenarios ++
      novemberVariableFourWeeklyScenarios ++
      novemberVariableMonthlyScenarios ++
      novemberVariableTwoWeeklyScenarios
  }

  "GET /confirmation" should {

    "show the page" when {

      scenarios.zipWithIndex.foreach {
        case ((scenario, outcome), index) =>

          s"the user has answered the questions for scenario $index" in {
            val userAnswers: UserAnswers = scenario

            setAnswers(userAnswers)

            val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)

            whenReady(res) { result =>
              result should have(
                httpStatus(OK),
                titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK"),
                contentExists(s"${outcome.setScale(2).toString()}"),
              )
            }
          }
      }

//      "the user has answered the questions for regular journey" in {
//
//        val userAnswers: UserAnswers = dummyUserAnswers
//
//        setAnswers(userAnswers)
//
//        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)
//
//        whenReady(res) { result =>
//          result should have(
//            httpStatus(OK),
//            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK"),
//            contentExists(s"Total furlough grant for pay period = £")
//          )
//        }
//      }
//      "the user has answered the questions for dummyUserAnswersNoLastPayDate" in {
//
//        val userAnswers: UserAnswers = dummyUserAnswersNoLastPayDate
//
//        setAnswers(userAnswers)
//
//        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)
//
//        whenReady(res) { result =>
//          result should have(
//            httpStatus(OK),
//            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK")
//          )
//        }
//      }
//      "the user has answered the questions for variableMonthlyPartial" in {
//
//        val userAnswers: UserAnswers = variableMonthlyPartial
//
//        setAnswers(userAnswers)
//
//        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)
//
//        whenReady(res) { result =>
//          result should have(
//            httpStatus(OK),
//            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK")
//          )
//        }
//      }
//      "the user has answered the questions for phase 2" in {
//
//        val userAnswers: UserAnswers = phaseTwoJourney()
//
//        setAnswers(userAnswers)
//
//        val res = getRequestHeaders("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)
//
//        whenReady(res) { result =>
//          result should have(
//            httpStatus(OK),
//            titleOf("What you can claim for this employee - Job Retention Scheme calculator - GOV.UK")
//          )
//        }
//      }
//    }
//
//
//    "redirect to another page" when {
//
//      "the user has not answered the questions" in {
//
//        val userAnswers: UserAnswers = emptyUserAnswers
//
//        setAnswers(userAnswers)
//
//        val res = getRequest("/confirmation")("sessionId" -> userAnswers.id, "X-Session-ID" -> userAnswers.id)
//
//        //TODO Should redirect to reset or start again page
//        whenReady(res) { result =>
//          result should have(
//            httpStatus(SEE_OTHER),
//            redirectLocation("/job-retention-scheme-calculator/error")
//          )
//        }
//      }
    }
  }
}
