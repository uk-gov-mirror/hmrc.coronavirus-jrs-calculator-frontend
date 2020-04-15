/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import forms.PensionAutoEnrolmentFormProvider
import models.PensionStatus.OptedIn
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.PensionAutoEnrolmentPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.PensionAutoEnrolmentView

import scala.concurrent.Future

class PensionAutoEnrolmentControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new PensionAutoEnrolmentFormProvider()
  val form = formProvider()

  lazy val pensionAutoEnrolmentRoute = routes.PensionAutoEnrolmentController.onPageLoad(NormalMode).url

  "PensionAutoEnrolment Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, pensionAutoEnrolmentRoute).withCSRFToken

      val result = route(application, request).value

      val view = application.injector.instanceOf[PensionAutoEnrolmentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(PensionAutoEnrolmentPage, OptedIn).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, pensionAutoEnrolmentRoute).withCSRFToken

      val view = application.injector.instanceOf[PensionAutoEnrolmentView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(OptedIn), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val request =
        FakeRequest(POST, pensionAutoEnrolmentRoute)
          .withFormUrlEncodedBody(("value", "optedIn"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, pensionAutoEnrolmentRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PensionAutoEnrolmentView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, pensionAutoEnrolmentRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, pensionAutoEnrolmentRoute)
          .withFormUrlEncodedBody(("value", "optedIn"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
