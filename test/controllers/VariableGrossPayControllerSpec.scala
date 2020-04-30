/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import forms.VariableGrossPayFormProvider
import models.{UserAnswers, VariableGrossPay}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{EmployeeStartDatePage, FurloughStartDatePage, VariableGrossPayPage}
import play.api.inject.bind
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.VariableGrossPayView

import scala.concurrent.Future

class VariableGrossPayControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new VariableGrossPayFormProvider()
  val form = formProvider()

  lazy val variableGrossPayRoute = routes.VariableGrossPayController.onPageLoad().url

  val furloughStart = LocalDate.parse("2020-04-01")
  val empStart = LocalDate.parse("2020-02-01")

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, variableGrossPayRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, variableGrossPayRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(("value", "123"))

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      FurloughStartDatePage.toString -> JsString(furloughStart.toString),
      EmployeeStartDatePage.toString -> JsString(empStart.toString)
    )
  )

  "VariableGrossPay Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[VariableGrossPayView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, furloughStart)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswersUpdated = userAnswers.set(VariableGrossPayPage, VariableGrossPay(111)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswersUpdated)).build()

      val view = application.injector.instanceOf[VariableGrossPayView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(VariableGrossPay(111)), furloughStart)(getRequest, messages).toString

      application.stop()
    }

    "redirect GET to coming soon if variable journey feature is disabled" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), Map("variable.journey.enabled" -> false))
        .build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ComingSoonController.onPageLoad().url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect POST to coming soon if variable journey feature is disabled" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), Map("variable.journey.enabled" -> false))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ComingSoonController.onPageLoad().url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, variableGrossPayRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[VariableGrossPayView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, furloughStart)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, variableGrossPayRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, variableGrossPayRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
