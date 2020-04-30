/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import forms.PaymentFrequencyFormProvider
import models.{PaymentFrequency, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.PaymentFrequencyPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.PaymentFrequencyView

import scala.concurrent.Future

class PaymentFrequencyControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val paymentFrequencyRoute = routes.PaymentFrequencyController.onPageLoad().url
  lazy val paymentFrequencyRoutePost = routes.PaymentFrequencyController.onSubmit().url

  val formProvider = new PaymentFrequencyFormProvider()
  val form = formProvider()

  lazy val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, paymentFrequencyRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  lazy val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, paymentFrequencyRoutePost).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "value" -> "weekly"
      )

  "PaymentFrequency Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[PaymentFrequencyView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(PaymentFrequencyPage, PaymentFrequency.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[PaymentFrequencyView]

      val result = route(application, getRequest).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(PaymentFrequency.values.head))(getRequest, messages).toString

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

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[PaymentFrequencyView]

      val request =
        FakeRequest(POST, paymentFrequencyRoutePost).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
