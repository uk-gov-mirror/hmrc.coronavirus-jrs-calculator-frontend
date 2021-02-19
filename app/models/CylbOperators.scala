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
//  {
//
//    // Note:
//    // 2021 period is:             Monday to Sunday 1/3/2021 to 7/3/2021
//    // 2019 period equivalent is:  Monday to Sunday 4/3/2019 to 10/3/2019
//    // Claim Period                                 4/3/2019 to 7/3/2019    (0 DAYS PRIOR AND 4 DAYS EQUIVALENT)
//    // Claim Period                                 1/3/2019 to 7/3/2019    (3 DAYS PRIOR AND 4 DAYS EQUIVALENT)
//    // Claim Period                                 1/3/2019 to 1/3/2019    (1 DAY  PRIOR AND 0 DAYS EQUIVALENT)
//
//    // Days Prior Calc
//    // ===============
//    //
//    //       1/3/2019             4/3/2019
//    //   if(startDateClaim) > AdjustedStartDate { 0 } else {
//    //           7/3/2019         4/3/2019
//    //      if(endDateClaim > AdjustedStartDate) {
//    //         AdjustedStartDate - startDateClaim  = 3 DAYS
//    //      } else {
//    //         AdjustedPeriod.length
//    //      }
//    //   }
//
////    extract(CylbDuration(Weekly, fullPeriod("2021,3,1", "2021,3,7"))) mustBe Tuple3(7, 4, 3)
////
////    extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,3" -> "2021,3,7"))) mustBe
////      Tuple3(7, 4, 1)
////
////    extract(CylbDuration(Weekly, partialPeriod("2021,3,1" -> "2021,3,7", "2021,3,4" -> "2021,3,7"))) mustBe
////      Tuple3(7, 4, 0)
//
////    val yearShifted = partial.partial.substractYears(partial.period.yearsBetweenPolicyStartAndPeriodEnd + 1)
//
////
////       <- 4/3/2019 ->
////    1/3/2019 -> 7/3/2019
//
////    val priorDays = if (yearShifted.start.isAfter(adjustedPeriod.start)) { 0 } else {
////      if (yearShifted.end.isAfter(adjustedPeriod.start)) {
////        ChronoUnit.DAYS.between(adjustedPeriod.start, yearShifted.start).abs
////      } else {
////        ChronoUnit.DAYS.between(adjustedPeriod.start, adjustedPeriod.end)
////      }
////    }
//
////    logger.debug(
////      s"[PartialPeriodCylb][previousPeriodDays] Values:" +
////        s"\n - partial.partial = ${partial.partial}" +
////        s"\n - partial.original = ${partial.original}" +
////        s"\n - partial.isFurloughStart = ${partial.isFurloughStart}" +
////        s"\n - partial.partial.countDays = ${partial.partial.countDays}" +
////        s"\n - partial.original.countDays = ${partial.original.countDays}")
//
//    if (partial.isFurloughStart) {
//      if (partial.partial.countDays < (partial.original.countDays - 1)) {
//        0
//      } else {
//        partial.period.yearsBetweenPolicyStartAndPeriodEnd + 1
//      }
//    } else {
//      //This is not leap year safe from 2024 onwards but this should not be an issue
//      partial.period.yearsBetweenPolicyStartAndPeriodEnd + 2
//    }
//  }
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
