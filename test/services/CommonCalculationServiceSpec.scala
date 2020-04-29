/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.PaymentFrequency.Monthly
import models.{Amount, FullPeriod, FullPeriodBreakdown, FullPeriodWithPaymentDate, PaymentDate, Period}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CommonCalculationServiceSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(fullPeriodScenarios) { (frequency, grossPay, furloughPayment, period, paymentDate, rate, expectedGrant) =>
    s"Calculate grant for a full period with Payment Frequency: $frequency, " +
      s"a Payment Date: $paymentDate, rate $rate and a Furlough Grant: ${furloughPayment.value}" in new CommonCalculationService {
      val expected = FullPeriodBreakdown(expectedGrant, FullPeriodWithPaymentDate(period, paymentDate))

      fullPeriodCalculation(frequency, furloughPayment, period, paymentDate, rate) mustBe expected
    }
  }

  "Returns zero for an amount lesser than threshold" in new CommonCalculationService {
    greaterThanAllowance(100.0, 101.0, NiRate()) mustBe 0.0
    greaterThanAllowance(99.0, 100.0, PensionRate()) mustBe 0.0
  }

  "Returns an ((amount - threshold) * rate) rounded half_up if greater than threshold" in new CommonCalculationService {
    greaterThanAllowance(1000.0, 100.0, NiRate()) mustBe 124.20
    greaterThanAllowance(1000.0, 100.0, PensionRate()) mustBe 27.0
  }

  private lazy val fullPeriodScenarios = Table(
    ("frequency", "grossPay", "furloughPayment", "period", "paymentDate", "rate", "expectedGrant"),
    (
      Monthly,
      Amount(2000.00),
      Amount(1600.00),
      FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      NiRate(),
      Amount(121.58)),
    (
      Monthly,
      Amount(750.00),
      Amount(600.00),
      FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      NiRate(),
      Amount(0.00)),
    (
      Monthly,
      Amount(2000.00),
      Amount(1600.00),
      FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      PensionRate(),
      Amount(32.64)),
    (
      Monthly,
      Amount(750.00),
      Amount(600.00),
      FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      PaymentDate(LocalDate.of(2020, 3, 31)),
      PensionRate(),
      Amount(2.64))
  )
}
