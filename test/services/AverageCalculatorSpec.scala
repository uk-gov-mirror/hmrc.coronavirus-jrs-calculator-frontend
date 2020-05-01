/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.PayMethod.Variable
import models.{Amount, FullPeriod, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriod, PartialPeriodWithPaymentDate, PaymentDate, Period, PeriodWithPaymentDate}

class AverageCalculatorSpec extends SpecBase with CoreTestDataBuilder {

  "calculates average pay for an employee" in new AverageCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val nonFurloughPay = NonFurloughPay(None, Some(Amount(1000.00)))
    val grossPay = Amount(2400.00)
    val priorFurloughPeriod = Period(employeeStartDate, furloughStartDate.minusDays(1))
    val afterFurloughPeriod =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        PaymentDate(LocalDate.of(2020, 3, 31)))

    val afterFurloughPartial = PartialPeriodWithPaymentDate(
      PartialPeriod(
        Period(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 31)),
        Period(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 15))),
      PaymentDate(LocalDate.of(2020, 5, 31))
    )
    val payPeriods: Seq[PeriodWithPaymentDate] = Seq(afterFurloughPeriod, afterFurloughPartial)

    val expected = Seq(
      paymentWithFullPeriod(817.47, afterFurloughPeriod, Variable),
      paymentWithPartialPeriod(1000.0, 395.55, afterFurloughPartial, Variable)
    )

    calculateAveragePay(nonFurloughPay, priorFurloughPeriod, payPeriods.head, grossPay) mustBe expected.head
    calculateAveragePay(nonFurloughPay, priorFurloughPeriod, payPeriods.tail.head, grossPay) mustBe expected.tail.head
  }

  "calculate daily average gross earning for a given pay period" in new AverageCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val periodBeforeFurlough = Period(employeeStartDate, furloughStartDate.minusDays(1))

    averageDailyCalculator(periodBeforeFurlough, Amount(2400.0)) mustBe Amount(26.37)
  }
}
