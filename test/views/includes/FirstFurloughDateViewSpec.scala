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

import assets.messages.BaseMessages
import forms.FirstFurloughDateFormProvider
import messages.FirstFurloughDateMessages
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.BaseSelectors
import views.behaviours.QuestionViewBehaviours
import views.html.FirstFurloughDateView

import java.time.LocalDate

class FirstFurloughDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  object Selectors extends BaseSelectors

  val messageKeyPrefix = "firstFurloughStartDate"
  val view: FirstFurloughDateView = injector.instanceOf[FirstFurloughDateView]
  val form = new FirstFurloughDateFormProvider()(LocalDate.now())

  val expectedContent = Seq(
    Selectors.h1   -> FirstFurloughDateMessages.heading,
    Selectors.p(1) -> FirstFurloughDateMessages.p1,
    Selectors.p(2) -> FirstFurloughDateMessages.p2
  )

  "FirstFurloughDateViewSpec" when {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[FirstFurloughDateView](Some(emptyUserAnswers))
      view.apply(form)(fakeRequest, messages)
    }

    implicit val doc: Document = asDocument(applyView(form))

    behave like normalPage(messageKeyPrefix)

    behave like pageWithSubmitButton(BaseMessages.continue)

    behave like pageWithHeading(heading = FirstFurloughDateMessages.heading)

    behave like pageWithExpectedMessages(expectedContent)
  }

}
