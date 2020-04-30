/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import forms.SalaryQuestionFormProvider
import models.PaymentFrequency.{FourWeekly, Weekly}
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{PaymentFrequencyPage, SalaryQuestionPage}
import play.api.inject.bind
import play.api.libs.json.{JsNumber, JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.SalaryQuestionView

import scala.concurrent.Future

class SalaryQuestionControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new SalaryQuestionFormProvider()
  val form = formProvider()

  lazy val salaryQuestionRoute = routes.SalaryQuestionController.onPageLoad().url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      SalaryQuestionPage.toString   -> JsNumber(111),
      PaymentFrequencyPage.toString -> JsString("fourweekly")
    )
  )

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, salaryQuestionRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  "SalaryQuestion Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = UserAnswers(userAnswersId).set(PaymentFrequencyPage, Weekly).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[SalaryQuestionView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, Weekly)(getRequest, messages).toString

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
        FakeRequest(POST, salaryQuestionRoute)
          .withFormUrlEncodedBody(("salary", "111"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, salaryQuestionRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("salary", "invalid value"))

      val boundForm = form.bind(Map("salary" -> "invalid value"))

      val view = application.injector.instanceOf[SalaryQuestionView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, FourWeekly)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, salaryQuestionRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, salaryQuestionRoute)
          .withFormUrlEncodedBody(("salary", "111"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
