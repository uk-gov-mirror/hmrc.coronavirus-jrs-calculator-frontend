/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.Calculation.NicCalculationResult
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{CalculationResult, PayPeriod, PayPeriodBreakdown, PayPeriodWithPayDay, PaymentDate, TaxYearEnding2020, TaxYearEnding2021}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NicPensionCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val monthlyPayPeriodOne =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 20)))
  val monthlyPayPeriodTwo =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), PaymentDate(LocalDate.of(2020, 4, 20)))

  val fourWeeklyPayPeriodOne =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28)), PaymentDate(LocalDate.of(2020, 3, 20)))
  val fourWeeklyPayPeriodTwo =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 28)), PaymentDate(LocalDate.of(2020, 4, 20)))

  val fortnightlyPayPeriodOne =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14)), PaymentDate(LocalDate.of(2020, 3, 14)))
  val fortnightlyPayPeriodTwo =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 14)), PaymentDate(LocalDate.of(2020, 4, 14)))

  val weeklyPayPeriodOne =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7)), PaymentDate(LocalDate.of(2020, 3, 7)))
  val weeklyPayPeriodTwo =
    PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 7)), PaymentDate(LocalDate.of(2020, 4, 7)))

  forAll(scenarios) { (frequency, payment, taxYear, rate, expected) =>
    s"For payment frequency $frequency, payment amount ${payment.amount}, rate $rate in $taxYear should return $expected" in new NicPensionCalculator {
      calculate(frequency, payment, rate) mustBe expected
    }
  }

  "calculates NI for multiples payments periods" in new NicPensionCalculator {
    val payments = List(PayPeriodBreakdown(200.00, weeklyPayPeriodOne), PayPeriodBreakdown(600.00, weeklyPayPeriodTwo))

    val expected: CalculationResult =
      CalculationResult(
        NicCalculationResult,
        60.86,
        List(PayPeriodBreakdown(4.69, weeklyPayPeriodOne), PayPeriodBreakdown(56.17, weeklyPayPeriodTwo)))
    calculateGrant(Weekly, payments, NiRate()) mustBe expected
  }

  private lazy val scenarios = Table(
    ("paymentFrequency", "FurloughPayment", "taxYear", "rate", "expected"),
    (Monthly, PayPeriodBreakdown(510.00, monthlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (Monthly, PayPeriodBreakdown(512.00, monthlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (Monthly, PayPeriodBreakdown(5000.00, monthlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 59.67),
    (Monthly, PayPeriodBreakdown(515.00, monthlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (Monthly, PayPeriodBreakdown(525.00, monthlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.15),
    (Monthly, PayPeriodBreakdown(3525.00, monthlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 59.40),
    (FourWeekly, PayPeriodBreakdown(470.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (FourWeekly, PayPeriodBreakdown(473.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (FourWeekly, PayPeriodBreakdown(5000.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 55.05),
    (FourWeekly, PayPeriodBreakdown(475.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (FourWeekly, PayPeriodBreakdown(490.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.30),
    (FourWeekly, PayPeriodBreakdown(3525.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 54.81),
    (FortNightly, PayPeriodBreakdown(235.00, fortnightlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (FortNightly, PayPeriodBreakdown(237.00, fortnightlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (FortNightly, PayPeriodBreakdown(5000.00, fortnightlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 27.51),
    (FortNightly, PayPeriodBreakdown(235.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (FortNightly, PayPeriodBreakdown(250.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.30),
    (FortNightly, PayPeriodBreakdown(3525.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 27.39),
    (Weekly, PayPeriodBreakdown(117.00, weeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (Weekly, PayPeriodBreakdown(119.00, weeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (Weekly, PayPeriodBreakdown(5000.00, weeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 13.74),
    (Weekly, PayPeriodBreakdown(119.00, weeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (Weekly, PayPeriodBreakdown(130.00, weeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.30),
    (Weekly, PayPeriodBreakdown(3525.00, weeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 13.68),
    //NI
    (Monthly, PayPeriodBreakdown(700.00, monthlyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (Monthly, PayPeriodBreakdown(1000.00, monthlyPayPeriodOne), TaxYearEnding2020, NiRate(), 38.78),
    (Monthly, PayPeriodBreakdown(5000.00, monthlyPayPeriodOne), TaxYearEnding2020, NiRate(), 245.78),
    (Monthly, PayPeriodBreakdown(700.00, monthlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (Monthly, PayPeriodBreakdown(1000.00, monthlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 36.98),
    (Monthly, PayPeriodBreakdown(5000.00, monthlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 243.98),
    (FourWeekly, PayPeriodBreakdown(600.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (FourWeekly, PayPeriodBreakdown(700.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 4.97),
    (FourWeekly, PayPeriodBreakdown(5000.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 226.73),
    (FourWeekly, PayPeriodBreakdown(670.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (FourWeekly, PayPeriodBreakdown(1000.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 44.71),
    (FourWeekly, PayPeriodBreakdown(5000.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 225.08),
    (FortNightly, PayPeriodBreakdown(300.00, fortnightlyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (FortNightly, PayPeriodBreakdown(333.00, fortnightlyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.14),
    (FortNightly, PayPeriodBreakdown(2500.00, fortnightlyPayPeriodOne), TaxYearEnding2020, NiRate(), 113.30),
    (FortNightly, PayPeriodBreakdown(335.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (FortNightly, PayPeriodBreakdown(339.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.14),
    (FortNightly, PayPeriodBreakdown(2500.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 112.47),
    (Weekly, PayPeriodBreakdown(150.00, weeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (Weekly, PayPeriodBreakdown(250.00, weeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 11.59),
    (Weekly, PayPeriodBreakdown(1000.00, weeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 56.58),
    (Weekly, PayPeriodBreakdown(168, weeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (Weekly, PayPeriodBreakdown(179, weeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 1.38),
    (Weekly, PayPeriodBreakdown(1000, weeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 56.17)
  )

}
