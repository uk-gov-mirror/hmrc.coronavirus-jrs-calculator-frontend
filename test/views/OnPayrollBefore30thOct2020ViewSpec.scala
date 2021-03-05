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

import assets.messages._
import forms.OnPayrollBefore30thOct2020FormProvider
import messages.OnPayrollBefore30thOct2020Messages
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.OnPayrollBefore30thOct2020View

class OnPayrollBefore30thOct2020ViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "onPayrollBefore30thOct2020"
  val view: OnPayrollBefore30thOct2020View = injector.instanceOf[OnPayrollBefore30thOct2020View]
  val form: Form[Boolean] = new OnPayrollBefore30thOct2020FormProvider()()

  object Selectors extends BaseSelectors

  "OnPayrollBefore30thOct2020View" when {

    val expectedContent = Seq(
      Selectors.h1   -> OnPayrollBefore30thOct2020Messages.h1,
      Selectors.hint -> OnPayrollBefore30thOct2020Messages.hint
    )

    implicit val request: DataRequest[_] = fakeDataRequest()

    val applyView: Form[Boolean] => HtmlFormat.Appendable = (form: Form[_]) =>
      view(form = form, postAction = controllers.routes.OnPayrollBefore30thOct2020Controller.onSubmit())(fakeRequest, messages)

    implicit val doc: Document = asDocument(applyView(form))

    behave like normalPage(messageKeyPrefix = messageKeyPrefix)
    behave like pageWithBackLink
    behave like pageWithHeading(heading = OnPayrollBefore30thOct2020Messages.h1)
    behave like yesNoPage(form, applyView, messageKeyPrefix)
    behave like pageWithSubmitButton(BaseMessages.continue)
    behave like pageWithExpectedMessages(expectedContent)
  }

}
