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

import assets.messages.{BaseMessages, PartTimeNormalHoursMessages}
import forms.PartTimeNormalHoursFormProvider
import models.requests.DataRequest
import models.{FullPeriod, Hours, PartialPeriod, Period}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.BaseSelectors
import views.html.PartTimeNormalHoursView
import views.includes.behaviours.HoursViewBehaviours

class PartTimeNormalHoursViewSpec extends HoursViewBehaviours {

  object Selectors extends BaseSelectors

  val messageKeyPrefix = "partTimeNormalHours"
  val view: PartTimeNormalHoursView = injector.instanceOf[PartTimeNormalHoursView]

  val period = Period(
    start = LocalDate.parse("2021-02-01"),
    end = LocalDate.parse("2021-02-10")
  )

  val periods = FullPeriod(period)

  val form: Form[Hours] = new PartTimeNormalHoursFormProvider()(periods)

  "PartTimeNormalHoursView" when {

    "the hours cover multiple days" when {

      val expectedContent = Seq(
        Selectors.h1      -> PartTimeNormalHoursMessages.h1(periods.period.start, periods.period.end),
        Selectors.p(1)    -> PartTimeNormalHoursMessages.p1,
        Selectors.p(2)    -> s"${PartTimeNormalHoursMessages.p2}",
        Selectors.link(1) -> PartTimeNormalHoursMessages.link
      )

      implicit val request: DataRequest[_] = fakeDataRequest()

      def applyView(): HtmlFormat.Appendable =
        view(
          form = form,
          period = periods,
          idx = 1
        )

      implicit val doc: Document = asDocument(applyView())

      behave like normalPage(
        messageKeyPrefix,
        Seq(
          PartTimeNormalHoursMessages.dateToStringWithoutYear(periods.period.start),
          PartTimeNormalHoursMessages.dateToString(periods.period.end)
        )
      )
      behave like pageWithBackLink
      behave like pageWithHeading(heading = PartTimeNormalHoursMessages.h1(periods.period.start, periods.period.end))
      behave like pageWithSubmitButton(BaseMessages.continue)
      behave like pageWithExpectedMessages(expectedContent)

      "has the correct link destination" in {
        doc.select(Selectors.link(1)).attr("href") mustBe appConf.usualHours
      }
    }

    "the hours cover a single day" when {

      val periods = PartialPeriod(
        original = period,
        partial = period.copy(
          start = LocalDate.parse("2021-02-10")
        )
      )

      val form: Form[Hours] = new PartTimeNormalHoursFormProvider()(periods)

      val expectedContent = Seq(
        Selectors.h1      -> PartTimeNormalHoursMessages.h1Single(periods.period.end),
        Selectors.p(1)    -> PartTimeNormalHoursMessages.p1,
        Selectors.p(2)    -> s"${PartTimeNormalHoursMessages.p2}",
        Selectors.link(1) -> PartTimeNormalHoursMessages.link
      )

      implicit val request: DataRequest[_] = fakeDataRequest()

      def applyView(): HtmlFormat.Appendable =
        view(
          form = form,
          period = periods,
          idx = 1
        )

      implicit val doc: Document = asDocument(applyView())

      "display welsh language toggles" in {
        assertRenderedById(doc, "cymraeg-switch")
      }

      behave like pageWithBackLink
      behave like pageWithHeading(heading = PartTimeNormalHoursMessages.h1Single(periods.period.end))
      behave like pageWithSubmitButton(BaseMessages.continue)
      behave like pageWithExpectedMessages(expectedContent)

      "has the correct link destination" in {
        doc.select(Selectors.link(1)).attr("href") mustBe appConf.usualHours
      }
    }
  }
}
