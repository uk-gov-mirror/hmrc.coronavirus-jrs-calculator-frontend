/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import base.SpecBase
import models.Amount._

class CommonCalculationServiceSpec extends SpecBase {

  "Returns zero for an amount lesser than threshold" in new CommonCalculationService {
    greaterThanAllowance(100.0.toAmount, 101.0, NiRate()) mustBe 0.0.toAmount
    greaterThanAllowance(99.0.toAmount, 100.0, PensionRate()) mustBe 0.0.toAmount
  }

  "Returns an ((amount - threshold) * rate) rounded half_up if greater than threshold" in new CommonCalculationService {
    greaterThanAllowance(1000.0.toAmount, 100.0, NiRate()) mustBe 124.20.toAmount
    greaterThanAllowance(1000.0.toAmount, 100.0, PensionRate()) mustBe 27.0.toAmount
  }
}
