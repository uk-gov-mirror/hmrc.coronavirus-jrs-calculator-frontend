/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import forms.FurloughPartialPayFormProvider
import models.PaymentFrequency.Weekly
import models.{FurloughPartialPay, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodEndPage, FurloughEndDatePage, PartialPayAfterFurloughPage, PaymentFrequencyPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.VariableLengthPartialPayView

import scala.concurrent.Future

class PartialPayAfterFurloughControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new FurloughPartialPayFormProvider()

  val form = formProvider()

  lazy val pageLoadAfterFurloughRoute = routes.PartialPayAfterFurloughController.onPageLoad().url
  lazy val submitAfterFurloughRoute = routes.PartialPayAfterFurloughController.onSubmit().url

  def getRequest(url: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  def postRequest(url: String): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, url).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(("value", "123"))

  val claimEndDate = LocalDate.of(2020, 5, 31)
  val furloughEndDate = LocalDate.of(2020, 4, 10)

  val userAnswers = UserAnswers(userAnswersId)
    .set(ClaimPeriodEndPage, claimEndDate)
    .success
    .value
    .set(FurloughEndDatePage, furloughEndDate)
    .success
    .value
    .set(PaymentFrequencyPage, Weekly)
    .success
    .value

  "PartialPayAfterFurloughController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val r = getRequest(pageLoadAfterFurloughRoute)

      val result = route(application, r).value

      val view = application.injector.instanceOf[VariableLengthPartialPayView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, furloughEndDate.plusDays(1), furloughEndDate.plusDays(7), routes.PartialPayAfterFurloughController.onSubmit())(
          r,
          messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers1 = userAnswers.set(PartialPayAfterFurloughPage, FurloughPartialPay(111)).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers1)).build()

      val view = application.injector.instanceOf[VariableLengthPartialPayView]

      val r = getRequest(pageLoadAfterFurloughRoute)

      val result = route(application, r).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(
          form.fill(FurloughPartialPay(111)),
          furloughEndDate.plusDays(1),
          furloughEndDate.plusDays(7),
          routes.PartialPayAfterFurloughController.onSubmit()
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

      val result = route(application, postRequest(submitAfterFurloughRoute)).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to the /variable-gross-pay straightaway if there is No furlough end stored in UserAnswers" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswersUpdated = UserAnswers(userAnswersId)
        .set(ClaimPeriodEndPage, claimEndDate)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswersUpdated))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest(submitAfterFurloughRoute)).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.NicCategoryController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to the /claim-period-end if there is no stored claim-period-end date in UserAnswers" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswersUpdated = UserAnswers(userAnswersId)
        .set(FurloughEndDatePage, claimEndDate.minusDays(1))
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswersUpdated))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest(submitAfterFurloughRoute)).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ClaimPeriodEndController.onPageLoad(NormalMode).url

      application.stop()
    }

    "redirect to the /variable-gross-pay if there is not enough partial to show for furlough end" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswersUpdated = UserAnswers(userAnswersId)
        .set(ClaimPeriodEndPage, claimEndDate)
        .success
        .value
        .set(FurloughEndDatePage, claimEndDate.minusDays(1))
        .success
        .value
        .set(PaymentFrequencyPage, Weekly)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswersUpdated))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, postRequest(submitAfterFurloughRoute)).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.NicCategoryController.onPageLoad(NormalMode).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, submitAfterFurloughRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[VariableLengthPartialPayView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, furloughEndDate.plusDays(1), claimEndDate, routes.PartialPayAfterFurloughController.onSubmit())(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, pageLoadAfterFurloughRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, submitAfterFurloughRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
