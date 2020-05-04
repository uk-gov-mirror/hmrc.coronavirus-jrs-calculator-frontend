/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import models.{FurloughDates, FurloughEnded, FurloughOngoing, FurloughWithinClaim, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import utils.LocalDateHelpers

trait FurloughPeriodExtractor extends LocalDateHelpers {

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

  def extractFurloughPeriod(userAnswers: UserAnswers): Option[FurloughDates] =
    userAnswers.get(FurloughStartDatePage).map(FurloughDates(_, userAnswers.get(FurloughEndDatePage)))
}
