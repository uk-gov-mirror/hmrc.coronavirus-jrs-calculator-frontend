/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import base.SpecBaseWithApplication
import forms.PensionContributionFormProvider
import models.PensionStatus.DoesContribute
import models.UserAnswers
import models.requests.DataRequest
import navigation.{FakeNavigator, Navigator}
import org.scalatestplus.mockito.MockitoSugar
import pages.PensionStatusPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.PensionContributionView

class PensionContributionControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PensionContributionFormProvider()
  val form = formProvider()

  lazy val pensionContributionRoute = routes.PensionContributionController.onPageLoad().url

  "PensionContributionController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, pensionContributionRoute).withCSRFToken

      val result = route(application, request).value

      val view = application.injector.instanceOf[PensionContributionView]

      status(result) mustEqual OK

      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      contentAsString(result) mustEqual
        view(form)(dataRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(PensionStatusPage, DoesContribute).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, pensionContributionRoute).withCSRFToken

      val view = application.injector.instanceOf[PensionContributionView]

      val result = route(application, request).value

      status(result) mustEqual OK

      val dataRequest = DataRequest(request, userAnswers.id, userAnswers)

      contentAsString(result) mustEqual
        view(form.fill(DoesContribute))(dataRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute))
          )
          .build()

      val request =
        FakeRequest(POST, pensionContributionRoute)
          .withFormUrlEncodedBody(("value", "doesContribute"))
          .withCSRFToken

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, pensionContributionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PensionContributionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      val dataRequest = DataRequest(request, emptyUserAnswers.id, emptyUserAnswers)

      contentAsString(result) mustEqual
        view(boundForm)(dataRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, pensionContributionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, pensionContributionRoute)
          .withFormUrlEncodedBody(("value", "optedIn"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
