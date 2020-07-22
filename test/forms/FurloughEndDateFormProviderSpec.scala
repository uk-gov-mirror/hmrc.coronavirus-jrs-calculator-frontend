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

package forms

import java.time.LocalDate

import base.SpecBaseControllerSpecs
import forms.behaviours.DateBehaviours
import models.Period
import play.api.data.FormError

class FurloughEndDateFormProviderSpec extends SpecBaseControllerSpecs {

  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 5, 1)
  private val furloughStart = startDate

  ".value" should {

    val form = new FurloughEndDateFormProvider()(Period(startDate, endDate), furloughStart)

    val validData = datesBetween(
      min = startDate.plusDays(20),
      max = startDate.plusDays(31)
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughEndDate.error.min.max"))

    behave like mandatoryDateField(form, "value")
  }

  ".endDate" should {
    val claimStart = LocalDate.of(2020, 7, 1)
    val claimEnd = LocalDate.of(2020, 7, 31)
    val furloughStart = claimStart
    val furloughEnd = furloughStart.plusDays(6)

    "not enforce 21 day minimum if claim start is on or after 1 July 2020" in {
      val form = new FurloughEndDateFormProvider()(Period(claimStart, claimEnd), furloughStart)

      val data = Map(
        "value.day"   -> furloughEnd.getDayOfMonth.toString,
        "value.month" -> furloughEnd.getMonthValue.toString,
        "value.year"  -> furloughEnd.getYear.toString
      )

      val result = form.bind(data)

      result.errors shouldBe List()
    }

    "be before or same as end of the claim for phase two and do not show furloughEndDate.error.min.max" in {
      val claimEnd = LocalDate.of(2020, 7, 15)
      val form = new FurloughEndDateFormProvider()(Period(claimStart, claimEnd), furloughStart)

      val data: Int => Map[String, String] = days =>
        Map(
          "value.day"   -> claimEnd.plusDays(days).getDayOfMonth.toString,
          "value.month" -> claimEnd.getMonthValue.toString,
          "value.year"  -> claimEnd.getYear.toString
      )

      form.bind(data(1)).errors.size shouldBe 1
      form.bind(data(1)).errors.head.message shouldBe "furloughEndDate.error.claimPeriod"
      form.bind(data(0)).errors shouldBe List()
    }
  }
}
