/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import forms.ReviewPayDatesFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.PayDatePage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call, Request}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ReviewPayDatesView

import scala.concurrent.Future

class ReviewPayDatesControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")
  val addMoreRoute = Call("GET", "/coronavirus-job-retention-scheme/calculator/payDate/1")

  val formProvider = new ReviewPayDatesFormProvider()
  val form = formProvider()

  lazy val reviewPayDatesRoute = routes.ReviewPayDatesController.onPageLoad(NormalMode).url

  "ReviewPayDates Controller" must {

    "redirect to pay date page when date list is empty" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request: Request[AnyContentAsEmpty.type] = FakeRequest(GET, reviewPayDatesRoute).withCSRFToken

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual addMoreRoute.url

      application.stop()
    }

    "return OK and the correct view for a GET when date list is non-empty" in {
      val date = LocalDate.of(2020, 3, 1)

      val userAnswers = UserAnswers("id").set(PayDatePage, date, Some(1))
        .getOrElse(fail("Could not initialise user answers with PayDate data"))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request: Request[AnyContentAsEmpty.type] = FakeRequest(GET, reviewPayDatesRoute).withCSRFToken

      val result = route(application, request).value

      val view = application.injector.instanceOf[ReviewPayDatesView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(date), form, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when 'no more pay dates' is submitted" in {

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
        FakeRequest(POST, reviewPayDatesRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to the next page when 'pay dates' is submitted" in {

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
        FakeRequest(POST, reviewPayDatesRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual addMoreRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val date = LocalDate.of(2020, 3, 1)

      val userAnswers = UserAnswers("id").set(PayDatePage, date, Some(1))
        .getOrElse(fail("Could not initialise user answers with PayDate data"))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, reviewPayDatesRoute)
          .withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[ReviewPayDatesView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(Seq(date), boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, reviewPayDatesRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, reviewPayDatesRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
