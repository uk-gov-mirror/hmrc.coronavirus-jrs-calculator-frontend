/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import models.PaymentFrequency._

case class CylbOperators(fullPeriodLength: Int, daysFromPrevious: Int, daysFromCurrent: Int)

protected sealed trait FixedLength {
  def fullPeriodLength: Int
}

protected trait Weekly extends FixedLength {
  override def fullPeriodLength: Int = 7
}

protected trait Fortnightly extends FixedLength {
  override def fullPeriodLength: Int = 14
}

protected trait FourWeekly extends FixedLength {
  override def fullPeriodLength: Int = 28
}

protected trait FullPeriodCylb { this: FixedLength =>
  def equivalentPeriodDays: Int = fullPeriodLength - previousPeriodDays

  def previousPeriodDays: Int = 2
}

protected trait PartialPeriodCylb { this: FixedLength =>
  def partial: PartialPeriod

  def equivalentPeriodDays: Int = partial.partial.countDays - previousPeriodDays

  def previousPeriodDays: Int =
    if (partial.isFurloughStart) {
      if (partial.partial.countDays < (partial.original.countDays - 1)) 0 else 1
    } else { 2 }
}

trait CylbDuration {
  def fullPeriodLength: Int
  def equivalentPeriodDays: Int
  def previousPeriodDays: Int
}

object CylbDuration {

  object WeeklyFullCylb extends CylbDuration with FullPeriodCylb with Weekly
  object FortnightlyFullCylb extends CylbDuration with FullPeriodCylb with Fortnightly
  object FourweeklyFullCylb extends CylbDuration with FullPeriodCylb with FourWeekly
  case class MonthlyFullCylb(fullPeriod: FullPeriod) extends CylbDuration {
    val fullPeriodLength: Int = fullPeriod.period.countDays
    override def equivalentPeriodDays: Int = fullPeriodLength
    override def previousPeriodDays: Int = 0
  }

  case class WeeklyPartialCylb(partial: PartialPeriod) extends CylbDuration with PartialPeriodCylb with Weekly
  case class FortnightlyPartialCylb(partial: PartialPeriod) extends CylbDuration with PartialPeriodCylb with Fortnightly
  case class FourweeklyPartialCylb(partial: PartialPeriod) extends CylbDuration with PartialPeriodCylb with FourWeekly
  case class MonthlyPartialCylb(partialPeriod: PartialPeriod) extends CylbDuration {
    val fullPeriodLength: Int = partialPeriod.original.countDays
    override def equivalentPeriodDays: Int = partialPeriod.partial.countDays
    override def previousPeriodDays: Int = 0
  }

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
