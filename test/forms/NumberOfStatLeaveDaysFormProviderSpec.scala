/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time._

import forms.behaviours.IntFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.{Form, FormError}
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.ViewUtils.dateToString

class NumberOfStatLeaveDaysFormProviderSpec extends IntFieldBehaviours with GuiceOneAppPerSuite {

  lazy val injector: Injector                               = app.injector
  implicit lazy val messagesApi: MessagesApi                = injector.instanceOf[MessagesApi]
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
  implicit val messages: Messages                           = messagesApi.preferred(fakeRequest)

  val boundaryStart = LocalDate.of(2019, 4, 6)
  val boundaryEnd   = LocalDate.of(2020, 4, 5)

  val form: Form[Int] = new NumberOfStatLeaveDaysFormProvider()(boundaryStart, boundaryEnd)

  ".value" must {

    val fieldName          = "value"
    val minimum            = 1
    val maximum            = Duration.between(boundaryStart.atStartOfDay(), boundaryEnd.atStartOfDay()).toDays.toInt
    val validDataGenerator = intsInRangeWithCommas(minimum, maximum)

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = validDataGenerator
    )

    behave like intField(
      form = form,
      fieldName = fieldName,
      nonNumericError =
        FormError(fieldName, "numberOfStatLeaveDays.error.nonNumeric", Seq(dateToString(boundaryStart), dateToString(boundaryEnd))),
      wholeNumberError =
        FormError(fieldName, "numberOfStatLeaveDays.error.wholeNumber", Seq(dateToString(boundaryStart), dateToString(boundaryEnd)))
    )

    behave like intFieldWithMinimum(
      form = form,
      fieldName = fieldName,
      minimum = minimum,
      expectedError = FormError(fieldName, "numberOfStatLeaveDays.error.minimum", Seq(minimum))
    )

    behave like intFieldWithMaximum(
      form = form,
      fieldName = fieldName,
      maximum = maximum,
      expectedError =
        FormError(fieldName, "numberOfStatLeaveDays.error.maximum", Seq(maximum, dateToString(boundaryStart), dateToString(boundaryEnd)))
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError =
        FormError(fieldName, "numberOfStatLeaveDays.error.required", Seq(dateToString(boundaryStart), dateToString(boundaryEnd)))
    )
  }

  ".daysBetween()" when {

    "given 2 dates with more than 0 days between them" should {

      "return the number of days between them as 10" in {

        val startDate = LocalDate.of(2020, 1, 10)
        val endDate   = LocalDate.of(2020, 1, 20)

        val actual: Int = new NumberOfStatLeaveDaysFormProvider().daysBetween(startDate, endDate)
        val expected    = 10

        actual shouldEqual expected
      }
    }

    "given 2 dates that are the same" should {

      "return the number of days between them as 1" in {

        val startDate = LocalDate.of(2020, 1, 10)
        val endDate   = LocalDate.of(2020, 1, 10)

        val actual: Int = new NumberOfStatLeaveDaysFormProvider().daysBetween(startDate, endDate)
        val expected    = 1

        actual shouldEqual expected
      }
    }

  }
}
