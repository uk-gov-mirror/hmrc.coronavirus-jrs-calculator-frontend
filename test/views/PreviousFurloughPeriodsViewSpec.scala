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

import assets.constants.PaymentFrequencyConstants.allRadioOptions
import assets.messages.{BaseMessages, PaymentFrequencyMessages}
import forms.behaviours.BooleanFieldBehaviours
import forms.{PaymentFrequencyFormProvider, PreviousFurloughPeriodsFormProvider}
import messages.PreviousFurloughPeriodsMessages
import models.PaymentFrequency
import models.requests.DataRequest
import org.jsoup.nodes.Document
import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import utils.LocalDateHelpers._
import views.ViewUtils.dateToString
import views.behaviours.{ViewBehaviours, YesNoViewBehaviours}
import views.html.{PaymentFrequencyView, PreviousFurloughPeriodsView}

import java.time.LocalDate

class PreviousFurloughPeriodsViewSpec extends ViewBehaviours with YesNoViewBehaviours {

  object Selectors extends BaseSelectors

  val messageKeyPrefix                  = "previousFurloughPeriods"
  val view: PreviousFurloughPeriodsView = injector.instanceOf[PreviousFurloughPeriodsView]
  val form: Form[Boolean]               = new PreviousFurloughPeriodsFormProvider()(nov1st2020)

  val expectedContent = Seq(
    Selectors.h1 -> PreviousFurloughPeriodsMessages.heading(dateToString(nov1st2020))
  )

  "PreviousFurloughPeriodsViewSpec" when {

    implicit val request: DataRequest[_] = fakeDataRequest()

    def applyView(date: LocalDate): Form[Boolean] => HtmlFormat.Appendable =
      (form: Form[_]) => view(form, date)(fakeRequest, messages)

    implicit val doc: Document = asDocument(applyView(nov1st2020)(form))

    behave like normalPage(messageKeyPrefix, Seq(dateToString(nov1st2020)))
    behave like pageWithBackLink
    behave like pageWithHeading(heading = PreviousFurloughPeriodsMessages.heading(dateToString(nov1st2020)))
    behave like pageWithSubmitButton(BaseMessages.continue)
    behave like pageWithExpectedMessages(expectedContent)
    behave like yesNoPage(
      form,
      applyView(nov1st2020),
      messageKeyPrefix,
      Seq(dateToString(nov1st2020)),
      Seq(dateToString(nov1st2020))
    )

    "display the correct error message when a user doesn't select an answer for 1st November 2020" in {
      val requiredErrorMessage = "previousFurloughPeriods.error.required"
      val error                = FormError(errorKey, requiredErrorMessage, Seq(dateToString(nov1st2020)))
      val doc                  = asDocument(applyView(nov1st2020)(form.withError(error)))
      val errorSpan            = doc.getElementsByClass("govuk-error-summary__list").first
      errorSpan.text() mustBe s"Select yes if this employee has been furloughed more than once since ${dateToString(nov1st2020)}"
    }

    "display the correct error message when a user doesn't select an answer for 1st May 2021" in {
      val requiredErrorMessage = "previousFurloughPeriods.error.required"
      val error                = FormError(errorKey, requiredErrorMessage, Seq(dateToString(may1st2021)))
      val doc                  = asDocument(applyView(may1st2021)(form.withError(error)))
      val errorSpan            = doc.getElementsByClass("govuk-error-summary__list").first
      errorSpan.text() mustBe s"Select yes if this employee has been furloughed more than once since ${dateToString(may1st2021)}"
    }
  }
}
