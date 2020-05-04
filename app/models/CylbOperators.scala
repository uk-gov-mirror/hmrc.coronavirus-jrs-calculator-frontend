/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import models.PaymentFrequency._

case class CylbOperators(fullPeriodLength: Int, daysFromPrevious: Int, daysFromCurrent: Int)

private sealed trait FixedLength {
  def fullPeriodLength: Int
}

private trait Weekly extends FixedLength {
  override def fullPeriodLength: Int = 7
}

private trait Fortnightly extends FixedLength {
  override def fullPeriodLength: Int = 14
}

private trait FourWeekly extends FixedLength {
  override def fullPeriodLength: Int = 28
}

private trait FullPeriodCylb { this: FixedLength =>
  def equivalentPeriodDays: Int = fullPeriodLength - previousPeriodDays

  def previousPeriodDays: Int = 2
}

private trait PartialPeriodCylb { this: FixedLength =>
  def partial: PartialPeriod

  def equivalentPeriodDays: Int = partial.partial.countDays - previousPeriodDays

  def previousPeriodDays: Int =
    if (partial.isFurloughStart) {
      if (partial.partial.countDays < (partial.original.countDays - 1)) 0 else 1
    } else { 2 }
}

private object WeeklyFullCylb extends CylbDuration with FullPeriodCylb with Weekly
private object FortnightlyFullCylb extends CylbDuration with FullPeriodCylb with Fortnightly
private object FourweeklyFullCylb extends CylbDuration with FullPeriodCylb with FourWeekly
private case class MonthlyFullCylb(fullPeriod: FullPeriod) extends CylbDuration {
  val fullPeriodLength: Int = fullPeriod.period.countDays
  override def equivalentPeriodDays: Int = fullPeriodLength
  override def previousPeriodDays: Int = 0
}

private case class WeeklyPartialCylb(partial: PartialPeriod) extends CylbDuration with PartialPeriodCylb with Weekly
private case class FortnightlyPartialCylb(partial: PartialPeriod) extends CylbDuration with PartialPeriodCylb with Fortnightly
private case class FourweeklyPartialCylb(partial: PartialPeriod) extends CylbDuration with PartialPeriodCylb with FourWeekly
private case class MonthlyPartialCylb(partialPeriod: PartialPeriod) extends CylbDuration {
  val fullPeriodLength: Int = partialPeriod.original.countDays
  override def equivalentPeriodDays: Int = partialPeriod.partial.countDays
  override def previousPeriodDays: Int = 0
}

trait CylbDuration {
  def fullPeriodLength: Int
  def equivalentPeriodDays: Int
  def previousPeriodDays: Int
}

object CylbDuration {

  def apply(paymentFrequency: PaymentFrequency, period: Periods): CylbDuration =
    (paymentFrequency, period) match {
      case (Weekly, _: FullPeriod)               => WeeklyFullCylb
      case (Weekly, partial: PartialPeriod)      => WeeklyPartialCylb(partial)
      case (FortNightly, _: FullPeriod)          => FortnightlyFullCylb
      case (FortNightly, partial: PartialPeriod) => FortnightlyPartialCylb(partial)
      case (FourWeekly, _: FullPeriod)           => FourweeklyFullCylb
      case (FourWeekly, partial: PartialPeriod)  => FourweeklyPartialCylb(partial)
      case (Monthly, full: FullPeriod)           => MonthlyFullCylb(full)
      case (Monthly, partial: PartialPeriod)     => MonthlyPartialCylb(partial)
    }

}
