/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.coronavirus.jrs.calculator.models

import java.time.LocalDate

case class ClaimPeriod(startDate: LocalDate, endDate: LocalDate)
