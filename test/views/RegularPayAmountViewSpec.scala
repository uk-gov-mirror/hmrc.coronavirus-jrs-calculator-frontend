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

import assets.messages.{BaseMessages, RegularPayAmountMessages}
import forms.RegularPayAmountFormProvider
import models.Salary
import models.requests.DataRequest
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers.{mar19th2020, mar2nd2021, oct30th2020}
import views.ViewUtils.dateToString
import views.behaviours.SalaryViewBehaviours
import views.html.RegularPayAmountView

import java.time.LocalDate

class RegularPayAmountViewSpec extends SalaryViewBehaviours {

  val messageKeyPrefix           = "regularPayAmount"
  val view: RegularPayAmountView = injector.instanceOf[RegularPayAmountView]
  val form: Form[Salary]         = new RegularPayAmountFormProvider()()
  val postAction                 = controllers.routes.RegularPayAmountController.onSubmit()

  implicit val request: DataRequest[_] = fakeDataRequest()

  def applyView(cutOffDate: LocalDate): Form[Salary] => HtmlFormat.Appendable =
    (form: Form[_]) => view(form, postAction, dateToString(cutOffDate))(fakeRequest, messages)

  object Selectors extends BaseSelectors

  def expectedContent(cutoffDate: LocalDate) = Seq(
    Selectors.h1     -> RegularPayAmountMessages.h1(cutoffDate),
    Selectors.p(1)   -> RegularPayAmountMessages.p1(cutoffDate),
    Selectors.p(2)   -> RegularPayAmountMessages.p2,
    Selectors.indent -> RegularPayAmountMessages.indent
  )

  "RegularPayAmountView" when {

    "employee is type 1" must {
      viewTests(mar19th2020)
    }

    "employee is type 2a" must {
      viewTests(oct30th2020)
    }

    "employee is type 2b" must {
      viewTests(mar2nd2021)
    }
  }

  def viewTests(cutoffDate: LocalDate) = {

    implicit val doc = asDocument(applyView(cutoffDate)(form))

    behave like pageWithBackLink
    behave like pageWithSubmitButton(BaseMessages.continue)
    behave like normalPage(messageKeyPrefix = messageKeyPrefix, Seq(dateToString(cutoffDate)))
    behave like salaryPage(form, applyView(cutoffDate), messageKeyPrefix, Seq(dateToString(cutoffDate)))
    behave like pageWithExpectedMessages(expectedContent(cutoffDate))
    behave like pageWithWhatToInclude()
  }
}
