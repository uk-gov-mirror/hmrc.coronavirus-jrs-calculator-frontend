package controllers

import assets.BaseITConstants
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models._
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

object AprilConfirmationScenarios extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers
  with BaseITConstants with ITCoreTestData {
  val aprilVariableWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Variable Weekly Scenarios" -> Seq(
    emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-05-05")
      .withFurloughEndDate("2021-04-14")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-01".toLocalDate, "2021-04-07".toLocalDate), Period("2021-04-04".toLocalDate, "2021-04-07".toLocalDate)), FullPeriod(Period("2021-04-08".toLocalDate, "2021-04-14".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(19564.4)

      .withFurloughStartDate("2021-04-04")
      .withClaimPeriodEnd("2021-04-30")

      .withPayDate(List("2021-03-31", "2021-04-07", "2021-04-14"))

      .withUsualHours(List(UsualHours("2021-04-07".toLocalDate, Hours(40.0)), UsualHours("2021-04-14".toLocalDate, Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-07".toLocalDate, Hours(14.0)), PartTimeHours("2021-04-14".toLocalDate, Hours(15.0))))

      -> 351.48,
    emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)

      .withFurloughEndDate("2021-04-07")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List("2019-04-02" -> 420, "2019-04-09" -> 490))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-07".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(26000)

      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")

      .withPayDate(List("2021-03-31", "2021-04-07"))

      .withUsualHours(List(UsualHours("2021-04-07".toLocalDate, Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-07".toLocalDate, Hours(14.0))))

      -> 258.58, emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)

      .withFurloughEndDate("2021-04-24")
      .withPaymentFrequency(Weekly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2021-04-03")
      .withLastYear(List("2019-04-09" -> 420, "2019-04-16" -> 490, "2019-04-23" -> 560, "2019-04-30" -> 630))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)

      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(26000)

      .withFurloughStartDate("2021-04-04")
      .withClaimPeriodEnd("2021-04-28")

      .withPayDate(List("2021-03-31", "2021-04-07", "2021-04-14", "2021-04-21", "2021-04-28"))

      .withUsualHours(List())
      .withPartTimeHours(List())

      -> 1257.15
  ))

  val aprilVariableTwoWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Variable Two Weekly Scenarios" -> Seq(
    emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-08-27")
      .withFurloughEndDate("2021-04-30")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2021-04-13")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-01".toLocalDate, "2021-04-14".toLocalDate), Period("2021-04-13".toLocalDate, "2021-04-14".toLocalDate)), FullPeriod(Period("2021-04-15".toLocalDate, "2021-04-28".toLocalDate)), PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-12".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(34000)

      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")

      .withPayDate(List("2021-03-31", "2021-04-14", "2021-04-28", "2021-05-12"))

      .withUsualHours(List(UsualHours("2021-04-14".toLocalDate, Hours(40.0)), UsualHours("2021-04-28".toLocalDate, Hours(50.0)), UsualHours("2021-05-12".toLocalDate, Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-14".toLocalDate, Hours(14.0)), PartTimeHours("2021-04-28".toLocalDate, Hours(15.0)), PartTimeHours("2021-05-12".toLocalDate, Hours(15.0))))

      -> 1032.71, emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)

      .withFurloughEndDate("2021-04-28")
      .withPaymentFrequency(FortNightly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List("2019-04-16" -> 840, "2019-04-30" -> 980))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)

      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(28000)

      .withFurloughStartDate("2021-04-04")
      .withClaimPeriodEnd("2021-04-28")

      .withPayDate(List("2021-03-31", "2021-04-14", "2021-04-28"))

      .withUsualHours(List())
      .withPartTimeHours(List())

      -> 1530.00

  ))

  val aprilVariableMonthlyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Variable Monthly Scenarios" -> Seq(
    emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-08-01")
      .withFurloughEndDate("2021-04-30")
      .withPaymentFrequency(Monthly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(10000)

      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")

      .withPayDate(List("2021-03-31", "2021-04-30"))

      .withUsualHours(List(UsualHours("2021-04-30".toLocalDate, Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-30".toLocalDate, Hours(14.0))))

      -> 641.94, emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)

      .withFurloughEndDate("2021-04-30")
      .withPaymentFrequency(Monthly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List("2019-04-30" -> 2000))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)

      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(26000)

      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2021-04-30")

      .withPayDate(List("2021-03-31", "2021-04-30"))

      .withUsualHours(List())
      .withPartTimeHours(List())

      -> 1890.96
  ))

  val aprilVariableFourWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Variable Four Weekly Scenarios" -> Seq(
    emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withEmployeeStartDate("2020-10-29")
      .withFurloughEndDate("2021-04-28")
      .withPaymentFrequency(FourWeekly)
      .withEmployeeStartedAfter1Feb2019()
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-03-05".toLocalDate, "2021-04-01".toLocalDate), Period("2021-04-01".toLocalDate, "2021-04-01".toLocalDate)), PartialPeriod(Period("2021-04-02".toLocalDate, "2021-04-29".toLocalDate), Period("2021-04-02".toLocalDate, "2021-04-28".toLocalDate))))
      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withAnnualPayAmount(1500)

      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")

      .withPayDate(List("2021-03-04", "2021-04-01", "2021-04-29"))

      .withUsualHours(List(UsualHours("2021-04-01".toLocalDate, Hours(40.0)), UsualHours("2021-04-29".toLocalDate, Hours(50.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-01".toLocalDate, Hours(14.0)), PartTimeHours("2021-04-29".toLocalDate, Hours(15.0))))

      -> 152.33, emptyUserAnswers

      .withFurloughStatus(FurloughStatus.FurloughEnded)

      .withFurloughEndDate("2021-04-28")
      .withPaymentFrequency(FourWeekly)
      .withEmployeeStartedOnOrBefore1Feb2019()
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List("2019-04-30" -> 2800))
      .withFurloughInLastTaxYear(false)
      .withPayPeriodsList(PayPeriodsList.Yes)

      .withPayMethod(PayMethod.Variable)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withAnnualPayAmount(36000)

      .withFurloughStartDate("2021-04-04")
      .withClaimPeriodEnd("2021-04-28")

      .withPayDate(List("2021-03-31", "2021-04-28"))

      .withUsualHours(List())
      .withPartTimeHours(List())

      -> 2000.00
  ))

  val aprilWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Fixed Weekly Scenarios" -> Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-07".toLocalDate)), FullPeriod(Period("2021-04-08".toLocalDate, "2021-04-14".toLocalDate)), FullPeriod(Period("2021-04-15".toLocalDate, "2021-04-21".toLocalDate)), FullPeriod(Period("2021-04-22".toLocalDate, "2021-04-28".toLocalDate)), PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-05".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(600)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-07", "2021-04-14", "2021-04-21", "2021-04-28", "2021-05-05"))
      .withUsualHours(List(UsualHours("2021-04-07".toLocalDate, Hours(37.0)), UsualHours("2021-04-14".toLocalDate, Hours(37.0)), UsualHours("2021-04-21".toLocalDate, Hours(37.0)), UsualHours("2021-04-28".toLocalDate, Hours(37.0)), UsualHours("2021-05-05".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2021-04-07".toLocalDate, Hours(10.0)), PartTimeHours("2021-04-14".toLocalDate, Hours(12.0)), PartTimeHours("2021-04-21".toLocalDate, Hours(10.0)), PartTimeHours("2021-04-28".toLocalDate, Hours(15.0)), PartTimeHours("2021-05-05".toLocalDate, Hours(1.06))))
      -> 1438.26,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-27")
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2021-04-02")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-04".toLocalDate, "2021-04-10".toLocalDate), Period("2021-04-06".toLocalDate, "2021-04-10".toLocalDate)), PartialPeriod(Period("2021-04-25".toLocalDate, "2021-05-01".toLocalDate), Period("2021-04-25".toLocalDate, "2021-04-27".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(1200)
      .withFurloughStartDate("2021-04-06")
      .withClaimPeriodEnd("2021-04-29")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-04-03", "2021-04-10", "2021-04-17", "2021-04-24", "2021-05-01"))
      .withUsualHours(List(UsualHours("2021-04-10".toLocalDate, Hours(37.0)), UsualHours("2021-05-01".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2021-04-10".toLocalDate, Hours(10.0)), PartTimeHours("2021-05-01".toLocalDate, Hours(1.06))))
      -> 1691.23,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-15".toLocalDate, "2021-04-21".toLocalDate)), FullPeriod(Period("2021-04-22".toLocalDate, "2021-04-28".toLocalDate)), PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-05".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(600)
      .withFurloughStartDate("2021-04-15")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-04-14", "2021-04-21", "2021-04-28", "2021-05-05"))
      .withUsualHours(List(UsualHours("2021-04-21".toLocalDate, Hours(37.0)), UsualHours("2021-04-28".toLocalDate, Hours(37.0)), UsualHours("2021-05-05".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2021-04-21".toLocalDate, Hours(10.0)), PartTimeHours("2021-04-28".toLocalDate, Hours(15.0)), PartTimeHours("2021-05-05".toLocalDate, Hours(1.06))))
      -> 763.66,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2021-04-29")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-05".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(550)
      .withFurloughStartDate("2021-04-29")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-04-28", "2021-05-05"))
      .withUsualHours(List(UsualHours("2021-05-05".toLocalDate, Hours(40.0))))
      .withPartTimeHours(List(PartTimeHours("2021-05-05".toLocalDate, Hours(14.0))))
      -> 81.71,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-08")
      .withPaymentFrequency(Weekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-07".toLocalDate)), PartialPeriod(Period("2021-04-08".toLocalDate, "2021-04-14".toLocalDate), Period("2021-04-08".toLocalDate, "2021-04-08".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(600)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-21")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-07", "2021-04-14"))
      .withUsualHours(List(UsualHours("2021-04-07".toLocalDate, Hours(37.0)), UsualHours("2021-04-14".toLocalDate, Hours(37.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-07".toLocalDate, Hours(10.0)), PartTimeHours("2021-04-14".toLocalDate, Hours(12.0))))
      -> 396.60
  ))

  val aprilTwoWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Fixed Two Weekly Scenarios" -> Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-14".toLocalDate)), FullPeriod(Period("2021-04-15".toLocalDate, "2021-04-28".toLocalDate)), PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-12".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(650)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-14", "2021-04-28", "2021-05-12"))
      .withUsualHours(List(UsualHours("2021-04-14".toLocalDate, Hours(98.0)), UsualHours("2021-04-28".toLocalDate, Hours(98.0)), UsualHours("2021-05-12".toLocalDate, Hours(21.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-14".toLocalDate, Hours(48.0)), PartTimeHours("2021-04-28".toLocalDate, Hours(48.0)), PartTimeHours("2021-05-12".toLocalDate, Hours(6.0))))
      -> 583.66,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-14".toLocalDate)), FullPeriod(Period("2021-04-15".toLocalDate, "2021-04-28".toLocalDate)), PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-12".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2300.12)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-14", "2021-04-28", "2021-05-12"))
      .withUsualHours(List(UsualHours("2021-04-14".toLocalDate, Hours(98.0)), UsualHours("2021-04-28".toLocalDate, Hours(98.0)), UsualHours("2021-05-12".toLocalDate, Hours(21.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-14".toLocalDate, Hours(48.0)), PartTimeHours("2021-04-28".toLocalDate, Hours(48.0)), PartTimeHours("2021-05-12".toLocalDate, Hours(6.0))))
      -> 1296.44,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2021-04-13")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-12".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(650)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-14", "2021-04-28", "2021-05-12"))
      .withUsualHours(List(UsualHours("2021-05-12".toLocalDate, Hours(21.0))))
      .withPartTimeHours(List(PartTimeHours("2021-05-12".toLocalDate, Hours(6.0))))
      -> 647.35,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-11")
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2021-04-03")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-03-29".toLocalDate, "2021-04-11".toLocalDate), Period("2021-04-03".toLocalDate, "2021-04-11".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(464.28)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2021-04-11")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-28", "2021-04-11"))
      .withUsualHours(List(UsualHours("2021-04-11".toLocalDate, Hours(70.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-11".toLocalDate, Hours(43.0))))
      -> 92.10,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2021-04-27")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-28".toLocalDate, "2021-05-11".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(789.12)
      .withFurloughStartDate("2021-04-29")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-04-27", "2021-05-11"))
      .withUsualHours(List(UsualHours("2021-05-11".toLocalDate, Hours(140.0))))
      .withPartTimeHours(List(PartTimeHours("2021-05-11".toLocalDate, Hours(50.0))))
      -> 57.98,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-25")
      .withPaymentFrequency(FortNightly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-01".toLocalDate, "2021-04-14".toLocalDate), Period("2021-04-05".toLocalDate, "2021-04-14".toLocalDate)), PartialPeriod(Period("2021-04-15".toLocalDate, "2021-04-28".toLocalDate), Period("2021-04-15".toLocalDate, "2021-04-25".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(643.12)
      .withFurloughStartDate("2021-04-05")
      .withClaimPeriodEnd("2021-04-29")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-14", "2021-04-28"))
      .withUsualHours(List(UsualHours("2021-04-14".toLocalDate, Hours(63.0)), UsualHours("2021-04-28".toLocalDate, Hours(77.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-14".toLocalDate, Hours(8.0)), PartTimeHours("2021-04-28".toLocalDate, Hours(12.0))))
      -> 662.08
  ))

  val aprilMonthlyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Fixed Monthly Scenarios" -> Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2400)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2021-03-31", "2021-04-30"))
      .withUsualHours(List(UsualHours("2021-04-30".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-30".toLocalDate, Hours(40.0))))
      -> 1440.00,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withRegularPayAmount(3126)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2021-03-31", "2021-04-30"))
      .withUsualHours(List())
      .withPartTimeHours(List())
      -> 2500.00,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-21")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-01".toLocalDate, "2021-04-30".toLocalDate), Period("2021-04-05".toLocalDate, "2021-04-21".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2400)
      .withFurloughStartDate("2021-04-05")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2021-03-31", "2021-04-30"))
      .withUsualHours(List(UsualHours("2021-04-30".toLocalDate, Hours(127.5))))
      .withPartTimeHours(List(PartTimeHours("2021-04-30".toLocalDate, Hours(52.5))))
      -> 640.00,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-21")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-01".toLocalDate, "2021-04-30".toLocalDate), Period("2021-04-05".toLocalDate, "2021-04-21".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(6500)
      .withFurloughStartDate("2021-04-05")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2021-03-31", "2021-04-30"))
      .withUsualHours(List(UsualHours("2021-04-30".toLocalDate, Hours(127.5))))
      .withPartTimeHours(List(PartTimeHours("2021-04-30".toLocalDate, Hours(52.5))))
      -> 833.40,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-11")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-01".toLocalDate, "2021-04-30".toLocalDate), Period("2021-04-02".toLocalDate, "2021-04-11".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2400)
      .withFurloughStartDate("2021-04-02")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2021-03-31", "2021-04-30"))
      .withUsualHours(List(UsualHours("2021-04-30".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-30".toLocalDate, Hours(40.0))))
      -> 480.00,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-20")
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2021-04-02")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-01".toLocalDate, "2021-04-30".toLocalDate), Period("2021-04-02".toLocalDate, "2021-04-20".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(5555)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-20")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2021-03-31", "2021-04-30"))
      .withUsualHours(List(UsualHours("2021-04-30".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-30".toLocalDate, Hours(40.0))))
      -> 1187.60,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-03-26".toLocalDate, "2021-04-25".toLocalDate), Period("2021-04-01".toLocalDate, "2021-04-25".toLocalDate)), PartialPeriod(Period("2021-04-26".toLocalDate, "2021-05-25".toLocalDate), Period("2021-04-26".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(4900)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.No)
      .withPayDate(List("2021-03-25", "2021-04-25", "2021-05-25"))
      .withUsualHours(List(UsualHours("2021-04-25".toLocalDate, Hours(160.0)), UsualHours("2021-05-25".toLocalDate, Hours(160.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-25".toLocalDate, Hours(95.0)), PartTimeHours("2021-05-25".toLocalDate, Hours(95.0))))
      -> 1015.70
  ))

  val aprilFourWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq("April Fixed Four Weekly Scenarios" -> Seq(
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-28")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-28".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2000)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-28"))
      .withUsualHours(List(UsualHours("2021-04-28".toLocalDate, Hours(148.0))))
      .withPartTimeHours(List(PartTimeHours("2021-04-28".toLocalDate, Hours(40.0))))
      -> 1167.57,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-28")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withRegularPayAmount(3300)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-04", "2021-04-01", "2021-04-29"))
      .withUsualHours(List())
      .withPartTimeHours(List())
      -> 2333.52,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
      .withRegularPayAmount(3300)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-28")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-28"))
      .withUsualHours(List())
      .withPartTimeHours(List())
      -> 2307.68,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-30")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(FullPeriod(Period("2021-04-01".toLocalDate, "2021-04-28".toLocalDate)), PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-26".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(3500)
      .withFurloughStartDate("2020-03-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-28", "2021-05-26"))
      .withUsualHours(List(UsualHours("2021-04-28".toLocalDate, Hours(148.0)), UsualHours("2021-05-26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2021-04-28".toLocalDate, Hours(40.0)), PartTimeHours("2021-05-26".toLocalDate, Hours(1.86))))
      -> 1831.11,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2021-04-30")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-30".toLocalDate, "2021-05-27".toLocalDate), Period("2021-04-30".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(2200)
      .withFurloughStartDate("2021-04-29")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-04-29", "2021-05-27"))
      .withUsualHours(List(UsualHours("2021-05-27".toLocalDate, Hours(148.0))))
      .withPartTimeHours(List(PartTimeHours("2021-05-27".toLocalDate, Hours(25.0))))
      -> 52.24,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughEnded)
      .withFurloughEndDate("2021-04-30")
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-26".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-30".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(3500)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-30")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-28", "2021-05-26"))
      .withUsualHours(List(UsualHours("2021-05-26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2021-05-26".toLocalDate, Hours(1.86))))
      -> 2454.81,
    emptyUserAnswers
      .withFurloughStatus(FurloughStatus.FurloughOngoing)
      .withPaymentFrequency(FourWeekly)
      .withClaimPeriodStart("2021-04-01")
      .withLastYear(List())
      .withPayPeriodsList(PayPeriodsList.Yes)
      .withPartTimePeriods(List(PartialPeriod(Period("2021-04-29".toLocalDate, "2021-05-26".toLocalDate), Period("2021-04-29".toLocalDate, "2021-04-29".toLocalDate))))
      .withPayMethod(PayMethod.Regular)
      .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
      .withRegularPayAmount(3500)
      .withFurloughStartDate("2021-04-01")
      .withClaimPeriodEnd("2021-04-29")
      .withRegularLengthEmployed(RegularLengthEmployed.Yes)
      .withPayDate(List("2021-03-31", "2021-04-28", "2021-05-26"))
      .withUsualHours(List(UsualHours("2021-05-26".toLocalDate, Hours(15.86))))
      .withPartTimeHours(List(PartTimeHours("2021-05-26".toLocalDate, Hours(1.86))))
      -> 2381.25
  ))
}
