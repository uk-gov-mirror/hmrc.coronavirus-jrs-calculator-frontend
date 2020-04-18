/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

///*
// * Copyright 2020 HM Revenue & Customs
// *
// */
//
//package services
//
//import java.time.LocalDate
//
//import base.SpecBase
//import models.Calculation.NicCalculationResult
//import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
//import models.{Amount, CalculationResult, Payment, PaymentDate, Period, PeriodBreakdown, PeriodWithPaymentDate, TaxYearEnding2020, TaxYearEnding2021}
//import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
//
//class NicPensionCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {
//
//  val monthlyPayPeriodOne =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 20)))
//  val monthlyPayPeriodTwo =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), PaymentDate(LocalDate.of(2020, 4, 20)))
//
//  val fourWeeklyPayPeriodOne =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28)), PaymentDate(LocalDate.of(2020, 3, 20)))
//  val fourWeeklyPayPeriodTwo =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 28)), PaymentDate(LocalDate.of(2020, 4, 20)))
//
//  val fortnightlyPayPeriodOne =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14)), PaymentDate(LocalDate.of(2020, 3, 14)))
//  val fortnightlyPayPeriodTwo =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 14)), PaymentDate(LocalDate.of(2020, 4, 14)))
//
//  val weeklyPayPeriodOne =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7)), PaymentDate(LocalDate.of(2020, 3, 7)))
//  val weeklyPayPeriodTwo =
//    PeriodWithPaymentDate(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 7)), PaymentDate(LocalDate.of(2020, 4, 7)))
//
//  forAll(scenarios) { (frequency, payment, taxYear, rate, expected) =>
//    s"For payment frequency $frequency, payment amount ${payment.payment.amount.value}, rate $rate in $taxYear should return $expected" in new NicPensionCalculator {
//      calculate(frequency, payment, rate) mustBe expected
//    }
//  }
//
//  "calculates NI for multiples payments periods" in new NicPensionCalculator {
//    val payments =
//      List(
//        PeriodBreakdown(Payment(Amount(200.00)), weeklyPayPeriodOne, Amount(576.92)),
//        PeriodBreakdown(Payment(Amount(600.00)), weeklyPayPeriodTwo, Amount(576.92)))
//
//    val expected: CalculationResult =
//      CalculationResult(
//        NicCalculationResult,
//        60.86,
//        List(
//          PeriodBreakdown(Payment(Amount(4.69)), weeklyPayPeriodOne, Amount(576.92)),
//          PeriodBreakdown(Payment(Amount(56.17)), weeklyPayPeriodTwo, Amount(576.92)))
//      )
//    calculateGrant(Weekly, payments, NiRate()) mustBe expected
//  }
//
//  private lazy val scenarios = Table(
//    ("paymentFrequency", "FurloughPayment", "taxYear", "rate", "expected"),
//    (Monthly, PeriodBreakdown(Payment(Amount(510.00)), monthlyPayPeriodOne, Amount(2500.00)), TaxYearEnding2020, PensionRate(), 0.0),
//    (Monthly, PeriodBreakdown(Payment(Amount(512.00)), monthlyPayPeriodOne, Amount(2500.00)), TaxYearEnding2020, PensionRate(), 0.03),
//    (Monthly, PeriodBreakdown(Payment(Amount(5000.00)), monthlyPayPeriodOne, Amount(2500.00)), TaxYearEnding2020, PensionRate(), 59.67),
//    (Monthly, PeriodBreakdown(Payment(Amount(515.00)), monthlyPayPeriodTwo, Amount(2500.00)), TaxYearEnding2021, PensionRate(), 0.0),
//    (Monthly, PeriodBreakdown(Payment(Amount(525.00)), monthlyPayPeriodTwo, Amount(2500.00)), TaxYearEnding2021, PensionRate(), 0.15),
//    (Monthly, PeriodBreakdown(Payment(Amount(3525.00)), monthlyPayPeriodTwo, Amount(2500.00)), TaxYearEnding2021, PensionRate(), 59.40),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(470.00)), fourWeeklyPayPeriodOne, Amount(2307.68)), TaxYearEnding2020, PensionRate(), 0.0),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(473.00)), fourWeeklyPayPeriodOne, Amount(2307.68)), TaxYearEnding2020, PensionRate(), 0.03),
//    (
//      FourWeekly,
//      PeriodBreakdown(Payment(Amount(5000.00)), fourWeeklyPayPeriodOne, Amount(2307.68)),
//      TaxYearEnding2020,
//      PensionRate(),
//      55.05),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(475.00)), fourWeeklyPayPeriodTwo, Amount(2307.68)), TaxYearEnding2021, PensionRate(), 0.0),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(490.00)), fourWeeklyPayPeriodTwo, Amount(2307.68)), TaxYearEnding2021, PensionRate(), 0.30),
//    (
//      FourWeekly,
//      PeriodBreakdown(Payment(Amount(3525.00)), fourWeeklyPayPeriodTwo, Amount(2307.68)),
//      TaxYearEnding2021,
//      PensionRate(),
//      54.81),
//    (
//      FortNightly,
//      PeriodBreakdown(Payment(Amount(235.00)), fortnightlyPayPeriodOne, Amount(1153.84)),
//      TaxYearEnding2020,
//      PensionRate(),
//      0.0),
//    (
//      FortNightly,
//      PeriodBreakdown(Payment(Amount(237.00)), fortnightlyPayPeriodOne, Amount(1153.84)),
//      TaxYearEnding2020,
//      PensionRate(),
//      0.03),
//    (
//      FortNightly,
//      PeriodBreakdown(Payment(Amount(5000.00)), fortnightlyPayPeriodOne, Amount(1153.84)),
//      TaxYearEnding2020,
//      PensionRate(),
//      27.51),
//    (
//      FortNightly,
//      PeriodBreakdown(Payment(Amount(235.00)), fortnightlyPayPeriodTwo, Amount(1153.84)),
//      TaxYearEnding2021,
//      PensionRate(),
//      0.0),
//    (
//      FortNightly,
//      PeriodBreakdown(Payment(Amount(250.00)), fortnightlyPayPeriodTwo, Amount(1153.84)),
//      TaxYearEnding2021,
//      PensionRate(),
//      0.30),
//    (
//      FortNightly,
//      PeriodBreakdown(Payment(Amount(3525.00)), fortnightlyPayPeriodTwo, Amount(1153.84)),
//      TaxYearEnding2021,
//      PensionRate(),
//      27.39),
//    (Weekly, PeriodBreakdown(Payment(Amount(117.00)), weeklyPayPeriodOne, Amount(576.92)), TaxYearEnding2020, PensionRate(), 0.0),
//    (Weekly, PeriodBreakdown(Payment(Amount(119.00)), weeklyPayPeriodOne, Amount(576.92)), TaxYearEnding2020, PensionRate(), 0.03),
//    (Weekly, PeriodBreakdown(Payment(Amount(5000.00)), weeklyPayPeriodOne, Amount(576.92)), TaxYearEnding2020, PensionRate(), 13.74),
//    (Weekly, PeriodBreakdown(Payment(Amount(119.00)), weeklyPayPeriodTwo, Amount(576.92)), TaxYearEnding2021, PensionRate(), 0.0),
//    (Weekly, PeriodBreakdown(Payment(Amount(130.00)), weeklyPayPeriodTwo, Amount(576.92)), TaxYearEnding2021, PensionRate(), 0.30),
//    (Weekly, PeriodBreakdown(Payment(Amount(3525.00)), weeklyPayPeriodTwo, Amount(576.92)), TaxYearEnding2021, PensionRate(), 13.68),
//    //NI
//    (Monthly, PeriodBreakdown(Payment(Amount(700.00)), monthlyPayPeriodOne, Amount(2500.00)), TaxYearEnding2020, NiRate(), 0.00),
//    (Monthly, PeriodBreakdown(Payment(Amount(1000.00)), monthlyPayPeriodOne, Amount(2500.00)), TaxYearEnding2020, NiRate(), 38.78),
//    (Monthly, PeriodBreakdown(Payment(Amount(5000.00)), monthlyPayPeriodOne, Amount(2500.00)), TaxYearEnding2020, NiRate(), 245.78),
//    (Monthly, PeriodBreakdown(Payment(Amount(700.00)), monthlyPayPeriodTwo, Amount(2500.00)), TaxYearEnding2021, NiRate(), 0.00),
//    (Monthly, PeriodBreakdown(Payment(Amount(1000.00)), monthlyPayPeriodTwo, Amount(2500.00)), TaxYearEnding2021, NiRate(), 36.98),
//    (Monthly, PeriodBreakdown(Payment(Amount(5000.00)), monthlyPayPeriodTwo, Amount(2500.00)), TaxYearEnding2021, NiRate(), 243.98),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(600.00)), fourWeeklyPayPeriodOne, Amount(2307.68)), TaxYearEnding2020, NiRate(), 0.00),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(700.00)), fourWeeklyPayPeriodOne, Amount(2307.68)), TaxYearEnding2020, NiRate(), 4.97),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(5000.00)), fourWeeklyPayPeriodOne, Amount(2307.68)), TaxYearEnding2020, NiRate(), 226.73),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(670.00)), fourWeeklyPayPeriodTwo, Amount(2307.68)), TaxYearEnding2021, NiRate(), 0.00),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(1000.00)), fourWeeklyPayPeriodTwo, Amount(2307.68)), TaxYearEnding2021, NiRate(), 44.71),
//    (FourWeekly, PeriodBreakdown(Payment(Amount(5000.00)), fourWeeklyPayPeriodTwo, Amount(2307.68)), TaxYearEnding2021, NiRate(), 225.08),
//    (FortNightly, PeriodBreakdown(Payment(Amount(300.00)), fortnightlyPayPeriodOne, Amount(1153.84)), TaxYearEnding2020, NiRate(), 0.00),
//    (FortNightly, PeriodBreakdown(Payment(Amount(333.00)), fortnightlyPayPeriodOne, Amount(1153.84)), TaxYearEnding2020, NiRate(), 0.14),
//    (FortNightly, PeriodBreakdown(Payment(Amount(2500.00)), fortnightlyPayPeriodOne, Amount(1153.84)), TaxYearEnding2020, NiRate(), 113.30),
//    (FortNightly, PeriodBreakdown(Payment(Amount(335.00)), fortnightlyPayPeriodTwo, Amount(1153.84)), TaxYearEnding2021, NiRate(), 0.00),
//    (FortNightly, PeriodBreakdown(Payment(Amount(339.00)), fortnightlyPayPeriodTwo, Amount(1153.84)), TaxYearEnding2021, NiRate(), 0.14),
//    (FortNightly, PeriodBreakdown(Payment(Amount(2500.00)), fortnightlyPayPeriodTwo, Amount(1153.84)), TaxYearEnding2021, NiRate(), 112.47),
//    (Weekly, PeriodBreakdown(Payment(Amount(150.00)), weeklyPayPeriodOne, Amount(576.92)), TaxYearEnding2020, NiRate(), 0.00),
//    (Weekly, PeriodBreakdown(Payment(Amount(250.00)), weeklyPayPeriodOne, Amount(576.92)), TaxYearEnding2020, NiRate(), 11.59),
//    (Weekly, PeriodBreakdown(Payment(Amount(1000.00)), weeklyPayPeriodOne, Amount(576.92)), TaxYearEnding2020, NiRate(), 56.58),
//    (Weekly, PeriodBreakdown(Payment(Amount(168.00)), weeklyPayPeriodTwo, Amount(576.92)), TaxYearEnding2021, NiRate(), 0.00),
//    (Weekly, PeriodBreakdown(Payment(Amount(179.00)), weeklyPayPeriodTwo, Amount(576.92)), TaxYearEnding2021, NiRate(), 1.38),
//    (Weekly, PeriodBreakdown(Payment(Amount(1000.00)), weeklyPayPeriodTwo, Amount(576.92)), TaxYearEnding2021, NiRate(), 56.17)
//  )
//
//}
