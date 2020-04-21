/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.{PeriodWithPaymentDate, Periods, UserAnswers}
import services.{PeriodHelper, PreviousYearPeriod}

trait LastYearPayControllerRequestHandler extends DataExtractor with PeriodHelper with PreviousYearPeriod {

  def getPayDates(userAnswers: UserAnswers): Option[Seq[LocalDate]] =
    for {
      data                                            <- extract(userAnswers)
      furloughPeriod                                  <- extractFurloughPeriod(data, userAnswers)
      periods: Seq[Periods]                           <- Some(generatePeriods(data.payDates, furloughPeriod))
      periodsWithPayDates: Seq[PeriodWithPaymentDate] <- Some(assignPayDates(data.paymentFrequency, periods, data.lastPayDay))
    } yield {
      val datesWithDuplicates = periodsWithPayDates.flatMap(p => previousYearPayDate(data.paymentFrequency, p.paymentDate.value))
      datesWithDuplicates.distinct
    }

}
