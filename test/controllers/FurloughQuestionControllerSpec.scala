/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import forms.FurloughQuestionFormProvider
import models.{FurloughQuestion, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughQuestionPage}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.FurloughQuestionView

import scala.concurrent.Future

class FurloughQuestionControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val furloughQuestionRoute = routes.FurloughQuestionController.onPageLoad(NormalMode).url

  val formProvider = new FurloughQuestionFormProvider()
  val form = formProvider()

  val start = LocalDate.of(2020, 3, 1)
  val end = LocalDate.of(2020, 4, 1)

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      ClaimPeriodStartPage.toString -> start,
      ClaimPeriodEndPage.toString   -> end
    )
  )

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, furloughQuestionRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  "FurloughQuestion Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[FurloughQuestionView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, start, end, NormalMode)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers1 = userAnswers.set(FurloughQuestionPage, FurloughQuestion.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers1)).build()

      val view = application.injector.instanceOf[FurloughQuestionView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(FurloughQuestion.values.head), start, end, NormalMode)(getRequest, messages).toString

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
        FakeRequest(POST, furloughQuestionRoute)
          .withFormUrlEncodedBody(("value", FurloughQuestion.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, furloughQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[FurloughQuestionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, start, end, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, furloughQuestionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, furloughQuestionRoute)
          .withFormUrlEncodedBody(("value", FurloughQuestion.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
