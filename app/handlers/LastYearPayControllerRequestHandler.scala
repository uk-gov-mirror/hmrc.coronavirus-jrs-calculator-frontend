/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.UserAnswers
import pages._
import services.{FurloughPeriodHelper, PeriodHelper, PreviousYearPeriod}

trait LastYearPayControllerRequestHandler extends PeriodHelper with PreviousYearPeriod with FurloughPeriodHelper {

  def getPayDates(userAnswers: UserAnswers): Option[Seq[LocalDate]] =
    for {
      frequency      <- userAnswers.get(PaymentFrequencyPage)
      lastPayDay     <- userAnswers.get(LastPayDatePage)
      furloughPeriod <- extractRelevantFurloughPeriod(userAnswers)
    } yield {
      val payDates = userAnswers.getList(PayDatePage)
      val periods = generatePeriods(payDates, furloughPeriod)
      val periodsWithPayDates = assignPayDates(frequency, periods, lastPayDay)
      val datesWithDuplicates = periodsWithPayDates.flatMap(p => previousYearPayDate(frequency, p))
      datesWithDuplicates.distinct
    }

}
