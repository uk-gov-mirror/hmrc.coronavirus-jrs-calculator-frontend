/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.coronavirus.jrs.calculator.controllers

import play.api.test.FakeRequest
import uk.gov.hmrc.coronavirus.jrs.calculator.helpers.BaseSpec
import uk.gov.hmrc.coronavirus.jrs.calculator.views.html._
import play.api.test.Helpers._

class CalculatorControllerSpec extends BaseSpec {

  val warmUpView = injector.instanceOf[warm_up]
  val controller = new CalculatorController(mcc, warmUpView)

  "warmUp" must {
    "show warmup page" in {
      val result = controller.warmUp(FakeRequest("GET", "/coronavirus-job-retention-scheme/calculator"))

      status(result) mustBe 200
    }
  }

}
