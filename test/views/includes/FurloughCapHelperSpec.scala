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
          "furloughBreakdown.furloughCap.fullPeriodCap"                        -> "{0}",
          "phaseTwoFurloughBreakdown.furloughCap.fullPeriodCap.partTime"       -> "{0}|{1}|{2}|{3}",
          "furloughBreakdown.furloughCap.periodSpansMonthCap"                  -> "{0}|{1}|{2}|{3}|{4}|{5}|{6}",
          "phaseTwoFurloughBreakdown.furloughCap.periodSpansMonthCap.partTime" -> "{0}|{1}|{2}|{3}|{4}|{5}|{6}|{7}|{8}",
          "furloughBreakdown.furloughCap.partialPeriodCap"                     -> "{0}|{1}|{2}|{3}",
          "phaseTwoFurloughBreakdown.furloughCap.partialPeriodCap.partTime"    -> "{0}|{1}|{2}|{3}|{4}|{5}"
        ))
    )
  )

  "calculationFor" must {

    "return a templated message for FullPeriodCap" in {
      instance.calculationFor(FullPeriodCap(100.00)) mustBe "100.00"
    }

    "return a templated message for FullPeriodCapWithPartTime" in {
      instance.calculationFor(FullPeriodCapWithPartTime(100.00, 200.00, 20.00, 10.00)) mustBe "200.00|20.00|10.00|100.00"
    }

    "return a templated message for PeriodSpansMonthCap" in {
      val cap = PeriodSpansMonthCap(2621.15, 17, 3, 80.65, 15, 4, 83.34)

      instance.calculationFor(cap) mustBe "17|March|80.65|15|April|83.34|2621.15"
    }

    "return a templated message for PeriodSpansMonthCapWithPartTime" in {
      val cap = PeriodSpansMonthCapWithPartTime(2621.15, 17, 3, 80.65, 15, 4, 83.34, 0.00, 0.00, 0.00)

      instance.calculationFor(cap) mustBe "17|March|80.65|15|April|83.34|0.00|0.00|2621.15"
    }

    "round the values for PeriodSpansMonthCap" in {
      val cap = PeriodSpansMonthCap(2621.150, 17, 3, 80.650, 15, 4, 83.340)

      instance.calculationFor(cap) mustBe "17|March|80.65|15|April|83.34|2621.15"
    }

    "return a templated message for PartialPeriodCap" in {
      val cap = PartialPeriodCap(1774.30, 22, 3, 80.65)

      instance.calculationFor(cap) mustBe "22|March|80.65|1774.30"
    }

    "return a templated message for PartialPeriodCapWithPartTime" in {
      val cap = PartialPeriodCapWithPartTime(1774.30, 22, 3, 80.65, 0.00, 0.00, 0.00)

      instance.calculationFor(cap) mustBe "22|March|80.65|0.00|0.00|1774.30"
    }

    "round the values for PartialPeriodCap" in {
      val cap = PartialPeriodCap(1774.3, 22, 3, 80.650)

      instance.calculationFor(cap) mustBe "22|March|80.65|1774.30"
    }

  }

}
