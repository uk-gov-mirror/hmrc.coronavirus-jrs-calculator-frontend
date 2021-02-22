/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import models.PaymentFrequency._

case class CylbOperators(fullPeriodLength: Int, daysFromPrevious: Int, daysFromCurrent: Int)

protected sealed trait FixedLength {
  def fullPeriodLength: Int

  def equivalentPeriodCalculator(claimPeriod: Period, partialPeriod: Option[Period]) = {

    val yearsBetweenPolicyStartAndClaimPeriod = claimPeriod.yearsBetweenPolicyStartAndPeriodEnd + 1

    val adjustedPeriod = claimPeriod.substract52Weeks(yearsBetweenPolicyStartAndClaimPeriod)
    val yearShift = partialPeriod.getOrElse(claimPeriod).substractYears(yearsBetweenPolicyStartAndClaimPeriod)

    val adjustedStartDateDayOfYear = adjustedPeriod.start.getDayOfYear
    val claimStartDayOfYear = yearShift.start.getDayOfYear
    val claimEndDayOfYear = yearShift.end.getDayOfYear
    val claimDuration = yearShift.countDays

    val priorDays = (adjustedStartDateDayOfYear - claimStartDayOfYear).min(claimDuration) max 0
    val afterDays = (claimEndDayOfYear - (adjustedStartDateDayOfYear - 1)).min(claimDuration) max 0

    priorDays -> afterDays
  }
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
  def fullPeriod: FullPeriod

  def equivalentPeriod = equivalentPeriodCalculator(fullPeriod.period, None)

  def equivalentPeriodDays: Int = equivalentPeriod._2

  def previousPeriodDays: Int = equivalentPeriod._1
}

protected trait PartialPeriodCylb { this: FixedLength =>
  def partial: PartialPeriod

  def equivalentPeriod = equivalentPeriodCalculator(partial.period, Some(partial.partial))

  def equivalentPeriodDays: Int = equivalentPeriod._2

  def previousPeriodDays: Int = equivalentPeriod._1
}

trait CylbDuration {
  def fullPeriodLength: Int
  def equivalentPeriodDays: Int
  def previousPeriodDays: Int
}

object CylbDuration {

  case class WeeklyFullCylb(override val fullPeriod: FullPeriod) extends CylbDuration with FullPeriodCylb with Weekly
  case class FortnightlyFullCylb(override val fullPeriod: FullPeriod) extends CylbDuration with FullPeriodCylb with Fortnightly
  case class FourweeklyFullCylb(override val fullPeriod: FullPeriod) extends CylbDuration with FullPeriodCylb with FourWeekly
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
      case (Weekly, full: FullPeriod)            => WeeklyFullCylb(full)
      case (Weekly, partial: PartialPeriod)      => WeeklyPartialCylb(partial)
      case (FortNightly, full: FullPeriod)       => FortnightlyFullCylb(full)
      case (FortNightly, partial: PartialPeriod) => FortnightlyPartialCylb(partial)
      case (FourWeekly, full: FullPeriod)        => FourweeklyFullCylb(full)
      case (FourWeekly, partial: PartialPeriod)  => FourweeklyPartialCylb(partial)
      case (Monthly, full: FullPeriod)           => MonthlyFullCylb(full)
      case (Monthly, partial: PartialPeriod)     => MonthlyPartialCylb(partial)
    }

}
