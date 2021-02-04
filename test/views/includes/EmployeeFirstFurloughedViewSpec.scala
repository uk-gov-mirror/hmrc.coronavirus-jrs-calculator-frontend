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

import java.time.LocalDate

import assets.messages.BaseMessages
import forms.{EmployeeFirstFurloughedFormProvider, EmployeeFirstFurloughedFormProviderSpec}
import messages.EmployeeFirstFurloughMessages
import models.requests.DataRequest
import views.includes.behaviours.{QuestionViewBehaviours, ViewBehaviours}
import views.html.EmployeeFirstFurloughedView
import play.api.data.Form
import play.twirl.api.HtmlFormat
import org.jsoup.nodes.Document

class EmployeeFirstFurloughedViewSpec extends QuestionViewBehaviours[LocalDate] {

  object Selectors extends BaseSelectors

  val messageKeyPrefix = "employeeFirstFurloughed"
  val view: EmployeeFirstFurloughedView = injector.instanceOf[EmployeeFirstFurloughedView]
  val form = new EmployeeFirstFurloughedFormProvider()()
  val section = Some(messages("section.main"))

  val expectedContent = Seq(
    Selectors.h1   -> EmployeeFirstFurloughMessages.heading,
    Selectors.p(1) -> EmployeeFirstFurloughMessages.p1,
    Selectors.p(2) -> EmployeeFirstFurloughMessages.p2
  )

  "EmployeeFirstFurloughViewSpec" when {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[EmployeeFirstFurloughedView](Some(emptyUserAnswers))
      view.apply(form)(fakeRequest, messages)
    }

    implicit val doc: Document = asDocument(applyView(form))

    behave like normalPage(messageKeyPrefix)

    behave like pageWithSubmitButton(BaseMessages.continue)

  }

}
