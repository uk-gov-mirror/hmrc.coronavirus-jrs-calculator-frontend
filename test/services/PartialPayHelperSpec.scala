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

import base.{CoreTestDataBuilder, SpecBase}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PartialPayHelperSpec extends SpecBase with ScalaCheckPropertyChecks with CoreTestDataBuilder {

  "PartialPayHelper" when {

    "getPeriodRemainder" must {

      "return remainders with multiple days before the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5"  -> "2020,4,25",
          "2020,4,10" -> "2020,4,25"
        )
        val expected = period(
          "2020,4,5",
          "2020,4,9"
        )

        getPeriodRemainder(partial) mustBe expected
      }

      "return remainders with multiple days after the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5" -> "2020,4,25",
          "2020,4,5" -> "2020,4,15"
        )
        val expected = period(
          "2020,4,16",
          "2020,4,25"
        )

        getPeriodRemainder(partial) mustBe expected
      }

      "return remainders with a single day before the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5" -> "2020,4,25",
          "2020,4,6" -> "2020,4,25"
        )
        val expected = period(
          "2020,4,5",
          "2020,4,5"
        )

        getPeriodRemainder(partial) mustBe expected
      }

      "return remainders with a single day after the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5" -> "2020,4,25",
          "2020,4,5" -> "2020,4,24"
        )
        val expected = period(
          "2020,4,25",
          "2020,4,25"
        )

        getPeriodRemainder(partial) mustBe expected
      }

    }

  }

}
