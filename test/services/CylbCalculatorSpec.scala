/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.PaymentFrequency.{FortNightly, FourWeekly, Weekly}
import models.{Amount, CylbPayment, NonFurloughPay, Periods}

class CylbCalculatorSpec extends SpecBase with CoreTestDataBuilder {

  val determineNonFurloughPay: (Periods, NonFurloughPay) => Amount = NonFurloughPay.determineNonFurloughPay

  "calculate cylb amounts for weekly" in new CylbCalculator {
    val cylbs = Seq(
      CylbPayment(LocalDate.of(2019, 3, 2), Amount(700.00)),
      CylbPayment(LocalDate.of(2019, 3, 9), Amount(350.00)),
      CylbPayment(LocalDate.of(2019, 3, 16), Amount(140.00))
    )

    val periods = Seq(
      fullPeriodWithPaymentDate("2020,3,1", "2020,3,7", "2020, 3, 7"),
      fullPeriodWithPaymentDate("2020,3,8", "2020,3,14", "2020, 3, 14")
    )

    val nonFurloughPay = NonFurloughPay(None, None)

    val expected = Seq(
      paymentWithFullPeriod(450.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,7", "2020, 3, 7")),
      paymentWithFullPeriod(200.00, fullPeriodWithPaymentDate("2020,3,8", "2020,3,14", "2020, 3, 14"))
    )

    calculateCylb(nonFurloughPay, Weekly, cylbs, periods, determineNonFurloughPay) mustBe expected
  }

  "calculate cylb amounts for fortnightly" in new CylbCalculator {
    val cylbs = Seq(
      CylbPayment(LocalDate.of(2019, 3, 2), Amount(1400.00)),
      CylbPayment(LocalDate.of(2019, 3, 16), Amount(700.00)),
      CylbPayment(LocalDate.of(2019, 3, 30), Amount(280.00))
    )

    val periods = Seq(
      fullPeriodWithPaymentDate("2020,3,1", "2020,3,14", "2020, 3, 14"),
      fullPeriodWithPaymentDate("2020,3,15", "2020,3,28", "2020, 3, 28")
    )

    val nonFurloughPay = NonFurloughPay(None, None)

    val expected = Seq(
      paymentWithFullPeriod(800.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,14", "2020, 3, 14")),
      paymentWithFullPeriod(340.00, fullPeriodWithPaymentDate("2020,3,15", "2020,3,28", "2020, 3, 28"))
    )

    calculateCylb(nonFurloughPay, FortNightly, cylbs, periods, determineNonFurloughPay) mustBe expected
  }

  "calculate cylb amounts for fourweekly" in new CylbCalculator {
    val cylbs = Seq(
      CylbPayment(LocalDate.of(2019, 3, 2), Amount(2800.00)),
      CylbPayment(LocalDate.of(2019, 3, 30), Amount(1400.00)),
      CylbPayment(LocalDate.of(2019, 4, 27), Amount(560.00))
    )

    val periods = Seq(
      fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28"),
      fullPeriodWithPaymentDate("2020,3,29", "2020,4,25", "2020, 4, 25")
    )

    val nonFurloughPay = NonFurloughPay(None, None)

    val expected = Seq(
      paymentWithFullPeriod(1500.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,28", "2020, 3, 28")),
      paymentWithFullPeriod(620.00, fullPeriodWithPaymentDate("2020,3,29", "2020,4,25", "2020, 4, 25"))
    )

    calculateCylb(nonFurloughPay, FourWeekly, cylbs, periods, determineNonFurloughPay) mustBe expected
  }

  "calculate cylb amounts for partial period where only days from current required" in new CylbCalculator {
    val cylbs = Seq(
      CylbPayment(LocalDate.of(2019, 3, 2), Amount(700.00)),
      CylbPayment(LocalDate.of(2019, 3, 9), Amount(350.00)),
      CylbPayment(LocalDate.of(2019, 3, 16), Amount(140.00))
    )

    val periods = Seq(
      partialPeriodWithPaymentDate("2020,3,1", "2020,3,7", "2020,3,3", "2020,3,7", "2020, 3, 7"),
      fullPeriodWithPaymentDate("2020,3,8", "2020,3,14", "2020, 3, 14")
    )

    val nonFurloughPay = NonFurloughPay(None, None)

    val expected = Seq(
      paymentWithPartialPeriod(0.0, 250.00, partialPeriodWithPaymentDate("2020,3,1", "2020,3,7", "2020,3,3", "2020,3,7", "2020, 3, 7")),
      paymentWithFullPeriod(200.00, fullPeriodWithPaymentDate("2020,3,8", "2020,3,14", "2020, 3, 14"))
    )

    calculateCylb(nonFurloughPay, Weekly, cylbs, periods, determineNonFurloughPay) mustBe expected
  }

  "calculate cylb amounts for partial period where only days from previous required" in new CylbCalculator {
    val cylbs = Seq(
      CylbPayment(LocalDate.of(2019, 3, 2), Amount(700.00)),
      CylbPayment(LocalDate.of(2019, 3, 9), Amount(350.00)),
      CylbPayment(LocalDate.of(2019, 3, 16), Amount(140.00))
    )

    val periods = Seq(
      fullPeriodWithPaymentDate("2020,3,1", "2020,3,7", "2020, 3, 7"),
      partialPeriodWithPaymentDate("2020,3,8", "2020,3,14", "2020,3,8", "2020,3,9", "2020, 3, 14")
    )

    val nonFurloughPay = NonFurloughPay(None, None)

    val expected = Seq(
      paymentWithFullPeriod(450.00, fullPeriodWithPaymentDate("2020,3,1", "2020,3,7", "2020, 3, 7")),
      paymentWithPartialPeriod(0.0, 100.00, partialPeriodWithPaymentDate("2020,3,8", "2020,3,14", "2020,3,8", "2020,3,9", "2020, 3, 14"))
    )

    calculateCylb(nonFurloughPay, Weekly, cylbs, periods, determineNonFurloughPay) mustBe expected
  }

}
