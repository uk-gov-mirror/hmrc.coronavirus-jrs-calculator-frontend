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

package utils

import java.time.LocalDate

import base.SpecBase
import models.{PaymentDate, TaxYearEnding2020, TaxYearEnding2021}

class TaxYearFinderSpec extends SpecBase {

  "Returns TaxYear for a given PayPeriod" in new TaxYearFinder {
    taxYearAt(PaymentDate(LocalDate.of(2020, 3, 31))) mustBe TaxYearEnding2020
    taxYearAt(PaymentDate(LocalDate.of(2020, 4, 30))) mustBe TaxYearEnding2021
    taxYearAt(PaymentDate(LocalDate.of(2020, 4, 5))) mustBe TaxYearEnding2020
    taxYearAt(PaymentDate(LocalDate.of(2020, 4, 6))) mustBe TaxYearEnding2021
    taxYearAt(PaymentDate(LocalDate.of(2020, 4, 7))) mustBe TaxYearEnding2021
  }
}
