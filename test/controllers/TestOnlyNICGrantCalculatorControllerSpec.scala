/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.{LocalDate, ZoneOffset}

import base.SpecBaseWithApplication
import forms.TestOnlyNICGrantCalculatorFormProvider
import models.PaymentFrequency.Weekly
import models.{NormalMode, TestOnlyNICGrantModel, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.TestOnlyNICGrantCalculatorPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.TestOnlyNICGrantCalculatorView

import scala.concurrent.Future

class TestOnlyNICGrantCalculatorControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  val formProvider = new TestOnlyNICGrantCalculatorFormProvider(frontendAppConfig)
  private def form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = TestOnlyNICGrantModel(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31), 1000, Weekly)

  lazy val testOnlyNICGrantCalculatorRoute = routes.TestOnlyNICGrantCalculatorController.onPageLoad(NormalMode).url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, testOnlyNICGrantCalculatorRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, testOnlyNICGrantCalculatorRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "startDate.day"    -> validAnswer.startDate.getDayOfMonth.toString,
        "startDate.month"  -> validAnswer.startDate.getMonthValue.toString,
        "startDate.year"   -> validAnswer.startDate.getYear.toString,
        "endDate.day"      -> validAnswer.endDate.getDayOfMonth.toString,
        "endDate.month"    -> validAnswer.endDate.getMonthValue.toString,
        "endDate.year"     -> validAnswer.endDate.getYear.toString,
        "furloughedAmount" -> "1000",
        "value"            -> "weekly"
      )

  "TestOnlyNICGrantCalculator Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[TestOnlyNICGrantCalculatorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(TestOnlyNICGrantCalculatorPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[TestOnlyNICGrantCalculatorView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode)(getRequest, messages).toString

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

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, testOnlyNICGrantCalculatorRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TestOnlyNICGrantCalculatorView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }
  }
}
