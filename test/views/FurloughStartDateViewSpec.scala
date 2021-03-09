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

import assets.messages.{BaseMessages, FurloughStartDateMessages}
import forms.FurloughStartDateFormProvider
import models.Period
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.FurloughStartDateView

class FurloughStartDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  object Selectors extends BaseSelectors

  val messageKeyPrefix            = "furloughStartDate"
  val view: FurloughStartDateView = injector.instanceOf[FurloughStartDateView]

  val period = Period(
    start = LocalDate.parse("2020-10-01"),
    end = LocalDate.parse("2020-10-10")
  )

  val form: Form[LocalDate] = new FurloughStartDateFormProvider()(period.end)

  "FurloughStartDateView" when {

    "before 2020-11-01" when {

      val expectedContent = Seq(
        Selectors.h1   -> FurloughStartDateMessages.heading,
        Selectors.p(1) -> FurloughStartDateMessages.p1
      )

      implicit val request: DataRequest[_] = fakeDataRequest()

      def applyView(): HtmlFormat.Appendable = view(
        form = form,
        claimStartDate = period.start
      )

      implicit val doc: Document = asDocument(applyView())

      behave like normalPage(messageKeyPrefix)
      behave like pageWithBackLink
      behave like pageWithHeading(heading = FurloughStartDateMessages.heading)
      behave like pageWithSubmitButton(BaseMessages.continue)
      behave like pageWithExpectedMessages(expectedContent)
    }

    "after 2020-11-01 and before 2021-05-01" when {

      val period = Period(
        start = LocalDate.parse("2020-11-01"),
        end = LocalDate.parse("2020-11-10")
      )

      val form: Form[LocalDate] = new FurloughStartDateFormProvider()(period.end)

      val expectedContent = Seq(
        Selectors.h1   -> FurloughStartDateMessages.heading,
        Selectors.p(1) -> FurloughStartDateMessages.p1,
        Selectors.p(2) -> FurloughStartDateMessages.p2
      )

      implicit val request: DataRequest[_] = fakeDataRequest()

      def applyView(): HtmlFormat.Appendable = view(
        form = form,
        claimStartDate = period.start
      )

      implicit val doc: Document = asDocument(applyView())

      behave like normalPage(messageKeyPrefix)
      behave like pageWithBackLink
      behave like pageWithHeading(heading = FurloughStartDateMessages.heading)
      behave like pageWithSubmitButton(BaseMessages.continue)
      behave like pageWithExpectedMessages(expectedContent)
    }

    "after 2021-05-01" when {

      val period = Period(
        start = LocalDate.parse("2021-05-01"),
        end = LocalDate.parse("2021-05-10")
      )

      val form: Form[LocalDate] = new FurloughStartDateFormProvider()(period.end)

      val expectedContent = Seq(
        Selectors.h1   -> FurloughStartDateMessages.heading,
        Selectors.p(1) -> FurloughStartDateMessages.p1,
        Selectors.p(2) -> FurloughStartDateMessages.extensionP2,
        Selectors.p(3) -> FurloughStartDateMessages.extensionP3
      )

      implicit val request: DataRequest[_] = fakeDataRequest()

      def applyView(): HtmlFormat.Appendable = view(
        form = form,
        claimStartDate = period.start
      )

      implicit val doc: Document = asDocument(applyView())

      behave like normalPage(messageKeyPrefix)
      behave like pageWithBackLink
      behave like pageWithHeading(heading = FurloughStartDateMessages.heading)
      behave like pageWithSubmitButton(BaseMessages.continue)
      behave like pageWithExpectedMessages(expectedContent)
    }
  }
}
