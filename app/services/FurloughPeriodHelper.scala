/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import models.{Period, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import utils.LocalDateHelpers

trait FurloughPeriodHelper extends LocalDateHelpers {

  def extractFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart  <- userAnswers.get(FurloughStartDatePage)
      claimPeriodEnd <- userAnswers.get(ClaimPeriodEndPage)
    } yield {
      userAnswers.get(FurloughEndDatePage) match {
        case Some(furloughEnd) => Period(furloughStart, furloughEnd)
        case None              => Period(furloughStart, claimPeriodEnd)
      }
    }

  def extractRelevantFurloughPeriod(userAnswers: UserAnswers): Option[Period] =
    for {
      furloughStart    <- userAnswers.get(FurloughStartDatePage)
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
    } yield {
      extractRelevantFurloughPeriod(furloughStart, userAnswers.get(FurloughEndDatePage), claimPeriodStart, claimPeriodEnd)
    }

  def extractRelevantFurloughPeriod(
    furloughStart: LocalDate,
    furloughEnd: Option[LocalDate],
    claimPeriodStart: LocalDate,
    claimPeriodEnd: LocalDate): Period = {
    val effectiveStartDate = latestOf(claimPeriodStart, furloughStart)
    furloughEnd match {
      case Some(furloughEnd) => Period(effectiveStartDate, furloughEnd)
      case None              => Period(effectiveStartDate, claimPeriodEnd)
    }
  }

}
