/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{PartialPeriod, Period, UserAnswers}
import pages.{ClaimPeriodEndPage, FurloughEndDatePage, FurloughStartDatePage, PayDatePage}

trait PartialPayHelper extends PeriodHelper {

  def hasPartialPayBefore(userAnswers: UserAnswers): Boolean =
    getPartialPeriods(userAnswers).exists(isFurloughStart)

  def hasPartialPayAfter(userAnswers: UserAnswers): Boolean =
    getPartialPeriods(userAnswers).exists(isFurloughEnd)

  def getPartialPeriods(userAnswers: UserAnswers): Seq[PartialPeriod] = {
    for {
      furloughStart  <- userAnswers.get(FurloughStartDatePage)
      claimPeriodEnd <- userAnswers.get(ClaimPeriodEndPage)
    } yield {
      val furloughPeriod = userAnswers.get(FurloughEndDatePage) match {
        case Some(furloughEnd) => Period(furloughStart, furloughEnd)
        case None              => Period(furloughStart, claimPeriodEnd)
      }
      val payDates = userAnswers.getList(PayDatePage)

      generatePeriods(payDates, furloughPeriod).collect {
        case pp: PartialPeriod => pp
      }
    }
  }.getOrElse(Seq.empty)

  def getPeriodRemainder(partialPeriod: PartialPeriod): Period = {
    val original = partialPeriod.original
    val partial = partialPeriod.partial

    if (partial.start.isAfter(original.start)) {
      Period(original.start, partial.start.minusDays(1))
    } else {
      Period(partial.end.plusDays(1), original.end)
    }
  }

}
