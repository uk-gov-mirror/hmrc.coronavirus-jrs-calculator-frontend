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

import forms.behaviours.BooleanFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.LocalDateHelpers.nov1st2020
import views.ViewUtils.dateToString

class PreviousFurloughPeriodsFormProviderSpec extends BooleanFieldBehaviours with GuiceOneAppPerSuite {

  val requiredKey = "previousFurloughPeriods.error.required"
  val invalidKey  = "error.boolean"

  lazy val injector: Injector                               = app.injector
  implicit lazy val messagesApi: MessagesApi                = injector.instanceOf[MessagesApi]
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
  implicit val messages: Messages                           = messagesApi.preferred(fakeRequest)

  val form = new PreviousFurloughPeriodsFormProvider()(nov1st2020)

  ".value" must {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey, Seq(dateToString(nov1st2020)))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(dateToString(nov1st2020)))
    )
  }
}
