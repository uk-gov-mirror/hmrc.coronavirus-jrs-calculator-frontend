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
import assets.PaymentFrequencyMessages

class PaymentFrequencyViewSpec extends ViewBehaviours {

  object Selectors extends BaseSelectors

  val messageKeyPrefix = "august.checkClaimAmounts"
  val view: PaymentFrequencyView = injector.instanceOf[PaymentFrequencyView]
  val form: Form[PaymentFrequency] = new PaymentFrequencyFormProvider()()

  //  def allRadioOptions(checked: Boolean = false) = Seq(
  //    RadioItem(
  //      value = Some(November.toString),
  //      content = Text(messages(s"selectClaimPeriod.${November.toString}")),
  //      hint = Some(hintText(SelectClaimPeriodMessages.novemberHint(frontendAppConfig.novemberClaimCutOff))),
  //      checked = checked,
  //      id = Some(November.toString)
  //    ),
  //    RadioItem(
  //      value = Some(December.toString),
  //      content = Text(messages(s"selectClaimPeriod.${December.toString}")),
  //      hint = Some(hintText(SelectClaimPeriodMessages.decemberHint(frontendAppConfig.decemberClaimCutOff))),
  //      checked = checked,
  //      id = Some(December.toString)
  //    )
  //  )

  val expectedContent = Seq(
    Selectors.h1      -> PaymentFrequencyMessages.heading,
    Selectors.indent  -> PaymentFrequencyMessages.indent,
    Selectors.p(1)    -> PaymentFrequencyMessages.p1,
    Selectors.p(2)    -> s"${PaymentFrequencyMessages.p2} ${PaymentFrequencyMessages.link}",
    Selectors.link(1) -> PaymentFrequencyMessages.link
  ) //update

  "PaymentFrequencyViewSpec" when {

    implicit val request: DataRequest[_] = fakeDataRequest()

    def applyView(): HtmlFormat.Appendable = view(form)

    implicit val doc: Document = asDocument(applyView())

    behave like normalPage(messageKeyPrefix)
    behave like pageWithBackLink
    behave like pageWithHeading("mikey")
    behave like pageWithSubmitButton(BaseMessages.continue)

    //    allRadioOptions().foreach { option =>
    behave like pageWithExpectedMessages(expectedContent)

    //      s"contain radio buttons for the value '${option.value.get}'" in {
    //
    //        val doc = asDocument(applyView(form, allRadioOptions()))
    //        assertContainsRadioButton(doc, option.value.get, "value", option.value.get, false, hint)
    //      }
    //
    //      s"rendered with a value of '${option.value.get}'" must {
    //
    //        s"have the '${option.value.get}' radio button selected" in {
    //
    //          val formWithData = form.bind(Map("value" -> s"${option.value.get}"))
    //          val doc = asDocument(applyView(formWithData, allRadioOptions(true)))
    //
    //          assertContainsRadioButton(doc, option.value.get, "value", option.value.get, true, hint)
    //        }
    //      }
    //    }

  }

}
