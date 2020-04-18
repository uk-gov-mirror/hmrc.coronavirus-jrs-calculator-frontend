/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.SpecBase
import models.PaymentFrequency.Monthly
import models.{Amount, PartialPeriod, PartialPeriodBreakdown, PartialPeriodWithPaymentDate, PaymentDate, Period, PeriodBreakdown, PeriodWithPaymentDate}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class NicCalculatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  forAll(fullPeriodScenarios) { (frequency, furloughPayment, periodWithPaymentDate, expectedGrant) =>
    s"Calculate NIC grant for a full period with Payment Frequency: $frequency, " +
      s"a Payment Date: ${periodWithPaymentDate.paymentDate} and a Furlough Grant: ${furloughPayment.value}" in new NicCalculator {
      val expected = PeriodBreakdown(expectedGrant, periodWithPaymentDate)

      calculateFullPeriod(frequency, furloughPayment, periodWithPaymentDate) mustBe expected
    }
  }

  forAll(partialPeriodScenarios) { (frequency, totalPay, partialPeriodWithPaymentDate, expectedGrant) =>
    s"Calculate NIC grant for a partial period with Payment Frequency: $frequency," +
      s"a PaymentDate: ${partialPeriodWithPaymentDate.paymentDate} and a Total Pay: ${totalPay.value}" in new NicCalculator {
      val expected = PartialPeriodBreakdown(expectedGrant, partialPeriodWithPaymentDate)

      calculatePartialPeriod(frequency, totalPay, partialPeriodWithPaymentDate) mustBe expected
    }
  }

  forAll(partialPeriodWithTopUpScenarios) { (frequency, totalPay, furloughPayment, partialPeriodWithPaymentDate, expectedGrant) =>
    s"Calculate NIC grant for a partial period where the employer has topped up the employees pay " +
      s"Frequency: $frequency, PaymentDate: ${partialPeriodWithPaymentDate.paymentDate}, Total Pay: ${totalPay.value} " +
      s"and Furlough Payment: $furloughPayment" in new NicCalculator {
      val expected = PartialPeriodBreakdown(expectedGrant, partialPeriodWithPaymentDate)

      calculatePartialPeriodWithTopUp(frequency, totalPay, furloughPayment, partialPeriodWithPaymentDate) mustBe expected
    }
  }

  private lazy val fullPeriodScenarios = Table(
    ("frequency", "furloughPayment", "periodWithPaymentDate", "expectedGrant"),
    (
      Monthly,
      Amount(1600.00),
      PeriodWithPaymentDate(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 31))),
      Amount(121.58)),
    (
      Monthly,
      Amount(600.00),
      PeriodWithPaymentDate(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 31))),
      Amount(0.00))
  )

  private lazy val partialPeriodScenarios = Table(
    ("frequency", "totalPay", "partialPeriodWithPaymentDate", "expectedGrant"),
    (
      Monthly,
      Amount(2160.0),
      PartialPeriodWithPaymentDate(
        PartialPeriod(
          Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)),
          Period(LocalDate.of(2020, 4, 16), LocalDate.of(2020, 4, 30))),
        PaymentDate(LocalDate.of(2020, 4, 30))
      ),
      Amount(98.53))
  )

  private lazy val partialPeriodWithTopUpScenarios = Table(
    ("frequency", "totalPay", "furloughPayment", "periodWithPaymentDate", "expectedGrant"),
    (
      Monthly,
      Amount(2400.00),
      Amount(960.0),
      PartialPeriodWithPaymentDate(
        PartialPeriod(
          Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)),
          Period(LocalDate.of(2020, 4, 16), LocalDate.of(2020, 4, 30))),
        PaymentDate(LocalDate.of(2020, 4, 30))
      ),
      Amount(92.07))
  )

}
