/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{CalculationResult, FurloughPayment, PayPeriod, PayPeriodBreakdown, PaymentDate, TaxYearEnding2020, TaxYearEnding2021}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CalculatorServiceSpec extends SpecBase with ScalaCheckPropertyChecks {

  val monthlyPayPeriodOne = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31), PaymentDate(LocalDate.of(2020, 3, 20)))
  val monthlyPayPeriodTwo = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30), PaymentDate(LocalDate.of(2020, 4, 20)))

  val fourWeeklyPayPeriodOne = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 28), PaymentDate(LocalDate.of(2020, 3, 20)))
  val fourWeeklyPayPeriodTwo = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 28), PaymentDate(LocalDate.of(2020, 4, 20)))

  val fortnightlyPayPeriodOne = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 14), PaymentDate(LocalDate.of(2020, 3, 14)))
  val fortnightlyPayPeriodTwo = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 14), PaymentDate(LocalDate.of(2020, 4, 14)))

  val weeklyPayPeriodOne = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 7), PaymentDate(LocalDate.of(2020, 3, 7)))
  val weeklyPayPeriodTwo = PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 7), PaymentDate(LocalDate.of(2020, 4, 7)))

  forAll(scenarios) { (frequency, payment, taxYear, rate, expected) =>
    s"For payment frequency $frequency, payment amount ${payment.amount}, rate $rate in $taxYear should return $expected" in new CalculatorService {
      calculate(frequency, payment, rate) mustBe expected
    }
  }

  "calculates NI for multiples payments periods" in new CalculatorService {
    val payments = List(FurloughPayment(200.00, weeklyPayPeriodOne), FurloughPayment(600.00, weeklyPayPeriodTwo))

    val expected: CalculationResult =
      CalculationResult(60.86, List(PayPeriodBreakdown(4.69, weeklyPayPeriodOne), PayPeriodBreakdown(56.17, weeklyPayPeriodTwo)))
    calculateResult(Weekly, payments, NiRate()) mustBe expected
  }

  private lazy val scenarios = Table(
    ("paymentFrequency", "FurloughPayment", "taxYear", "rate", "expected"),
    (Monthly, FurloughPayment(510.00, monthlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (Monthly, FurloughPayment(512.00, monthlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (Monthly, FurloughPayment(5000.00, monthlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 59.67),
    (Monthly, FurloughPayment(515.00, monthlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (Monthly, FurloughPayment(525.00, monthlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.15),
    (Monthly, FurloughPayment(3525.00, monthlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 59.40),
    (FourWeekly, FurloughPayment(470.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (FourWeekly, FurloughPayment(473.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (FourWeekly, FurloughPayment(5000.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 55.05),
    (FourWeekly, FurloughPayment(475.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (FourWeekly, FurloughPayment(490.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.30),
    (FourWeekly, FurloughPayment(3525.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 54.81),
    (FortNightly, FurloughPayment(235.00, fortnightlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (FortNightly, FurloughPayment(237.00, fortnightlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (FortNightly, FurloughPayment(5000.00, fortnightlyPayPeriodOne), TaxYearEnding2020, PensionRate(), 27.51),
    (FortNightly, FurloughPayment(235.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (FortNightly, FurloughPayment(250.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.30),
    (FortNightly, FurloughPayment(3525.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 27.39),
    (Weekly, FurloughPayment(117.00, weeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.0),
    (Weekly, FurloughPayment(119.00, weeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 0.03),
    (Weekly, FurloughPayment(5000.00, weeklyPayPeriodOne), TaxYearEnding2020, PensionRate(), 13.74),
    (Weekly, FurloughPayment(119.00, weeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.0),
    (Weekly, FurloughPayment(130.00, weeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 0.30),
    (Weekly, FurloughPayment(3525.00, weeklyPayPeriodTwo), TaxYearEnding2021, PensionRate(), 13.68),
    //NI
    (Monthly, FurloughPayment(700.00, monthlyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (Monthly, FurloughPayment(1000.00, monthlyPayPeriodOne), TaxYearEnding2020, NiRate(), 38.78),
    (Monthly, FurloughPayment(5000.00, monthlyPayPeriodOne), TaxYearEnding2020, NiRate(), 245.78),
    (Monthly, FurloughPayment(700.00, monthlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (Monthly, FurloughPayment(1000.00, monthlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 36.98),
    (Monthly, FurloughPayment(5000.00, monthlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 243.98),
    (FourWeekly, FurloughPayment(600.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (FourWeekly, FurloughPayment(700.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 4.97),
    (FourWeekly, FurloughPayment(5000.00, fourWeeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 226.73),
    (FourWeekly, FurloughPayment(670.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (FourWeekly, FurloughPayment(1000.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 44.71),
    (FourWeekly, FurloughPayment(5000.00, fourWeeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 225.08),
    (FortNightly, FurloughPayment(300.00, fortnightlyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (FortNightly, FurloughPayment(333.00, fortnightlyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.14),
    (FortNightly, FurloughPayment(2500.00, fortnightlyPayPeriodOne), TaxYearEnding2020, NiRate(), 113.30),
    (FortNightly, FurloughPayment(335.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (FortNightly, FurloughPayment(339.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.14),
    (FortNightly, FurloughPayment(2500.00, fortnightlyPayPeriodTwo), TaxYearEnding2021, NiRate(), 112.47),
    (Weekly, FurloughPayment(150.00, weeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 0.00),
    (Weekly, FurloughPayment(250.00, weeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 11.59),
    (Weekly, FurloughPayment(1000.00, weeklyPayPeriodOne), TaxYearEnding2020, NiRate(), 56.58),
    (Weekly, FurloughPayment(168, weeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 0.00),
    (Weekly, FurloughPayment(179, weeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 1.38),
    (Weekly, FurloughPayment(1000, weeklyPayPeriodTwo), TaxYearEnding2021, NiRate(), 56.17)
  )

}
