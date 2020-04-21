/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import models.Amount

import scala.math.BigDecimal.RoundingMode.RoundingMode

object AmountRounding {

  val roundWithMode: (BigDecimal, RoundingMode) => BigDecimal = (value, mode) => value.setScale(2, mode)

  val roundWithModeReturnAmount: (BigDecimal, RoundingMode) => Amount = (value, mode) => Amount(value.setScale(2, mode))

  val roundAmountWithMode: (Amount, RoundingMode) => Amount =
    (value, mode) => Amount(value.value.setScale(2, mode))

}
