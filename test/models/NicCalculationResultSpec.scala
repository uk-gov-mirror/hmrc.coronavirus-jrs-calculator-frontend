/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import base.SpecBase
import play.api.libs.json.Json
import NicCalculationResult._

class NicCalculationResultSpec extends SpecBase {

  "serialize/deserialize from/to json" in {
    val period = PayPeriod(LocalDate.now, LocalDate.now)
    val nicCalculationResult = NicCalculationResult(0.10, Seq(PayPeriodBreakdown(123.00, period)))
    val expectedJsValue = Json.parse(
      """{"total":0.1,"payPeriodBreakdowns":[{"amount":123,"payPeriod":{"start":"2020-04-12","end":"2020-04-12"}}]}""")

    Json.toJson(nicCalculationResult) mustBe expectedJsValue
  }
}
