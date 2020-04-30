/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.{Period, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import utils.LocalDateHelpers

trait FurloughPeriodExtractor extends LocalDateHelpers {

  def extractFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart  <- userAnswers.get(FurloughStartDatePage)
      claimPeriodEnd <- userAnswers.get(ClaimPeriodEndPage)
    } yield
      userAnswers
        .get(FurloughEndDatePage)
        .fold(Period(furloughStart, claimPeriodEnd))(furloughEnd => Period(furloughStart, furloughEnd))

  def extractRelevantFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart    <- userAnswers.get(FurloughStartDatePage)
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
    } yield extractRelevantFurloughPeriod(furloughStart, userAnswers.get(FurloughEndDatePage), claimPeriodStart, claimPeriodEnd)

  def extractRelevantFurloughPeriod(
    furloughStart: LocalDate,
    furloughEnd: Option[LocalDate],
    claimPeriodStart: LocalDate,
    claimPeriodEnd: LocalDate): Period = {
    val effectiveStartDate = latestOf(claimPeriodStart, furloughStart)
    furloughEnd.fold(Period(effectiveStartDate, claimPeriodEnd))(furloughEnd => Period(effectiveStartDate, furloughEnd))
  }
}
