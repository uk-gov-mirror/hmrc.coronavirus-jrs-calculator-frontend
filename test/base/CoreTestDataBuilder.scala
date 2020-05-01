/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package base

import java.time.LocalDate

import models.FurloughStatus.FurloughOngoing
import models.NicCategory.Payable
import models.PayMethod.Regular
import models.PaymentFrequency.Monthly
import models.{Amount, FullPeriod, FullPeriodWithPaymentDate, JourneyCoreData, MandatoryData, PartialPeriod, PartialPeriodWithPaymentDate, PayMethod, PaymentDate, PaymentWithFullPeriod, PaymentWithPartialPeriod, PensionStatus, Period, UserAnswers}
import pages._

trait CoreTestDataBuilder {

  def period(start: String, end: String) =
    Period(buildLocalDate(periodBuilder(start)), buildLocalDate(periodBuilder(end)))

  def partialPeriod(original: (String, String), partial: (String, String)) =
    PartialPeriod(period(original._1, original._2), period(partial._1, partial._2))

  def fullPeriod(start: String, end: String) = FullPeriod(period(start, end))

  def paymentWithFullPeriod(furloughPayment: BigDecimal, period: FullPeriodWithPaymentDate, payMethod: PayMethod): PaymentWithFullPeriod =
    PaymentWithFullPeriod(Amount(furloughPayment), period, payMethod)

  def paymentWithPartialPeriod(
    nonFurloughPay: BigDecimal,
    furloughPayment: BigDecimal,
    period: PartialPeriodWithPaymentDate,
    payMethod: PayMethod): PaymentWithPartialPeriod =
    PaymentWithPartialPeriod(Amount(nonFurloughPay), Amount(furloughPayment), period, payMethod)

  def fullPeriodWithPaymentDate(start: String, end: String, paymentDate: String): FullPeriodWithPaymentDate =
    FullPeriodWithPaymentDate(FullPeriod(period(start, end)), PaymentDate(buildLocalDate(periodBuilder(paymentDate))))

  def partialPeriodWithPaymentDate(
    start: String,
    end: String,
    pstart: String,
    pend: String,
    paymentDate: String): PartialPeriodWithPaymentDate =
    PartialPeriodWithPaymentDate(
      PartialPeriod(period(start, end), period(pstart, pend)),
      PaymentDate(buildLocalDate(periodBuilder(paymentDate))))

  def paymentDate(date: String): PaymentDate = PaymentDate(buildLocalDate(periodBuilder(date)))

  private val periodBuilder: String => Array[Int] =
    date => date.replace(" ", "").replace("-", ",").split(",").map(_.toInt)

  private val buildLocalDate: Array[Int] => LocalDate = array => LocalDate.of(array(0), array(1), array(2))

  private val claimPeriod: Period = period("2020-3-1", "2020-3-31")
  private val furloughStart = buildLocalDate(periodBuilder("2020-3-1"))
  private val lastPayDate = buildLocalDate(periodBuilder("2020-3-31"))
  val defaultedMandatoryData =
    MandatoryData(
      claimPeriod,
      Monthly,
      Payable,
      PensionStatus.DoesContribute,
      Regular,
      FurloughOngoing,
      Seq.empty,
      furloughStart,
      lastPayDate)

  val defaultJourneyCoreData =
    JourneyCoreData(
      claimPeriod,
      Seq(fullPeriodWithPaymentDate("2020-3-1", "2020-3-31", "2020-3-31")),
      Monthly,
      Payable,
      PensionStatus.DoesContribute)

  val mandatoryAnswers = UserAnswers("id")
    .set(ClaimPeriodStartPage, LocalDate.of(2020, 3, 1))
    .get
    .set(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))
    .get
    .set(PaymentFrequencyPage, Monthly)
    .get
    .set(NicCategoryPage, Payable)
    .get
    .set(PensionStatusPage, PensionStatus.DoesContribute)
    .get
    .set(PayMethodPage, Regular)
    .get
    .set(FurloughStatusPage, FurloughOngoing)
    .get
    .set(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
    .get
    .set(LastPayDatePage, LocalDate.of(2020, 3, 31))
    .get
    .setListWithInvalidation(PayDatePage, LocalDate.of(2020, 2, 29), 1)
    .get
    .setListWithInvalidation(PayDatePage, LocalDate.of(2020, 3, 31), 2)
    .get
}
