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

package views

import assets.messages.BaseMessages
import forms.StatutoryLeavePayFormProvider
import messages.StatutoryLeavePayMessages
import models.Amount
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.AmountViewBehaviours
import views.html.StatutoryLeavePayView

class StatutoryLeavePayViewSpec extends AmountViewBehaviours {

  val messageKeyPrefix            = "statutoryLeavePay"
  val view: StatutoryLeavePayView = injector.instanceOf[StatutoryLeavePayView]
  val form: Form[Amount]          = new StatutoryLeavePayFormProvider()(BigDecimal(200.00))
  val postAction                  = controllers.routes.StatutoryLeavePayController.onSubmit()

  implicit val request: DataRequest[_] = fakeDataRequest()

  def applyView: Form[Amount] => HtmlFormat.Appendable =
    (form: Form[_]) => view(form)(fakeRequest, messages)

  object Selectors extends BaseSelectors

  val expectedContent = Seq(
    Selectors.h1        -> StatutoryLeavePayMessages.h1,
    Selectors.p(1)      -> StatutoryLeavePayMessages.detailsP1,
    Selectors.bullet(1) -> StatutoryLeavePayMessages.detailsL1,
    Selectors.bullet(2) -> StatutoryLeavePayMessages.detailsL2,
    Selectors.bullet(3) -> StatutoryLeavePayMessages.detailsL3,
    Selectors.bullet(4) -> StatutoryLeavePayMessages.detailsL4,
    Selectors.button    -> StatutoryLeavePayMessages.continueButton
  )

  implicit val doc: Document = asDocument(applyView(form))

  behave like normalPage(messageKeyPrefix = messageKeyPrefix)
  behave like pageWithBackLink
  behave like pageWithHeading(heading = StatutoryLeavePayMessages.h1)
  behave like amountPage(form, applyView, messageKeyPrefix)
  behave like pageWithSubmitButton(BaseMessages.continue)
  behave like pageWithExpectedMessages(expectedContent)
}
