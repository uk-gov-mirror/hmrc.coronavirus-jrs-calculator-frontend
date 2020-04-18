/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import forms.VariableLengthPartialPayFormProvider
import models.PaymentFrequency.Weekly
import models.{UserAnswers, VariableLengthPartialPay}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodStartPage, FurloughStartDatePage, PaymentFrequencyPage, VariableLengthPartialPayPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.VariableLengthPartialPayView

import scala.concurrent.Future

class VariableLengthPartialPayControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new VariableLengthPartialPayFormProvider()
  val form = formProvider()

  lazy val pageLoadBeforeFurloughRoute = routes.VariableLengthPartialPayController.onPageLoadBeforeFurlough.url
  lazy val pageLoadAfterFurloughRoute = routes.VariableLengthPartialPayController.onPageLoadAfterFurlough().url

  lazy val submitBeforeFurloughRoute = routes.VariableLengthPartialPayController.onSubmitBeforeFurlough().url
  lazy val submitAfterFurloughRoute = routes.VariableLengthPartialPayController.onSubmitAfterFurlough().url

  def getRequest(url: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def postRequest(url: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(("value", "123"))

  val claimStartDate = LocalDate.of(2020, 3, 1)
  val furloughStartDate = LocalDate.of(2020, 3, 10)

  val userAnswers = UserAnswers(userAnswersId)
    .set(ClaimPeriodStartPage, claimStartDate)
    .success
    .value
    .set(FurloughStartDatePage, furloughStartDate)
    .success
    .value
    .set(PaymentFrequencyPage, Weekly)
    .success
    .value

  "VariableLengthPartialPay Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val r = getRequest(pageLoadBeforeFurloughRoute)

      val result = route(application, r).value

      val view = application.injector.instanceOf[VariableLengthPartialPayView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form,
          furloughStartDate.minusDays(7),
          furloughStartDate.minusDays(1),
          routes.VariableLengthPartialPayController.onSubmitBeforeFurlough())(r, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers1 = userAnswers.set(VariableLengthPartialPayPage, VariableLengthPartialPay(111), Some(1)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers1)).build()

      val view = application.injector.instanceOf[VariableLengthPartialPayView]

      val r = getRequest(pageLoadBeforeFurloughRoute)

      val result = route(application, r).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form.fill(VariableLengthPartialPay(111)),
          furloughStartDate.minusDays(7),
          furloughStartDate.minusDays(1),
          routes.VariableLengthPartialPayController.onSubmitBeforeFurlough()
        )(r, messages).toString

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

      val result = route(application, postRequest(submitBeforeFurloughRoute)).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual pageLoadAfterFurloughRoute

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, submitBeforeFurloughRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[VariableLengthPartialPayView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, claimStartDate, furloughStartDate.minusDays(1), routes.VariableLengthPartialPayController.onSubmitBeforeFurlough())(
          request,
          messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, pageLoadBeforeFurloughRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, submitBeforeFurloughRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
