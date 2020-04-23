/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.{Period, UserAnswers}
import pages._
import services.{PeriodHelper, PreviousYearPeriod}

trait LastYearPayControllerRequestHandler extends DataExtractor with PeriodHelper with PreviousYearPeriod {

  def getPayDates(userAnswers: UserAnswers): Option[Seq[LocalDate]] =
    for {
      frequency      <- userAnswers.get(PaymentFrequencyPage)
      lastPayDay     <- userAnswers.get(LastPayDatePage)
      furloughPeriod <- extractFurloughPeriod(userAnswers)
    } yield {
      val payDates = userAnswers.getList(PayDatePage)
      val periods = generatePeriods(payDates, furloughPeriod)
      val periodsWithPayDates = assignPayDates(frequency, periods, lastPayDay)
      val datesWithDuplicates = periodsWithPayDates.flatMap(p => previousYearPayDate(frequency, p))
      datesWithDuplicates.distinct
    }

  private def extractFurloughPeriod(userAnswers: UserAnswers) =
    for {
      furloughStart  <- userAnswers.get(FurloughStartDatePage)
      claimPeriodEnd <- userAnswers.get(ClaimPeriodEndPage)
    } yield {
      userAnswers.get(FurloughEndDatePage) match {
        case Some(furloughEnd) => Period(furloughStart, furloughEnd)
        case None              => Period(furloughStart, claimPeriodEnd)
      }
    }

}
