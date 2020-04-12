/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import base.SpecBase
import models.NicCalculationResult._
import play.api.libs.json.Json

class NicCalculationResultSpec extends SpecBase {

  "serialize/deserialize from/to json" in {
    val paymentDate = PaymentDate(LocalDate.now)
    val nicCalculationResult = NicCalculationResult(0.10, Seq(PaymentDateBreakdown(123.00, paymentDate)))
    val expectedJsValue =
      Json.parse("""{"total":0.1,"paymentDateBreakdowns":[{"amount":123,"paymentDate": "2020-04-12"}]}""")

    Json.toJson(nicCalculationResult) mustBe expectedJsValue
  }
}
