/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
