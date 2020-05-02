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

  // TODO: Equivalent to extractFurloughPeriod - rename after that is removed
  def extractFurloughPeriod2(userAnswers: UserAnswers): Option[FurloughDates] =
    for {
      furloughStart <- userAnswers.get(FurloughStartDatePage)
    } yield {
      FurloughDates(furloughStart, userAnswers.get(FurloughEndDatePage))
    }

  def extractFurloughWithinClaim(userAnswers: UserAnswers): Option[FurloughWithinClaim] =
    for {
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
      furloughDates    <- extractFurloughPeriod2(userAnswers)
    } yield {
      val startDate = latestOf(claimPeriodStart, furloughDates.start)
      val endDate = furloughDates match {
        case FurloughOngoing(_)            => claimPeriodEnd
        case FurloughEnded(_, furloughEnd) => earliestOf(claimPeriodEnd, furloughEnd)
      }
      FurloughWithinClaim(startDate, endDate)
    }

  // TODO: Remove after refactor
  def extractFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart  <- userAnswers.get(FurloughStartDatePage)
      claimPeriodEnd <- userAnswers.get(ClaimPeriodEndPage)
    } yield
      userAnswers
        .get(FurloughEndDatePage)
        .fold(Period(furloughStart, claimPeriodEnd))(furloughEnd => Period(furloughStart, furloughEnd))

  // TODO: Remove after refactor
  def extractRelevantFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart    <- userAnswers.get(FurloughStartDatePage)
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
    } yield extractRelevantFurloughPeriod(furloughStart, userAnswers.get(FurloughEndDatePage), claimPeriodStart, claimPeriodEnd)

  // TODO: Remove after refactor
  def extractRelevantFurloughPeriod(
    furloughStart: LocalDate,
    furloughEnd: Option[LocalDate],
    claimPeriodStart: LocalDate,
    claimPeriodEnd: LocalDate): Period = {
    val effectiveStartDate = latestOf(claimPeriodStart, furloughStart)
    furloughEnd.fold(Period(effectiveStartDate, claimPeriodEnd))(furloughEnd => Period(effectiveStartDate, furloughEnd))
  }
}
