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

import assets.messages.{BaseMessages, BeenOnStatutoryLeaveMessages}
import forms.HasEmployeeBeenOnStatutoryLeaveFormProvider
import models.requests.DataRequest
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers
import utils.LocalDateHelpers.{apr6th2020, oct30th2020}
import views.ViewUtils.dateToString
import views.behaviours.YesNoViewBehaviours
import views.html.HasEmployeeBeenOnStatutoryLeaveView

class HasEmployeeBeenOnStatutoryLeaveViewSpec extends YesNoViewBehaviours with LocalDateHelpers {

  val boundaryStart                             = dateToString(apr6th2020)
  val boundaryEnd                               = dateToString(oct30th2020)
  val messageKeyPrefix                          = "hasEmployeeBeenOnStatutoryLeave"
  val view: HasEmployeeBeenOnStatutoryLeaveView = injector.instanceOf[HasEmployeeBeenOnStatutoryLeaveView]
  val form: Form[Boolean]                       = new HasEmployeeBeenOnStatutoryLeaveFormProvider()(boundaryStart, boundaryEnd)
  val postAction                                = controllers.routes.HasEmployeeBeenOnStatutoryLeaveController.onSubmit()

  implicit val request: DataRequest[_] = fakeDataRequest()

  def applyView(): Form[Boolean] => HtmlFormat.Appendable =
    (form: Form[_]) => view(form, postAction, boundaryStart, boundaryEnd)(fakeRequest, messages)

  object Selectors extends BaseSelectors

  val expectedContent = Seq(
    Selectors.h1        -> BeenOnStatutoryLeaveMessages.h1(boundaryStart, boundaryEnd),
    Selectors.p(1)      -> BeenOnStatutoryLeaveMessages.p,
    Selectors.bullet(1) -> BeenOnStatutoryLeaveMessages.bullet1,
    Selectors.bullet(2) -> BeenOnStatutoryLeaveMessages.bullet2,
    Selectors.bullet(3) -> BeenOnStatutoryLeaveMessages.bullet3,
    Selectors.bullet(4) -> BeenOnStatutoryLeaveMessages.bullet4,
    Selectors.indent    -> BeenOnStatutoryLeaveMessages.insetText
  )

  implicit val doc = asDocument(applyView()(form))

  behave like pageWithBackLink
  behave like pageWithSubmitButton(BaseMessages.continue)
  behave like normalPage(messageKeyPrefix = messageKeyPrefix, headingArgs = Seq(boundaryStart, boundaryEnd))
  behave like yesNoPage(form,
                        applyView(),
                        messageKeyPrefix,
                        headingArgs = Seq(boundaryStart, boundaryEnd),
                        titleArgs = Seq(boundaryStart, boundaryEnd))
  behave like pageWithExpectedMessages(expectedContent)
}
