package controllers.scenarios

import assets.BaseITConstants
import models.PaymentFrequency._
import models._
import utils.{CreateRequestHelper, CustomMatchers, ITCoreTestData, IntegrationSpecBase}

object JuneConfirmationScenarios
    extends IntegrationSpecBase with CreateRequestHelper with CustomMatchers with BaseITConstants with ITCoreTestData {

  val juneFixedFourWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Fixed Four Weekly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-26".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List(UsualHours("2021-06-28".toLocalDate, Hours(148.0)), UsualHours("2021-07-26".toLocalDate, Hours(15.86))))
        .withPartTimeHours(List(PartTimeHours("2021-06-28".toLocalDate, Hours(40.0)), PartTimeHours("2021-07-26".toLocalDate, Hours(1.86))))
        -> 1268.46,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withRegularPayAmount(3300)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 2474.36,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withRegularPayAmount(3300)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-28")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-28"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 2307.68,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-26".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(3500)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List(UsualHours("2021-06-28".toLocalDate, Hours(148.0)), UsualHours("2021-07-26".toLocalDate, Hours(15.86))))
        .withPartTimeHours(List(PartTimeHours("2021-06-28".toLocalDate, Hours(40.0)), PartTimeHours("2021-07-26".toLocalDate, Hours(1.86))))
        -> 1831.11,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-29")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-27".toLocalDate, "2021-07-24".toLocalDate),
                                                Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2200)
        .withFurloughStartDate("2021-06-29")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-06-26", "2021-07-24"))
        .withUsualHours(List(UsualHours("2021-07-24".toLocalDate, Hours(148.0))))
        .withPartTimeHours(List(PartTimeHours("2021-07-24".toLocalDate, Hours(25.0))))
        -> 104.48,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-26".toLocalDate),
                                                Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(3500)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List(UsualHours("2021-07-26".toLocalDate, Hours(15.86))))
        .withPartTimeHours(List(PartTimeHours("2021-07-26".toLocalDate, Hours(1.86))))
        -> 2454.81,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-26".toLocalDate),
                                                Period("2021-06-29".toLocalDate, "2021-06-29".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(3500)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-29")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List(UsualHours("2021-07-26".toLocalDate, Hours(15.86))))
        .withPartTimeHours(List(PartTimeHours("2021-07-26".toLocalDate, Hours(1.86))))
        -> 2381.25,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-06".toLocalDate, "2021-06-02".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2654.11)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-05", "2021-06-02"))
        .withUsualHours(List(UsualHours("2021-06-02".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-02".toLocalDate, Hours(11.4))))
        -> 101.55,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-02")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-06".toLocalDate, "2021-06-02".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(3200.11)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-05", "2021-06-02"))
        .withUsualHours(List(UsualHours("2021-06-02".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-02".toLocalDate, Hours(11.4))))
        -> 111.60,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-01")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-10".toLocalDate, "2021-06-06".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2322.11)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-09", "2021-06-06"))
        .withUsualHours(List(UsualHours("2021-06-06".toLocalDate, Hours(10.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-06".toLocalDate, Hours(6.5))))
        -> 23.22,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-02")
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-05-05".toLocalDate, "2021-06-01".toLocalDate),
                        Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate)),
          PartialPeriod(Period("2021-06-02".toLocalDate, "2021-06-29".toLocalDate),
                        Period("2021-06-02".toLocalDate, "2021-06-02".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1465.55)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-04", "2021-06-01", "2021-06-29"))
        .withUsualHours(List(UsualHours("2021-06-01".toLocalDate, Hours(7.5)), UsualHours("2021-06-29".toLocalDate, Hours(7.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-01".toLocalDate, Hours(3.0)), PartTimeHours("2021-06-29".toLocalDate, Hours(3.0))))
        -> 50.24,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(FourWeekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-26".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List(UsualHours("2021-06-28".toLocalDate, Hours(148.0)), UsualHours("2021-07-26".toLocalDate, Hours(15.86))))
        .withPartTimeHours(List(PartTimeHours("2021-06-28".toLocalDate, Hours(40.0)), PartTimeHours("2021-07-26".toLocalDate, Hours(1.86))))
        -> 1268.46
    ))

  val juneFixedMonthlyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Fixed Monthly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2400)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(160.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(40.0))))
        -> 1440.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withRegularPayAmount(3126)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 2500.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-21")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate),
                                                Period("2021-06-05".toLocalDate, "2021-06-21".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2400)
        .withFurloughStartDate("2021-06-05")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(127.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(52.5))))
        -> 640.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-21")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate),
                                                Period("2021-06-05".toLocalDate, "2021-06-21".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(6500)
        .withFurloughStartDate("2021-06-05")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(127.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(52.5))))
        -> 833.40,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-11")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate),
                                                Period("2021-06-02".toLocalDate, "2021-06-11".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2400)
        .withFurloughStartDate("2021-06-02")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(160.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(40.0))))
        -> 480.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-20")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-02")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate),
                                                Period("2021-06-02".toLocalDate, "2021-06-20".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(5555)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-20")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(160.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(40.0))))
        -> 1187.60,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-05-26".toLocalDate, "2021-06-25".toLocalDate),
                        Period("2021-06-01".toLocalDate, "2021-06-25".toLocalDate)),
          PartialPeriod(Period("2021-06-26".toLocalDate, "2021-07-25".toLocalDate),
                        Period("2021-06-26".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(4900)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-25", "2021-06-25", "2021-07-25"))
        .withUsualHours(List(UsualHours("2021-06-25".toLocalDate, Hours(160.0)), UsualHours("2021-07-25".toLocalDate, Hours(160.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-25".toLocalDate, Hours(95.0)), PartTimeHours("2021-07-25".toLocalDate, Hours(95.0))))
        -> 1015.70,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2654.11)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(11.4))))
        -> 94.78,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-27".toLocalDate, "2021-06-25".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2654.11)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-26", "2021-06-25"))
        .withUsualHours(List(UsualHours("2021-06-25".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-25".toLocalDate, Hours(11.4))))
        -> 94.78,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-02")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-03".toLocalDate, "2021-06-02".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(3200.11)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-02", "2021-06-02"))
        .withUsualHours(List(UsualHours("2021-06-02".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-02".toLocalDate, Hours(11.4))))
        -> 110.59,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-01")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-10".toLocalDate, "2021-06-08".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2322.11)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-09", "2021-06-08"))
        .withUsualHours(List(UsualHours("2021-06-08".toLocalDate, Hours(10.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-08".toLocalDate, Hours(6.5))))
        -> 21.67,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-02")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-05-02".toLocalDate, "2021-06-01".toLocalDate),
                        Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate)),
          PartialPeriod(Period("2021-06-02".toLocalDate, "2021-07-01".toLocalDate),
                        Period("2021-06-02".toLocalDate, "2021-06-02".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1465.55)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-01", "2021-06-01", "2021-07-01"))
        .withUsualHours(List(UsualHours("2021-06-01".toLocalDate, Hours(7.5)), UsualHours("2021-07-01".toLocalDate, Hours(7.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-01".toLocalDate, Hours(3.0)), PartTimeHours("2021-07-01".toLocalDate, Hours(3.0))))
        -> 46.15,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(3222)
        .withFurloughStartDate("2021-05-31")
        .withClaimPeriodEnd("2021-06-01")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(7.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(3.0))))
        -> 50.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPaymentFrequency(Monthly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2400)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(160.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(40.0))))
        -> 1440.00
    ))

  val juneFixedTwoWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Fixed Two Weekly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate)),
          FullPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(650)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(98.0)),
                             UsualHours("2021-06-28".toLocalDate, Hours(98.0)),
                             UsualHours("2021-07-12".toLocalDate, Hours(21.0))))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-14".toLocalDate, Hours(48.0)),
          PartTimeHours("2021-06-28".toLocalDate, Hours(48.0)),
          PartTimeHours("2021-07-12".toLocalDate, Hours(6.0))
        ))
        -> 583.66,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate)),
          FullPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(2300.12)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(98.0)),
                             UsualHours("2021-06-28".toLocalDate, Hours(98.0)),
                             UsualHours("2021-07-12".toLocalDate, Hours(21.0))))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-14".toLocalDate, Hours(48.0)),
          PartTimeHours("2021-06-28".toLocalDate, Hours(48.0)),
          PartTimeHours("2021-07-12".toLocalDate, Hours(6.0))
        ))
        -> 1296.44,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-13")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                                                Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(650)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-07-12".toLocalDate, Hours(21.0))))
        .withPartTimeHours(List(PartTimeHours("2021-07-12".toLocalDate, Hours(6.0))))
        -> 647.35,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-11")
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-03")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-29".toLocalDate, "2021-06-11".toLocalDate),
                                                Period("2021-06-03".toLocalDate, "2021-06-11".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(464.28)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-11")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-28", "2021-06-11"))
        .withUsualHours(List(UsualHours("2021-06-11".toLocalDate, Hours(70.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-11".toLocalDate, Hours(43.0))))
        -> 92.10,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-28")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                                                Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(789.12)
        .withFurloughStartDate("2021-06-29")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-07-12".toLocalDate, Hours(140.0))))
        .withPartTimeHours(List(PartTimeHours("2021-07-12".toLocalDate, Hours(50.0))))
        -> 57.98,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-25")
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate),
                        Period("2021-06-05".toLocalDate, "2021-06-14".toLocalDate)),
          PartialPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate),
                        Period("2021-06-15".toLocalDate, "2021-06-25".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(643.12)
        .withFurloughStartDate("2021-06-05")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(63.0)), UsualHours("2021-06-28".toLocalDate, Hours(77.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-14".toLocalDate, Hours(8.0)), PartTimeHours("2021-06-28".toLocalDate, Hours(12.0))))
        -> 662.08,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-20".toLocalDate, "2021-06-02".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1000.34)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-19", "2021-06-02"))
        .withUsualHours(List(UsualHours("2021-06-02".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-02".toLocalDate, Hours(11.4))))
        -> 76.55,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-21".toLocalDate, "2021-06-03".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1500)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-20", "2021-06-03"))
        .withUsualHours(List(UsualHours("2021-06-03".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-03".toLocalDate, Hours(11.4))))
        -> 111.60,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-01")
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-24".toLocalDate, "2021-06-06".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1010.11)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-23", "2021-06-06"))
        .withUsualHours(List(UsualHours("2021-06-06".toLocalDate, Hours(10.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-06".toLocalDate, Hours(6.5))))
        -> 20.20,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-02")
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-05-19".toLocalDate, "2021-06-01".toLocalDate),
                        Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate)),
          PartialPeriod(Period("2021-06-02".toLocalDate, "2021-06-15".toLocalDate),
                        Period("2021-06-02".toLocalDate, "2021-06-02".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1465.55)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-18", "2021-06-01", "2021-06-15"))
        .withUsualHours(List(UsualHours("2021-06-01".toLocalDate, Hours(7.5)), UsualHours("2021-06-15".toLocalDate, Hours(7.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-01".toLocalDate, Hours(3.0)), PartTimeHours("2021-06-15".toLocalDate, Hours(3.0))))
        -> 100.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(FortNightly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate)),
          FullPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(650)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(98.0)),
                             UsualHours("2021-06-28".toLocalDate, Hours(98.0)),
                             UsualHours("2021-07-12".toLocalDate, Hours(21.0))))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-14".toLocalDate, Hours(48.0)),
          PartTimeHours("2021-06-28".toLocalDate, Hours(48.0)),
          PartTimeHours("2021-07-12".toLocalDate, Hours(6.0))
        ))
        -> 583.66
    ))

  val juneFixedWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Fixed Weekly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-12")
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-06")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-06".toLocalDate, "2021-06-12".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(550)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-12")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-06-05", "2021-06-12"))
        .withUsualHours(List(UsualHours("2021-06-12".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-12".toLocalDate, Hours(14.0))))
        -> 286.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate)),
          FullPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate)),
          FullPeriod(Period("2021-06-15".toLocalDate, "2021-06-21".toLocalDate)),
          FullPeriod(Period("2021-06-22".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-05".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(600)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14", "2021-06-21", "2021-06-28", "2021-07-05"))
        .withUsualHours(List(
          UsualHours("2021-06-07".toLocalDate, Hours(37.0)),
          UsualHours("2021-06-14".toLocalDate, Hours(37.0)),
          UsualHours("2021-06-21".toLocalDate, Hours(37.0)),
          UsualHours("2021-06-28".toLocalDate, Hours(37.0)),
          UsualHours("2021-07-05".toLocalDate, Hours(15.86))
        ))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-07".toLocalDate, Hours(10.0)),
          PartTimeHours("2021-06-14".toLocalDate, Hours(12.0)),
          PartTimeHours("2021-06-21".toLocalDate, Hours(10.0)),
          PartTimeHours("2021-06-28".toLocalDate, Hours(15.0)),
          PartTimeHours("2021-07-05".toLocalDate, Hours(1.06))
        ))
        -> 1438.26,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-02")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-04".toLocalDate, "2021-06-10".toLocalDate),
                        Period("2021-06-06".toLocalDate, "2021-06-10".toLocalDate)),
          FullPeriod(Period("2021-06-11".toLocalDate, "2021-06-17".toLocalDate)),
          FullPeriod(Period("2021-06-18".toLocalDate, "2021-06-24".toLocalDate)),
          PartialPeriod(Period("2021-06-25".toLocalDate, "2021-07-01".toLocalDate),
                        Period("2021-06-25".toLocalDate, "2021-06-29".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1200)
        .withFurloughStartDate("2021-06-06")
        .withClaimPeriodEnd("2021-06-29")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-06-03", "2021-06-10", "2021-06-17", "2021-06-24", "2021-07-01"))
        .withUsualHours(List(
          UsualHours("2021-06-10".toLocalDate, Hours(37.0)),
          UsualHours("2021-06-17".toLocalDate, Hours(37.0)),
          UsualHours("2021-06-24".toLocalDate, Hours(37.0)),
          UsualHours("2021-07-01".toLocalDate, Hours(37.0))
        ))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-10".toLocalDate, Hours(10.0)),
          PartTimeHours("2021-06-17".toLocalDate, Hours(12.0)),
          PartTimeHours("2021-06-24".toLocalDate, Hours(10.0)),
          PartTimeHours("2021-07-01".toLocalDate, Hours(15.0))
        ))
        -> 1362.66,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-27")
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-02")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-04".toLocalDate, "2021-06-10".toLocalDate),
                        Period("2021-06-06".toLocalDate, "2021-06-10".toLocalDate)),
          PartialPeriod(Period("2021-06-25".toLocalDate, "2021-07-01".toLocalDate),
                        Period("2021-06-25".toLocalDate, "2021-06-27".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(1200)
        .withFurloughStartDate("2021-06-06")
        .withClaimPeriodEnd("2021-06-29")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-06-03", "2021-06-10", "2021-06-17", "2021-06-24", "2021-07-01"))
        .withUsualHours(List(UsualHours("2021-06-10".toLocalDate, Hours(37.0)), UsualHours("2021-07-01".toLocalDate, Hours(15.86))))
        .withPartTimeHours(List(PartTimeHours("2021-06-10".toLocalDate, Hours(10.0)), PartTimeHours("2021-07-01".toLocalDate, Hours(1.06))))
        -> 1691.23,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-14".toLocalDate, "2021-06-20".toLocalDate)),
          FullPeriod(Period("2021-06-21".toLocalDate, "2021-06-27".toLocalDate)),
          PartialPeriod(Period("2021-06-28".toLocalDate, "2021-07-04".toLocalDate),
                        Period("2021-06-28".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(600)
        .withFurloughStartDate("2021-06-14")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-06-13", "2021-06-20", "2021-06-27", "2021-07-04"))
        .withUsualHours(List(UsualHours("2021-06-20".toLocalDate, Hours(37.0)),
                             UsualHours("2021-06-27".toLocalDate, Hours(37.0)),
                             UsualHours("2021-07-04".toLocalDate, Hours(15.86))))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-20".toLocalDate, Hours(10.0)),
          PartTimeHours("2021-06-27".toLocalDate, Hours(15.0)),
          PartTimeHours("2021-07-04".toLocalDate, Hours(1.06))
        ))
        -> 827.64,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-29")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-05".toLocalDate),
                                                Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(550)
        .withFurloughStartDate("2021-06-29")
        .withClaimPeriodEnd("2021-06-30")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-06-28", "2021-07-05"))
        .withUsualHours(List(UsualHours("2021-07-05".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-07-05".toLocalDate, Hours(14.0))))
        -> 81.71,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-08")
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate)),
          PartialPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate),
                        Period("2021-06-08".toLocalDate, "2021-06-08".toLocalDate))
        ))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(600)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-21")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(37.0)), UsualHours("2021-06-14".toLocalDate, Hours(37.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(10.0)), PartTimeHours("2021-06-14".toLocalDate, Hours(12.0))))
        -> 396.60,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughOngoing)
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-29".toLocalDate, "2021-06-04".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(666.12)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-28", "2021-06-04"))
        .withUsualHours(List(UsualHours("2021-06-04".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-04".toLocalDate, Hours(11.4))))
        -> 101.94,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-02")
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-29".toLocalDate, "2021-06-04".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-02".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(890.11)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-28", "2021-06-04"))
        .withUsualHours(List(UsualHours("2021-06-04".toLocalDate, Hours(34.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-04".toLocalDate, Hours(11.4))))
        -> 111.60,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-01")
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-30".toLocalDate, "2021-06-05".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(500)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-29", "2021-06-05"))
        .withUsualHours(List(UsualHours("2021-06-05".toLocalDate, Hours(10.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-05".toLocalDate, Hours(6.5))))
        -> 20.00,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-02")
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-05-26".toLocalDate, "2021-06-01".toLocalDate),
                                                Period("2021-06-01".toLocalDate, "2021-06-01".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(754.44)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-02")
        .withRegularLengthEmployed(RegularLengthEmployed.Yes)
        .withPayDate(List("2021-05-25", "2021-06-01", "2021-06-08"))
        .withUsualHours(List(UsualHours("2021-06-01".toLocalDate, Hours(7.5))))
        .withPartTimeHours(List(PartTimeHours("2021-06-01".toLocalDate, Hours(3.0))))
        -> 133.34,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-12")
        .withOnPayrollBefore30thOct2020(true)
        .withPaymentFrequency(Weekly)
        .withClaimPeriodStart("2021-06-06")
        .withLastYear(List())
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-06".toLocalDate, "2021-06-12".toLocalDate))))
        .withPayMethod(PayMethod.Regular)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withRegularPayAmount(550)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-12")
        .withRegularLengthEmployed(RegularLengthEmployed.No)
        .withPayDate(List("2021-06-05", "2021-06-12"))
        .withUsualHours(List(UsualHours("2021-06-12".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-12".toLocalDate, Hours(14.0))))
        -> 286.00
    ))

  val juneVariableFourWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Variable Four Weekly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2021-03-01")
        .withFurloughEndDate("2021-06-29")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FourWeekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-05-30".toLocalDate, "2021-06-26".toLocalDate),
                        Period("2021-06-01".toLocalDate, "2021-06-26".toLocalDate)),
          PartialPeriod(Period("2021-06-27".toLocalDate, "2021-07-24".toLocalDate),
                        Period("2021-06-27".toLocalDate, "2021-06-29".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(1500)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-29", "2021-06-26", "2021-07-24"))
        .withUsualHours(List(UsualHours("2021-06-26".toLocalDate, Hours(40.0)), UsualHours("2021-07-24".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-26".toLocalDate, Hours(14.0)), PartTimeHours("2021-07-24".toLocalDate, Hours(15.0))))
        -> 247.76,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-09-05")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(true)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FourWeekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-30")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(PartialPeriod(Period("2021-06-30".toLocalDate, "2021-07-27".toLocalDate),
                                                Period("2021-06-30".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(19564.4)
        .withFurloughStartDate("2021-06-29")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-06-29", "2021-07-27"))
        .withUsualHours(List(UsualHours("2021-07-27".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-07-27".toLocalDate, Hours(14.0))))
        -> 34.26,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-28")
        .withPaymentFrequency(FourWeekly)
        .withEmployeeStartedOnOrBefore1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List("2019-06-23" -> 2800, "2019-07-21" -> 200))
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withAnnualPayAmount(36000)
        .withFurloughStartDate("2021-06-04")
        .withClaimPeriodEnd("2021-06-28")
        .withPayDate(List("2021-05-23", "2021-06-20", "2021-07-18"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 1989.50,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-25")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FourWeekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-26".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(26000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List(UsualHours("2021-06-28".toLocalDate, Hours(40.0)), UsualHours("2021-07-26".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-28".toLocalDate, Hours(14.0)), PartTimeHours("2021-07-26".toLocalDate, Hours(14.0))))
        -> 1608.33,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-25")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FourWeekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withStatutoryLeavePay(Amount(9243))
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-26".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(26000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-28", "2021-07-26"))
        .withUsualHours(List(UsualHours("2021-06-28".toLocalDate, Hours(40.0)), UsualHours("2021-07-26".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-28".toLocalDate, Hours(14.0)), PartTimeHours("2021-07-26".toLocalDate, Hours(14.0))))
        -> 1608.33
    ))

  val juneVariableMonthlyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Variable Monthly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-29")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Monthly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(10000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(14.0))))
        -> 1013.06,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-09-04")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(true)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Monthly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-30".toLocalDate))))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(10000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List(UsualHours("2021-06-30".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-30".toLocalDate, Hours(14.0))))
        -> 577.82,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-30")
        .withPaymentFrequency(Monthly)
        .withEmployeeStartedOnOrBefore1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List("2019-06-10" -> 300, "2019-07-10" -> 2000))
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withAnnualPayAmount(20000)
        .withFurloughStartDate("2021-05-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-10", "2021-06-10", "2021-07-10"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 1503.78,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-26")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Monthly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withAnnualPayAmount(12000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 1834.32,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-26")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Monthly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withAnnualPayAmount(12000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-30"))
        .withStatutoryLeaveData(60, 5714.4)
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 1555.20
    ))

  val juneVariableTwoWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Variable Two Weekly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2021-01-24")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FortNightly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-13")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate),
                        Period("2021-06-13".toLocalDate, "2021-06-14".toLocalDate)),
          FullPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(34000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(40.0)),
                             UsualHours("2021-06-28".toLocalDate, Hours(50.0)),
                             UsualHours("2021-07-12".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-14".toLocalDate, Hours(14.0)),
          PartTimeHours("2021-06-28".toLocalDate, Hours(15.0)),
          PartTimeHours("2021-07-12".toLocalDate, Hours(15.0))
        ))
        -> 1032.71,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-09-04")
        .withFurloughEndDate("2021-06-25")
        .withOnPayrollBefore30thOct2020(true)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FortNightly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate),
                        Period("2021-06-05".toLocalDate, "2021-06-14".toLocalDate)),
          PartialPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate),
                        Period("2021-06-15".toLocalDate, "2021-06-25".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(19564.4)
        .withFurloughStartDate("2021-06-05")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(40.0)), UsualHours("2021-06-28".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-14".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-28".toLocalDate, Hours(15.0))))
        -> 811.10,
      emptyUserAnswers
        .withRtiSubmission(EmployeeRTISubmission.Yes)
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-03-19")
        .withFurloughEndDate("2021-06-25")
        .withPaymentFrequency(FortNightly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate),
                        Period("2021-06-05".toLocalDate, "2021-06-14".toLocalDate)),
          PartialPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate),
                        Period("2021-06-15".toLocalDate, "2021-06-25".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(1000)
        .withFurloughStartDate("2021-06-05")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(40.0)), UsualHours("2021-06-28".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-14".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-28".toLocalDate, Hours(15.0))))
        -> 631.16,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-28")
        .withPaymentFrequency(FortNightly)
        .withEmployeeStartedOnOrBefore1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List("2019-06-17" -> 840, "2019-07-01" -> 980))
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withAnnualPayAmount(30000)
        .withFurloughStartDate("2021-06-04")
        .withClaimPeriodEnd("2021-06-28")
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 1639.40,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-25")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FortNightly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate)),
          FullPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(19564.4)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(40.0)),
                             UsualHours("2021-06-28".toLocalDate, Hours(50.0)),
                             UsualHours("2021-07-12".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-14".toLocalDate, Hours(14.0)),
          PartTimeHours("2021-06-28".toLocalDate, Hours(15.0)),
          PartTimeHours("2021-07-12".toLocalDate, Hours(15.0))
        ))
        -> 1674.37,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-25")
        .withFurloughEndDate("2021-06-30")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(FortNightly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withStatutoryLeavePay(Amount(9243))
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-14".toLocalDate)),
          FullPeriod(Period("2021-06-15".toLocalDate, "2021-06-28".toLocalDate)),
          PartialPeriod(Period("2021-06-29".toLocalDate, "2021-07-12".toLocalDate),
                        Period("2021-06-29".toLocalDate, "2021-06-30".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(19564.4)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-14", "2021-06-28", "2021-07-12"))
        .withUsualHours(List(UsualHours("2021-06-14".toLocalDate, Hours(40.0)),
                             UsualHours("2021-06-28".toLocalDate, Hours(50.0)),
                             UsualHours("2021-07-12".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(
          PartTimeHours("2021-06-14".toLocalDate, Hours(14.0)),
          PartTimeHours("2021-06-28".toLocalDate, Hours(15.0)),
          PartTimeHours("2021-07-12".toLocalDate, Hours(15.0))
        ))
        -> 1674.37
    ))

  val juneVariableWeeklyScenarios: Seq[(String, Seq[(UserAnswers, BigDecimal)])] = Seq(
    "June Variable Weekly Scenarios" -> Seq(
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-10-02")
        .withFurloughEndDate("2021-06-14")
        .withOnPayrollBefore30thOct2020(true)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate),
                        Period("2021-06-04".toLocalDate, "2021-06-07".toLocalDate)),
          FullPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(19564.4)
        .withFurloughStartDate("2021-06-04")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(40.0)), UsualHours("2021-06-14".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-14".toLocalDate, Hours(15.0))))
        -> 479.11,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-09-03")
        .withFurloughEndDate("2021-06-14")
        .withOnPayrollBefore30thOct2020(true)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate),
                        Period("2021-06-04".toLocalDate, "2021-06-07".toLocalDate)),
          FullPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(26000)
        .withFurloughStartDate("2021-06-04")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(40.0)), UsualHours("2021-06-14".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-14".toLocalDate, Hours(15.0))))
        -> 569.34,
      emptyUserAnswers
        .withRtiSubmission(EmployeeRTISubmission.No)
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-03-04")
        .withFurloughEndDate("2021-06-14")
        .withOnPayrollBefore30thOct2020(true)
        .withPreviousFurloughedPeriodsAnswer(true)
        .withFirstFurloughDate("2020-11-01")
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(
          PartialPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate),
                        Period("2021-06-04".toLocalDate, "2021-06-07".toLocalDate)),
          FullPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate))
        ))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(12000)
        .withFurloughStartDate("2021-06-04")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(40.0)), UsualHours("2021-06-14".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-14".toLocalDate, Hours(15.0))))
        -> 344.52,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-07")
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedOnOrBefore1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List("2019-06-03" -> 420, "2019-06-10" -> 490))
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate))))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(26000)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-07"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(40.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(14.0))))
        -> 258.58,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withFurloughEndDate("2021-06-24")
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedOnOrBefore1Feb2019()
        .withClaimPeriodStart("2021-06-03")
        .withLastYear(List("2019-06-10" -> 420, "2019-06-17" -> 490, "2019-06-24" -> 560))
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeNo)
        .withAnnualPayAmount(26000)
        .withFurloughStartDate("2021-06-04")
        .withClaimPeriodEnd("2021-06-28")
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14", "2021-06-21", "2021-06-28"))
        .withUsualHours(List())
        .withPartTimeHours(List())
        -> 1241.15,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-24")
        .withFurloughEndDate("2021-06-14")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate)),
                                  FullPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate))))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(19564.4)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(40.0)), UsualHours("2021-06-14".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-14".toLocalDate, Hours(15.0))))
        -> 778.84,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2020-12-24")
        .withFurloughEndDate("2021-06-14")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withStatutoryLeavePay(Amount(9243))
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate)),
                                  FullPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate))))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(19564.4)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(40.0)), UsualHours("2021-06-14".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-14".toLocalDate, Hours(15.0))))
        -> 778.84,
      emptyUserAnswers
        .withFurloughStatus(FurloughStatus.FurloughEnded)
        .withEmployeeStartDate("2021-03-01")
        .withFurloughEndDate("2021-06-14")
        .withOnPayrollBefore30thOct2020(false)
        .withPreviousFurloughedPeriodsAnswer(false)
        .withPaymentFrequency(Weekly)
        .withEmployeeStartedAfter1Feb2019()
        .withClaimPeriodStart("2021-06-01")
        .withLastYear(List())
        .withFurloughInLastTaxYear(false)
        .withPayPeriodsList(PayPeriodsList.Yes)
        .withPartTimePeriods(List(FullPeriod(Period("2021-06-01".toLocalDate, "2021-06-07".toLocalDate)),
                                  FullPeriod(Period("2021-06-08".toLocalDate, "2021-06-14".toLocalDate))))
        .withPayMethod(PayMethod.Variable)
        .withPartTimeQuestion(PartTimeQuestion.PartTimeYes)
        .withAnnualPayAmount(4782.2)
        .withFurloughStartDate("2021-06-01")
        .withClaimPeriodEnd("2021-06-30")
        .withPayDate(List("2021-05-31", "2021-06-07", "2021-06-14"))
        .withUsualHours(List(UsualHours("2021-06-07".toLocalDate, Hours(40.0)), UsualHours("2021-06-14".toLocalDate, Hours(50.0))))
        .withPartTimeHours(List(PartTimeHours("2021-06-07".toLocalDate, Hours(14.0)), PartTimeHours("2021-06-14".toLocalDate, Hours(15.0))))
        -> 392.97
    ))
}
