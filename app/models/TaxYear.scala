/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

sealed trait TaxYear
case object TaxYearEnding2020 extends TaxYear
case object TaxYearEnding2021 extends TaxYear
