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

import assets.constants.PaymentFrequencyConstants.allRadioOptions
import assets.messages.{BaseMessages, PaymentFrequencyMessages}
import forms.PaymentFrequencyFormProvider
import models.PaymentFrequency
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.html.PaymentFrequencyView
import views.includes.behaviours.ViewBehaviours

class PaymentFrequencyViewSpec extends ViewBehaviours {

  object Selectors extends BaseSelectors

  val messageKeyPrefix = "payFrequency"
  val view: PaymentFrequencyView = injector.instanceOf[PaymentFrequencyView]
  val form: Form[PaymentFrequency] = new PaymentFrequencyFormProvider()()

  val expectedContent = Seq(
    Selectors.h1      -> PaymentFrequencyMessages.heading,
    Selectors.indent  -> PaymentFrequencyMessages.indent,
    Selectors.p(1)    -> PaymentFrequencyMessages.p1,
    Selectors.p(2)    -> s"${PaymentFrequencyMessages.p2} ${PaymentFrequencyMessages.link}",
    Selectors.link(1) -> PaymentFrequencyMessages.link
  )

  "PaymentFrequencyViewSpec" when {

    implicit val request: DataRequest[_] = fakeDataRequest()

    def applyView(): HtmlFormat.Appendable =
      view(
        form = form,
        postAction = controllers.routes.PaymentFrequencyController.onSubmit(),
        radioItems = allRadioOptions()
      )

    implicit val doc: Document = asDocument(applyView())

    behave like normalPage(messageKeyPrefix)
    behave like pageWithBackLink
    behave like pageWithHeading(heading = PaymentFrequencyMessages.heading)
    behave like pageWithSubmitButton(BaseMessages.continue)
    behave like pageWithExpectedMessages(expectedContent)

    allRadioOptions().foreach { option =>
      s"contain radio buttons for the value '${option.value.get}'" in {

        val doc = asDocument(applyView())
        assertContainsRadioButton(doc, id = option.value.get, name = "value", value = option.value.get, isChecked = false)
      }

      s"rendered with a value of '${option.value.get}'" must {

        s"have the '${option.value.get}' radio button selected" in {

          def applyView(form: Form[_], radioItems: Seq[RadioItem]): HtmlFormat.Appendable =
            view.apply(
              form = form,
              postAction = controllers.routes.PaymentFrequencyController.onSubmit(),
              radioItems = radioItems
            )

          val formWithData: Form[PaymentFrequency] = form.bind(Map("value" -> s"${option.value.get}"))
          val doc = asDocument(applyView(formWithData, allRadioOptions(checked = true)))

          assertContainsRadioButton(doc, id = option.value.get, name = "value", value = option.value.get, isChecked = true)
        }
      }
    }
  }

}
