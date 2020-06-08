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

package models

import java.time.LocalDate

import org.scalatest.{MustMatchers, OptionValues, WordSpec}

class PeriodSpec extends WordSpec with MustMatchers with OptionValues {

  "PhaseTwoPeriod" must {

    val periodWithPaymentDate = FullPeriodWithPaymentDate(
      FullPeriod(
        Period(LocalDate.of(2020, 7, 1), LocalDate.of(2020, 7, 31))
      ),
      PaymentDate(LocalDate.of(2020, 7, 31))
    )

    "isPartTime" should {

      "be true if there are actual hours and usual hours" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(10.0)), Some(Hours(20.0)))
        period.isPartTime mustBe true
      }

      "be false if there are actual hours and no usual hours" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(10.0)), None)
        period.isPartTime mustBe false
      }

      "be false if there are usual hours and no actual hours" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, None, Some(Hours(10.0)))
        period.isPartTime mustBe false
      }

      "be false if there are no usual or actual hours" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, None, None)
        period.isPartTime mustBe false
      }
    }

    "furloughed" should {

      "return number of furloughed hours when actual is less than usual" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(15.5)), Some(Hours(20.2)))
        period.furloughed mustBe 4.7
      }

      "return zero when actual is the same as usual" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(15.5)), Some(Hours(15.5)))
        period.furloughed mustBe 0.0
      }

      "return zero when actual is greater than usual" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(20.2)), Some(Hours(15.5)))
        period.furloughed mustBe 0.0
      }

    }

    "isFullTime" should {

      "be true if actual hours is greater than usual hours" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(20.2)), Some(Hours(15.5)))
        period.isFullTime mustBe true
      }

      "be false if actual hours is lower than usual hours" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(15.5)), Some(Hours(20.2)))
        period.isFullTime mustBe false
      }

      "be false if actual hours missing" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, None, Some(Hours(20.2)))
        period.isFullTime mustBe false
      }

      "be false if usual hours missing" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, Some(Hours(20.2)), None)
        period.isFullTime mustBe false
      }

      "be false if usual and actual hours are missing" in {
        val period = PhaseTwoPeriod(periodWithPaymentDate, None, None)
        period.isFullTime mustBe false
      }

    }
  }
}
