/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{CalculationResult, FurloughPayment, PaymentDate, PaymentDateBreakdown, TaxYearEnding2020, TaxYearEnding2021}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CalculatorServiceSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(scenarios) { (frequency, payment, taxYear, rate, expected) =>
    s"For payment frequency $frequency, payment amount ${payment.amount}, rate $rate in $taxYear should return $expected" in new CalculatorService {
      calculate(frequency, payment, NiRate()) mustBe expected
    }
  }

  "calculates NI for multiples payments periods" in new CalculatorService {
    val periodOne: PaymentDate = PaymentDate(LocalDate.of(2020, 3, 7))
    val periodTwo: PaymentDate = PaymentDate(LocalDate.of(2020, 4, 14))
    val payments = List(FurloughPayment(200.00, periodOne), FurloughPayment(600.00, periodTwo))

    val expected: CalculationResult =
      CalculationResult(60.86, List(PaymentDateBreakdown(4.69, periodOne), PaymentDateBreakdown(56.17, periodTwo)))
    calculateResult(Weekly, payments, NiRate()) mustBe expected
  }

  private lazy val scenarios = Table(
    ("paymentFrequency", "FurloughPayment", "taxYear", "rate", "expected"),
    (Monthly, FurloughPayment(700.00, PaymentDate(LocalDate.of(2020, 3, 31))), TaxYearEnding2020, NiRate(), 0.00),
    (Monthly, FurloughPayment(1000.00, PaymentDate(LocalDate.of(2020, 3, 31))), TaxYearEnding2020, NiRate(), 38.78),
    (Monthly, FurloughPayment(5000.00, PaymentDate(LocalDate.of(2020, 3, 31))), TaxYearEnding2020, NiRate(), 245.78),
    (Monthly, FurloughPayment(700.00, PaymentDate(LocalDate.of(2020, 5, 31))), TaxYearEnding2021, NiRate(), 0.00),
    (Monthly, FurloughPayment(1000.00, PaymentDate(LocalDate.of(2020, 5, 31))), TaxYearEnding2021, NiRate(), 36.98),
    (Monthly, FurloughPayment(5000.00, PaymentDate(LocalDate.of(2020, 5, 31))), TaxYearEnding2021, NiRate(), 243.98),
    (FourWeekly, FurloughPayment(600.00, PaymentDate(LocalDate.of(2020, 3, 28))), TaxYearEnding2020, NiRate(), 0.00),
    (FourWeekly, FurloughPayment(700.00, PaymentDate(LocalDate.of(2020, 3, 28))), TaxYearEnding2020, NiRate(), 4.97),
    (FourWeekly, FurloughPayment(5000.00, PaymentDate(LocalDate.of(2020, 3, 28))), TaxYearEnding2020, NiRate(), 226.32),
    (FourWeekly, FurloughPayment(670.00, PaymentDate(LocalDate.of(2020, 4, 28))), TaxYearEnding2021, NiRate(), 0.00),
    (FourWeekly, FurloughPayment(1000.00, PaymentDate(LocalDate.of(2020, 4, 28))), TaxYearEnding2021, NiRate(), 44.71),
    (FourWeekly, FurloughPayment(5000.00, PaymentDate(LocalDate.of(2020, 4, 28))), TaxYearEnding2021, NiRate(), 224.66),
    (FortNightly, FurloughPayment(300.00, PaymentDate(LocalDate.of(2020, 3, 14))), TaxYearEnding2020, NiRate(), 0.00),
    (FortNightly, FurloughPayment(333.00, PaymentDate(LocalDate.of(2020, 3, 14))), TaxYearEnding2020, NiRate(), 0.14),
    (FortNightly, FurloughPayment(2500.00, PaymentDate(LocalDate.of(2020, 3, 14))), TaxYearEnding2020, NiRate(), 113.16),
    (FortNightly, FurloughPayment(335.00, PaymentDate(LocalDate.of(2020, 4, 14))), TaxYearEnding2021, NiRate(), 0.00),
    (FortNightly, FurloughPayment(339.00, PaymentDate(LocalDate.of(2020, 4, 14))), TaxYearEnding2021, NiRate(), 0.14),
    (FortNightly, FurloughPayment(2500.00, PaymentDate(LocalDate.of(2020, 4, 14))), TaxYearEnding2021, NiRate(), 112.33),
    (Weekly, FurloughPayment(150.00, PaymentDate(LocalDate.of(2020, 3, 7))), TaxYearEnding2020, NiRate(), 0.00),
    (Weekly, FurloughPayment(250.00, PaymentDate(LocalDate.of(2020, 3, 7))), TaxYearEnding2020, NiRate(), 11.59),
    (Weekly, FurloughPayment(1000.00, PaymentDate(LocalDate.of(2020, 3, 7))), TaxYearEnding2020, NiRate(), 56.58),
    (Weekly, FurloughPayment(168, PaymentDate(LocalDate.of(2020, 4, 7))), TaxYearEnding2021, NiRate(), 0.00),
    (Weekly, FurloughPayment(179, PaymentDate(LocalDate.of(2020, 4, 7))), TaxYearEnding2021, NiRate(), 1.38),
    (Weekly, FurloughPayment(1000, PaymentDate(LocalDate.of(2020, 4, 7))), TaxYearEnding2021, NiRate(), 56.17)
  )

}
