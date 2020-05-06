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
import models.Amount
import Calculators._

class CalculatorsSpec extends SpecBase {

  "Calculates 80% of a given amount" in new Calculators {
    eightyPercent(Amount(1000.0)) mustBe Amount(800.0)
  }

  "Daily calculation rounded half up" in new Calculators {
    dailyCalculation(Amount(3000.155), 30, 10) mustBe Amount(1000.05)
  }

  "return 80% of an amount if lesser than cap allowance unrounded or cap otherwise" in new Calculators {
    claimableAmount(Amount(100.15), 99) mustBe Amount(80.120)
    claimableAmount(Amount(100.00), 79) mustBe Amount(79)
  }

  "Round an amount HALF_UP" in new Calculators {
    Amount(1000.5111).halfUp mustBe Amount(1000.51)
    Amount(1000.4999).halfUp mustBe Amount(1000.50)
  }
}
