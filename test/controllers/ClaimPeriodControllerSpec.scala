/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import forms.ClaimPeriodFormProvider
import models.{ClaimPeriodModel, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimPeriodPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ClaimPeriodView

import scala.concurrent.Future

class ClaimPeriodControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new ClaimPeriodFormProvider()
  private def form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = ClaimPeriodModel(LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC))

  lazy val claimPeriodRoute = routes.ClaimPeriodController.onPageLoad(NormalMode).url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, claimPeriodRoute)
      .withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, claimPeriodRoute)
      .withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "startDateValue.day"   -> validAnswer.startDate.getDayOfMonth.toString,
        "startDateValue.month" -> validAnswer.startDate.getMonthValue.toString,
        "startDateValue.year"  -> validAnswer.startDate.getYear.toString,
        "endDateValue.day"   -> validAnswer.endDate.getDayOfMonth.toString,
        "endDateValue.month" -> validAnswer.endDate.getMonthValue.toString,
        "endDateValue.year"  -> validAnswer.endDate.getYear.toString
      )

  "ClaimPeriod Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[ClaimPeriodView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(getRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when user answers is not present" in {

      val application = applicationBuilder(userAnswers = None).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[ClaimPeriodView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ClaimPeriodPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[ClaimPeriodView]

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

    "redirect to the next page when valid data is submitted and user answer is not present" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = None)
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
        FakeRequest(POST, claimPeriodRoute)
          .withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ClaimPeriodView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }
  }
}
