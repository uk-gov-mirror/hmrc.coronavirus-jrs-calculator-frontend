/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{PartialPeriod, Period, UserAnswers}
import pages.PayDatePage

trait PartialPayHelper extends PeriodHelper with FurloughPeriodHelper {

  def hasPartialPayBefore(userAnswers: UserAnswers): Boolean =
    getPartialPeriods(userAnswers).exists(isFurloughStart)

  def hasPartialPayAfter(userAnswers: UserAnswers): Boolean =
    getPartialPeriods(userAnswers).exists(isFurloughEnd)

  def getPartialPeriods(userAnswers: UserAnswers): Seq[PartialPeriod] =
    extractRelevantFurloughPeriod(userAnswers).fold(
      Seq.empty[PartialPeriod]
    ) { furloughPeriod =>
      val payDates = userAnswers.getList(PayDatePage)
      generatePeriods(payDates, furloughPeriod).collect {
        case pp: PartialPeriod => pp
      }
    }

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
