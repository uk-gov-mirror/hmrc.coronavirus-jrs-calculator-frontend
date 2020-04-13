/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import base.SpecBase
import models.CalculationResult._
import play.api.libs.json.Json

class CalculationResultSpec extends SpecBase {

  "serialize/deserialize from/to json" in {
    val paymentDate = PaymentDate(LocalDate.now)
    val nicCalculationResult = CalculationResult(0.10, Seq(PaymentDateBreakdown(123.00, paymentDate)))
    val expectedJsValue =
      Json.parse(s"""{"total":0.1,"paymentDateBreakdowns":[{"amount":123,"paymentDate": "${paymentDate.value}"}]}""")

    Json.toJson(nicCalculationResult) mustBe expectedJsValue
  }
}
