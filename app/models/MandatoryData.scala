/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

case class MandatoryData(
  claimPeriod: Period,
  paymentFrequency: PaymentFrequency,
  nicCategory: NicCategory,
  payStatus: PensionStatus,
  payMethod: PayMethod,
  furloughOngoing: FurloughStatus,
  payDates: Seq[LocalDate],
  furloughStart: LocalDate,
  lastPayDay: LocalDate)
