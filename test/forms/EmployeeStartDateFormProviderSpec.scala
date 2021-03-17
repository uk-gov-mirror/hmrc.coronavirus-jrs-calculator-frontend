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

import forms.behaviours.DateBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import views.ViewUtils._

import java.time.LocalDate

class EmployeeStartDateFormProviderSpec extends DateBehaviours with GuiceOneAppPerSuite {

  def messagesApi = app.injector.instanceOf[MessagesApi]
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  lazy val formProvider = new EmployeeStartDateFormProvider()

  ".value" when {

    "When the claim is Pre-November" must {

      val furloughStart = LocalDate.of(2020, 3, 10)
      val claimStart    = LocalDate.of(2020, 3, 1)
      lazy val form     = formProvider(furloughStart, claimStart)

      val validData = datesBetween(
        min = formProvider.feb2nd2019,
        max = furloughStart
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(form,
                                   "value",
                                   furloughStart.minusDays(1),
                                   FormError("value", "employeeStartDate.error.max", Seq("19 March 2020")))

      behave like dateFieldWithMin(form,
                                   "value",
                                   formProvider.feb2nd2019,
                                   FormError("value", "employeeStartDate.error.min", Array(dateToString(formProvider.feb2nd2019))))

      behave like mandatoryDateField(form, "value")

    }

    "When the claim is November 2020 to April 2021" must {

      val furloughStart = LocalDate.of(2020, 9, 10)
      val claimStart    = LocalDate.of(2020, 12, 1)
      val form          = formProvider(furloughStart, claimStart)

      val validData = datesBetween(
        min = formProvider.feb2nd2019,
        max = furloughStart
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(form,
                                   "value",
                                   furloughStart.minusDays(1),
                                   FormError("value", "employeeStartDate.error.max", Seq("30 October 2020")))

      behave like dateFieldWithMin(form,
                                   "value",
                                   formProvider.feb2nd2019,
                                   FormError("value", "employeeStartDate.error.min", Array(dateToString(formProvider.feb2nd2019))))

      behave like mandatoryDateField(form, "value")

    }

    "When the claim is May 2021 to September 2021" must {

      val furloughStart = LocalDate.of(2021, 1, 10)
      val claimStart    = LocalDate.of(2021, 5, 1)
      val form          = formProvider(furloughStart, claimStart)

      val validData = datesBetween(
        min = formProvider.feb2nd2019,
        max = furloughStart
      )

      behave like dateField(form, "value", validData)

      behave like dateFieldWithMax(form,
                                   "value",
                                   furloughStart.minusDays(1),
                                   FormError("value", "employeeStartDate.error.max", Seq("2 March 2021")))

      behave like dateFieldWithMin(form,
                                   "value",
                                   formProvider.feb2nd2019,
                                   FormError("value", "employeeStartDate.error.min", Array(dateToString(formProvider.feb2nd2019))))

      behave like mandatoryDateField(form, "value")

    }
  }
}
