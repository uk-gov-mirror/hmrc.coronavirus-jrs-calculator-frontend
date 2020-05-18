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

package views.includes

import base.SpecBase
import models._
import play.api.i18n.Messages
import play.api.test.Helpers

class FurloughCapHelperSpec extends SpecBase {

  val instance = new FurloughCapHelper()
  implicit val messages: Messages = Helpers.stubMessages(
    Helpers.stubMessagesApi(
      Map(
        "en" -> Map(
          "furloughBreakdown.furloughCap.periodSpansMonthCap" -> "{0}|{1}|{2}|{3}|{4}|{5}|{6}",
          "furloughBreakdown.furloughCap.partialPeriodCap"    -> "{0}|{1}|{2}|{3}"
        ))
    )
  )

  "calculationFor" must {

    "return None for FullPeriodCap" in {
      instance.calculationFor(FullPeriodCap(100.00)) mustBe None
    }

    "return a templated message for PeriodSpansMonthCap" in {
      val cap = PeriodSpansMonthCap(2621.15, 17, 3, 80.65, 15, 4, 83.34)

      instance.calculationFor(cap).value mustBe "17|March|80.65|15|April|83.34|2621.15"
    }

    "round the values for PeriodSpansMonthCap" in {
      val cap = PeriodSpansMonthCap(2621.150, 17, 3, 80.650, 15, 4, 83.340)

      instance.calculationFor(cap).value mustBe "17|March|80.65|15|April|83.34|2621.15"
    }

    "return a templated message for PartialPeriodCap" in {
      val cap = PartialPeriodCap(1774.30, 22, 3, 80.65)

      instance.calculationFor(cap).value mustBe "22|March|80.65|1774.30"
    }

    "round the values for PartialPeriodCap" in {
      val cap = PartialPeriodCap(1774.3, 22, 3, 80.650)

      instance.calculationFor(cap).value mustBe "22|March|80.65|1774.30"
    }

  }

}
