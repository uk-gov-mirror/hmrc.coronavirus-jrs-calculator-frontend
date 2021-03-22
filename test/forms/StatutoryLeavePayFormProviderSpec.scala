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

import base.SpecBase
import forms.behaviours.BigDecimalFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class StatutoryLeavePayFormProviderSpec extends BigDecimalFieldBehaviours with GuiceOneAppPerSuite {

  lazy val injector: Injector                               = app.injector
  implicit lazy val messagesApi: MessagesApi                = injector.instanceOf[MessagesApi]
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")
  implicit val messages: Messages                           = messagesApi.preferred(fakeRequest)

  def form(referencePay: BigDecimal) = new StatutoryLeavePayFormProvider()(referencePay)
  lazy val formForBehaviourTests     = new StatutoryLeavePayFormProvider().apply(BigDecimal(400.00))

  ".value" must {

    val fieldName    = "value"
    val requiredKey  = "statutoryLeavePay.error.required"
    val invalidKey   = "statutoryLeavePay.error.invalid"
    val moreThan0Key = "statutoryLeavePay.error.moreThan0"

    val referencePay = BigDecimal(999999999.99)

    "reject 0" in {
      val value = "0.0"
      val data  = Map("value" -> value)

      val result = form(referencePay).bind(data)

      result.errors shouldBe Seq(FormError(fieldName, moreThan0Key))
    }

    "reject negative numbers" in {
      val value = "-1.0"
      val data  = Map("value" -> value)

      val result = form(referencePay).bind(data)

      result.errors shouldBe Seq(FormError(fieldName, moreThan0Key))
    }

    behave like bigDecimalFieldWithMax(
      formForBehaviourTests,
      fieldName,
      error = FormError(fieldName, invalidKey),
      max = referencePay
    )

    behave like mandatoryField(
      formForBehaviourTests,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

}
