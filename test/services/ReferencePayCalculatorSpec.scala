/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.PayMethod.{Regular, Variable}
import models.PaymentFrequency.Monthly
import models.{Amount, CylbPayment, FullPeriod, FullPeriodWithPaymentDate, NonFurloughPay, PartialPeriod, PartialPeriodWithPaymentDate, PaymentDate, PaymentWithPeriod, Period}

class ReferencePayCalculatorSpec extends SpecBase with CoreTestDataBuilder {

  "calculates reference gross pay for an employee on variable pays not including cylb when empty" in new ReferencePayCalculator {
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val nonFurloughPay = NonFurloughPay(None, Some(Amount(1000.00)))
    val grossPay = Amount(2400.00)
    val priorFurloughPeriod = Period(employeeStartDate, furloughStartDate.minusDays(1))
    val afterFurloughPeriod =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
        PaymentDate(LocalDate.of(2020, 3, 31)))
    val afterFurloughPeriodTwo =
      FullPeriodWithPaymentDate(
        FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
        PaymentDate(LocalDate.of(2020, 4, 30)))
    val afterFurloughPartial = PartialPeriodWithPaymentDate(
      PartialPeriod(
        Period(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 31)),
        Period(LocalDate.of(2020, 5, 1), LocalDate.of(2020, 5, 15))),
      PaymentDate(LocalDate.of(2020, 5, 31))
    )
    val payPeriods = Seq(afterFurloughPeriod, afterFurloughPeriodTwo, afterFurloughPartial)

    val expected = Seq(
      paymentWithFullPeriod(817.47, afterFurloughPeriod, Variable),
      paymentWithFullPeriod(791.10, afterFurloughPeriodTwo, Variable),
      paymentWithPartialPeriod(1000.0, 395.55, afterFurloughPartial, Variable)
    )

    calculateVariablePay(nonFurloughPay, priorFurloughPeriod, payPeriods, grossPay, Seq.empty, Monthly) mustBe expected
  }

  "compare cylb and avg gross pay amount taking the greater" in new ReferencePayCalculator {
    val cylb = Seq(
      paymentWithFullPeriod(500.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28"), Variable),
      paymentWithFullPeriod(200.00, fullPeriodWithPaymentDate("2020,3,29", "2020,4,26", "2020, 4, 26"), Variable)
    )

    val avg = Seq(
      paymentWithFullPeriod(450.0, fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28"), Variable),
      paymentWithFullPeriod(450.0, fullPeriodWithPaymentDate("2020,3,29", "2020,4,26", "2020, 4, 26"), Variable)
    )

    val expected = Seq(
      paymentWithFullPeriod(500.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28"), Variable),
      paymentWithFullPeriod(450.00, fullPeriodWithPaymentDate("2020,3,29", "2020,4,26", "2020, 4, 26"), Variable)
    )

    takeGreaterGrossPay(cylb, avg) mustBe expected
  }

  "calculate avg and cylb and return the greater" in new ReferencePayCalculator {
    val nonFurloughPay = NonFurloughPay(None, None)
    val employeeStartDate = LocalDate.of(2019, 12, 1)
    val furloughStartDate = LocalDate.of(2020, 3, 1)
    val cylbs = Seq(CylbPayment(LocalDate.of(2019, 3, 31), Amount(900.0)))
    val priorFurloughPeriod = Period(employeeStartDate, furloughStartDate.minusDays(1))
    val afterFurloughPeriod = fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 31")

    val expected: Seq[PaymentWithPeriod] = Seq(
      paymentWithFullPeriod(900.0, fullPeriodWithPaymentDate("2020,3,1", "2020,3,31", "2020, 3, 31"), Variable),
    )

    calculateVariablePay(nonFurloughPay, priorFurloughPeriod, Seq(afterFurloughPeriod), Amount(2400.0), cylbs, Monthly) mustBe expected
  }

  "calculates regular pay" in new ReferencePayCalculator {
    val salary = Amount(2000)
    val afterFurlough = partialPeriodWithPaymentDate("2020, 4, 1", "2020, 4, 30", "2020, 4, 1", "2020, 4, 15", "2020, 4, 30")

    val periods = Seq(
      fullPeriodWithPaymentDate("2020, 3, 1", "2020, 3, 31", "2020, 3, 31"),
      afterFurlough
    )

    val expected = Seq(
      paymentWithFullPeriod(2000.0, fullPeriodWithPaymentDate("2020,3,1", "2020,3,31", "2020, 3, 31"), Regular),
      paymentWithPartialPeriod(1000.0, 1000.0, afterFurlough, Regular)
    )

    calculateRegularPay(salary, periods) mustBe expected
  }
}
