/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.{FurloughDates, FurloughEnded, FurloughOngoing, FurloughWithinClaim, Period, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import utils.LocalDateHelpers

trait FurloughPeriodExtractor extends LocalDateHelpers {

  def extractFurloughPeriod(userAnswers: UserAnswers): Option[FurloughDates] =
    for {
      furloughStart <- userAnswers.get(FurloughStartDatePage)
    } yield {
      FurloughDates(furloughStart, userAnswers.get(FurloughEndDatePage))
    }

  def extractFurloughWithinClaim(userAnswers: UserAnswers): Option[FurloughWithinClaim] =
    for {
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
      furloughDates    <- extractFurloughPeriod(userAnswers)
    } yield {
      val startDate = latestOf(claimPeriodStart, furloughDates.start)
      val endDate = furloughDates match {
        case FurloughOngoing(_)            => claimPeriodEnd
        case FurloughEnded(_, furloughEnd) => earliestOf(claimPeriodEnd, furloughEnd)
      }
      FurloughWithinClaim(startDate, endDate)
    }
}
