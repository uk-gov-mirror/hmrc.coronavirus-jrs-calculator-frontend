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

import java.time.{LocalDate, ZoneOffset}

import forms.behaviours.DateBehaviours
import models.PaymentFrequency
import org.scalacheck.Gen
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import views.ViewUtils.dateToString

class PayDateFormProviderSpec extends DateBehaviours with GuiceOneAppPerSuite {

  def messagesApi = app.injector.instanceOf[MessagesApi]
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  val form = new PayDateFormProvider()()

  "PayDateFormProvider" should {

    ".value" should {

      val validData = datesBetween(
        min = LocalDate.of(2000, 1, 1),
        max = LocalDate.now(ZoneOffset.UTC)
      )

      behave like dateField(form, "value", validData)

      behave like mandatoryDateField(form, "value")
    }

    "before constraint" must {

      "accept valid data" in {
        val gen = for {
          daysMinus <- Gen.choose(1, 100)
          validDates <- datesBetween(
                         min = LocalDate.of(2020, 3, 1),
                         max = LocalDate.of(2020, 5, 1)
                       )
        } yield (validDates, daysMinus)

        forAll(gen -> "valid dates") {
          case (date: LocalDate, minusDays: Int) =>
            val dateBefore = date.minusDays(minusDays)

            val data = Map(
              "value.day"   -> dateBefore.getDayOfMonth.toString,
              "value.month" -> dateBefore.getMonthValue.toString,
              "value.year"  -> dateBefore.getYear.toString
            )

            val result = new PayDateFormProvider().apply(beforeDate = Some(date)).bind(data)

            result.errors shouldBe empty
            result.value.value shouldEqual dateBefore
        }
      }

      "reject invalid data" in {
        val gen = for {
          daysPlus <- Gen.choose(1, 100)
          validDates <- datesBetween(
                         min = LocalDate.of(2020, 3, 1),
                         max = LocalDate.of(2020, 5, 1)
                       )
        } yield (validDates, daysPlus)

        forAll(gen -> "valid dates") {
          case (date: LocalDate, plusDays: Int) =>
            val dateAfter = date.plusDays(plusDays)

            val data = Map(
              "value.day"   -> dateAfter.getDayOfMonth.toString,
              "value.month" -> dateAfter.getMonthValue.toString,
              "value.year"  -> dateAfter.getYear.toString
            )

            val result = new PayDateFormProvider().apply(beforeDate = Some(date)).bind(data)

            result.errors.head.key shouldBe "value"
            result.errors.head.message shouldBe "payDate.error.mustBeBefore"
            result.errors.head.args should contain only dateToString(date)
        }
      }
    }

    "after constraint" must {

      "accept valid data" in {
        val gen = for {
          daysPlus <- Gen.choose(1, 100)
          validDates <- datesBetween(
                         min = LocalDate.of(2020, 3, 1),
                         max = LocalDate.of(2020, 5, 1)
                       )
        } yield (validDates, daysPlus)

        forAll(gen -> "valid dates") {
          case (date: LocalDate, plusDays: Int) =>
            val dateAfter = date.plusDays(plusDays)

            val data = Map(
              "value.day"   -> dateAfter.getDayOfMonth.toString,
              "value.month" -> dateAfter.getMonthValue.toString,
              "value.year"  -> dateAfter.getYear.toString
            )

            val result = new PayDateFormProvider().apply(afterDate = Some(date)).bind(data)

            result.errors shouldBe empty
            result.value.value shouldEqual dateAfter
        }
      }

      "reject invalid data" in {
        val gen = for {
          daysPlus <- Gen.choose(1, 100)
          validDates <- datesBetween(
                         min = LocalDate.of(2020, 3, 1),
                         max = LocalDate.of(2020, 5, 1)
                       )
        } yield (validDates, daysPlus)

        forAll(gen -> "valid dates") {
          case (date: LocalDate, plusDays: Int) =>
            val dateBefore = date.minusDays(plusDays)

            val data = Map(
              "value.day"   -> dateBefore.getDayOfMonth.toString,
              "value.month" -> dateBefore.getMonthValue.toString,
              "value.year"  -> dateBefore.getYear.toString
            )

            val result = new PayDateFormProvider().apply(afterDate = Some(date)).bind(data)

            result.errors.head.key shouldBe "value"
            result.errors.head.message shouldBe "payDate.error.mustBeAfter"
            result.errors.head.args should contain only dateToString(date)
        }
      }
    }

    "validate /pay-date/1 date as per paymentFrequency" must {

      "return Valid if date is on or after lookback date as per paymentFrequency" in {
        val dateBefore = LocalDate.of(2020, 11, 1)
        val pf = PaymentFrequency.Weekly

        val formData = dateBefore.minusDays(7)

        val data = Map(
          "value.day"   -> formData.getDayOfMonth.toString,
          "value.month" -> formData.getMonthValue.toString,
          "value.year"  -> formData.getYear.toString
        )

        val result = new PayDateFormProvider().apply(beforeDate = Some(dateBefore), paymentFrequency = Some(pf)).bind(data)

        result.errors shouldBe empty
        result.value.value shouldEqual formData
      }

      "return InValid if date is before expected lookback date as per paymentFrequency" in {
        val dateBefore = LocalDate.of(2020, 11, 1)
        val pf = PaymentFrequency.Weekly

        val formData = dateBefore.minusDays(8)
        val lookBackDateAsPerPF = dateBefore.minusDays(7)

        val data = Map(
          "value.day"   -> formData.getDayOfMonth.toString,
          "value.month" -> formData.getMonthValue.toString,
          "value.year"  -> formData.getYear.toString
        )

        val result = new PayDateFormProvider().apply(beforeDate = Some(dateBefore), paymentFrequency = Some(pf)).bind(data)

        result.errors.head.key shouldBe "value"
        result.errors.head.message shouldBe "payDate.error.must.be.as.per.paymentFrequency"
        result.errors.head.args shouldBe Seq(dateToString(dateBefore), dateToString(lookBackDateAsPerPF))
      }
    }
  }

}
