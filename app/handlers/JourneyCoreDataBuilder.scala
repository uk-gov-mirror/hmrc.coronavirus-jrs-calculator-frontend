/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.{JourneyCoreData, MandatoryData, PaymentFrequency, Period, PeriodWithPaymentDate, Periods}

trait JourneyCoreDataBuilder {

  def build(
    data: MandatoryData,
    generatePeriods: (Seq[LocalDate], Period) => Seq[Periods],
    assign: (PaymentFrequency, Seq[Periods], LocalDate) => Seq[PeriodWithPaymentDate],
    furloughPeriod: Period): JourneyCoreData = {
    import data._

    val generatedPeriods: Seq[Periods] = generatePeriods(payDates, furloughPeriod)

    JourneyCoreData(furloughPeriod, assign(paymentFrequency, generatedPeriods, lastPayDay), paymentFrequency, nicCategory, pensionStatus)
  }

}
