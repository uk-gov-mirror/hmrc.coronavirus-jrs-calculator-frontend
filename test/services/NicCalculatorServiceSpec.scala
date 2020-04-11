/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.{FourWeekly, FurloughPayment, Monthly, PayPeriod, TaxYearEnding2020, TaxYearEnding2021}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NicCalculatorServiceSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(scenarios) { (frequency, payment, taxYear, expected) =>
    s"For payment frequency $frequency, payment amount ${payment.amount} in $taxYear should return $expected" in new NicCalculatorService {
      calculateNic(frequency, payment) mustBe expected
    }
  }

  private lazy val scenarios = Table(
    ("paymentFrequency", "FurloughPayment", "taxYear", "expected"),
    (
      Monthly,
      FurloughPayment(700.00, PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      TaxYearEnding2020,
      0.00),
    (
      Monthly,
      FurloughPayment(1000.00, PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      TaxYearEnding2020,
      38.77),
    (
      Monthly,
      FurloughPayment(5000.00, PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      TaxYearEnding2020,
      245.77),
    (
      Monthly,
      FurloughPayment(700.00, PayPeriod(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 31))),
      TaxYearEnding2021,
      0.00),
    (
      Monthly,
      FurloughPayment(1000.00, PayPeriod(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 31))),
      TaxYearEnding2021,
      36.98),
    (
      Monthly,
      FurloughPayment(5000.00, PayPeriod(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 31))),
      TaxYearEnding2021,
      243.98),
    (
      FourWeekly,
      FurloughPayment(600.00, PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      TaxYearEnding2020,
      0.00),
    (
      FourWeekly,
      FurloughPayment(700.00, PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      TaxYearEnding2020,
      4.96)
  )

}
