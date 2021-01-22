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

package views.includes

import assets.BaseMessages
import forms.PaymentFrequencyFormProvider
import models.PaymentFrequency
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.PaymentFrequencyView
import views.includes.behaviours.ViewBehaviours

class PaymentFrequencyViewSpec extends ViewBehaviours {

  object Selectors extends BaseSelectors

  val messageKeyPrefix = "august.checkClaimAmounts"

  val view: PaymentFrequencyView = injector.instanceOf[PaymentFrequencyView]
  val form: Form[PaymentFrequency] = new PaymentFrequencyFormProvider()()

  implicit val request: DataRequest[_] = fakeDataRequest()

  "PaymentFrequencyViewSpec" when {

    def applyView(): HtmlFormat.Appendable = view(form)

    implicit val doc: Document = asDocument(applyView())

    behave like normalPage(messageKeyPrefix)
    behave like pageWithBackLink
    behave like pageWithHeading("mikey")
    behave like pageWithSubmitButton(BaseMessages.continue)
  }

}
