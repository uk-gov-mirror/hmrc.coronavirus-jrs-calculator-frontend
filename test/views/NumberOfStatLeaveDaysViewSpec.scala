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
import models._
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers._
import views.ViewUtils.dateToString
import views.behaviours.IntViewBehaviours
import views.html.NumberOfStatLeaveDaysView

class NumberOfStatLeaveDaysViewSpec extends IntViewBehaviours {

  val messageKeyPrefix = "numberOfStatLeaveDays"
  val form: Form[Int] = new NumberOfStatLeaveDaysFormProvider()(apr6th2019, apr5th2020)
  val view: NumberOfStatLeaveDaysView = injector.instanceOf[NumberOfStatLeaveDaysView]
  val postAction: Call = controllers.routes.NumberOfStatLeaveDaysController.onSubmit()

  implicit val request: DataRequest[_] = fakeDataRequest()

  def applyView(boundaryStart: LocalDate, boundaryEnd: LocalDate, employeeType: EmployeeType): Form[Int] => HtmlFormat.Appendable =
    (form: Form[_]) =>
      view(
        form = form,
        postAction = postAction,
        boundaryStart =
          if (employeeType == Type4) {
            messages("hasEmployeeBeenOnStatutoryLeave.dayEmploymentStarted")
          } else {
            dateToString(boundaryStart)
          },
        boundaryEnd = dateToString(boundaryEnd)
      )(fakeRequest, messages)

  object Selectors extends BaseSelectors

  def expectedContent(boundaryStart: LocalDate, boundaryEnd: LocalDate, employeeType: EmployeeType) = Seq(
    Selectors.h1 -> NumberOfStatLeaveDaysMessages.h1(boundaryStart, boundaryEnd, employeeType),
    Selectors.detail -> NumberOfStatLeaveDaysMessages.dropDown,
    Selectors.p(1) -> NumberOfStatLeaveDaysMessages.dropDownParagraph,
    Selectors.bullet(1) -> NumberOfStatLeaveDaysMessages.bullet1,
    Selectors.bullet(2) -> NumberOfStatLeaveDaysMessages.bullet2,
    Selectors.bullet(3) -> NumberOfStatLeaveDaysMessages.bullet3,
    Selectors.bullet(4) -> NumberOfStatLeaveDaysMessages.bullet4
  )

  "NumberOfStatLeaveDaysView" when {

    "employee is type 3" must {
      viewTests(boundaryStart = apr6th2019, boundaryEnd = apr5th2020, employeeType = Type3)
    }

    "employee is type 4" must {
      viewTests(boundaryStart = feb1st2020.plusDays(1), boundaryEnd = apr5th2020, employeeType = Type4)
    }

    "employee is type 5a" must {
      viewTests(boundaryStart = apr6th2020, boundaryEnd = nov8th2020.plusDays(1), employeeType = Type5a)
    }

    "employee is type 5b" must {
      viewTests(boundaryStart = apr6th2020, boundaryEnd = may8th2021.plusDays(1), employeeType = Type5b)
    }
  }

  def viewTests(boundaryStart: LocalDate, boundaryEnd: LocalDate, employeeType: EmployeeType): Unit = {

    implicit val doc: Document = asDocument(applyView(boundaryStart, boundaryEnd, employeeType)(form))

    val headingArgs =
      if (employeeType == Type4) {
        Seq(messages("hasEmployeeBeenOnStatutoryLeave.dayEmploymentStarted"), dateToString(boundaryEnd))
      } else {
        Seq(dateToString(boundaryStart), dateToString(boundaryEnd))
      }

    behave like pageWithBackLink
    behave like normalPage(
      messageKeyPrefix = messageKeyPrefix,
      headingArgs = headingArgs
    )
    behave like intPage(
      form = form,
      createView = applyView(boundaryStart, boundaryEnd, employeeType),
      messageKeyPrefix = messageKeyPrefix,
      headingArgs = headingArgs
    )
    behave like pageWithExpectedMessages(expectedContent(boundaryStart, boundaryEnd, employeeType))
    behave like pageWithSubmitButton(BaseMessages.continue)
  }
}
