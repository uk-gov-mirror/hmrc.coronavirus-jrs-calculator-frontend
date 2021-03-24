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
import forms.NumberOfStatLeaveDaysFormProvider
import messages.NumberOfStatLeaveDaysMessages
import models.requests.DataRequest
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers.{apr5th2020, apr6th2019, apr6th2020, feb1st2020, mar19th2020, mar2nd2021, may8th2021, nov8th2020, oct30th2020}
import views.ViewUtils.dateToString
import views.behaviours.IntViewBehaviours
import views.html.NumberOfStatLeaveDaysView

class NumberOfStatLeaveDaysViewSpec extends IntViewBehaviours {

  val messageKeyPrefix = "numberOfStatLeaveDays"

  val form: Form[Int] = new NumberOfStatLeaveDaysFormProvider()(apr6th2019, apr5th2020)

  val view: NumberOfStatLeaveDaysView = injector.instanceOf[NumberOfStatLeaveDaysView]
  val postAction: Call                = controllers.routes.NumberOfStatLeaveDaysController.onSubmit()

  implicit val request: DataRequest[_] = fakeDataRequest()

  def applyView(boundaryStart: LocalDate, boundaryEnd: LocalDate): Form[Int] => HtmlFormat.Appendable =
    (form: Form[_]) =>
      view(
        form = form,
        postAction = postAction,
        boundaryStart = dateToString(boundaryStart),
        boundaryEnd = dateToString(boundaryEnd)
      )(fakeRequest, messages)

  object Selectors extends BaseSelectors

  def expectedContent(boundaryStart: LocalDate, boundaryEnd: LocalDate) = Seq(
    Selectors.h1        -> NumberOfStatLeaveDaysMessages.h1(boundaryStart, boundaryEnd),
    Selectors.detail    -> NumberOfStatLeaveDaysMessages.dropDown,
    Selectors.p(1)      -> NumberOfStatLeaveDaysMessages.dropDownParagraph,
    Selectors.bullet(1) -> NumberOfStatLeaveDaysMessages.bullet1,
    Selectors.bullet(2) -> NumberOfStatLeaveDaysMessages.bullet2,
    Selectors.bullet(3) -> NumberOfStatLeaveDaysMessages.bullet3,
    Selectors.bullet(4) -> NumberOfStatLeaveDaysMessages.bullet4
  )

  "NumberOfStatLeaveDaysView" when {

    "employee is type 3" must {
      viewTests(apr6th2019, apr5th2020)
    }

    "employee is type 4" must {
      viewTests(feb1st2020.plusDays(1), apr5th2020)
    }

    "employee is type 5a" must {
      viewTests(apr6th2020, nov8th2020.plusDays(1))
    }

    "employee is type 5b" must {
      viewTests(apr6th2020, may8th2021.plusDays(1))
    }
  }

  def viewTests(boundaryStart: LocalDate, boundaryEnd: LocalDate): Unit = {

    implicit val doc = asDocument(applyView(boundaryStart, boundaryEnd)(form))

    behave like pageWithBackLink
    behave like normalPage(messageKeyPrefix = messageKeyPrefix, Seq(dateToString(boundaryStart), dateToString(boundaryEnd)))
    behave like intPage(
      form = form,
      createView = applyView(boundaryStart, boundaryEnd),
      messageKeyPrefix = messageKeyPrefix,
      headingArgs = Seq(dateToString(boundaryStart), dateToString(boundaryEnd))
    )
    behave like pageWithExpectedMessages(expectedContent(boundaryStart, boundaryEnd))
    behave like pageWithSubmitButton(BaseMessages.continue)
  }
}
