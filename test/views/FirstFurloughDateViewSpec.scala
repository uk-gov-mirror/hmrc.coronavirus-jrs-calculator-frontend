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

import java.time.LocalDate

import assets.messages.BaseMessages
import forms.FirstFurloughDateFormProvider
import messages.FirstFurloughDateMessages
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.FirstFurloughDateView

class FirstFurloughDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  object Selectors extends BaseSelectors

  val messageKeyPrefix            = "firstFurloughStartDate"
  val view: FirstFurloughDateView = injector.instanceOf[FirstFurloughDateView]
  val form                        = new FirstFurloughDateFormProvider()(LocalDate.now())

  val expectedContent = (date: LocalDate) =>
    Seq(
      Selectors.h1   -> FirstFurloughDateMessages.heading,
      Selectors.p(1) -> FirstFurloughDateMessages.p1(date),
  )

  val expectedContentWithSecondP = (date: LocalDate) =>
    Seq(
      Selectors.h1   -> FirstFurloughDateMessages.heading,
      Selectors.p(1) -> FirstFurloughDateMessages.p1(date),
      Selectors.p(2) -> FirstFurloughDateMessages.p2
  )

  val nov1st2020            = LocalDate.of(2020, 11, 1)
  val mar1st2020: LocalDate = LocalDate.of(2020, 3, 1)
  val may1st2021: LocalDate = LocalDate.of(2021, 5, 1)

  "FirstFurloughDateViewSpec" when {

    "making a calculation for a variable pay (3) new starter " when {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[FirstFurloughDateView](Some(emptyUserAnswers))
        view.apply(form, mar1st2020)(fakeRequest, messages)
      }

      implicit val doc: Document = asDocument(applyView(form))

      behave like normalPage(messageKeyPrefix)

      behave like pageWithSubmitButton(BaseMessages.continue)

      behave like pageWithHeading(heading = FirstFurloughDateMessages.heading)

      behave like pageWithExpectedMessages(expectedContent(mar1st2020))
    }

    "making a calculation for a variable pay (5A) new starter " when {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[FirstFurloughDateView](Some(emptyUserAnswers))
        view.apply(form, nov1st2020)(fakeRequest, messages)
      }

      implicit val doc: Document = asDocument(applyView(form))

      behave like normalPage(messageKeyPrefix)

      behave like pageWithSubmitButton(BaseMessages.continue)

      behave like pageWithHeading(heading = FirstFurloughDateMessages.heading)

      behave like pageWithExpectedMessages(expectedContentWithSecondP(nov1st2020))
    }

    "making a calculation for a variable pay (5B) new starter " when {
      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[FirstFurloughDateView](Some(emptyUserAnswers))
        view.apply(form, may1st2021)(fakeRequest, messages)
      }

      implicit val doc: Document = asDocument(applyView(form))

      behave like normalPage(messageKeyPrefix)

      behave like pageWithSubmitButton(BaseMessages.continue)

      behave like pageWithHeading(heading = FirstFurloughDateMessages.heading)

      behave like pageWithExpectedMessages(expectedContent(may1st2021))
    }

  }

}
