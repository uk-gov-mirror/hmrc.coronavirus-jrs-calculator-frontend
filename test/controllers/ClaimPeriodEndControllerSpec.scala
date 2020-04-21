/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.{LocalDate, ZoneOffset}

import base.SpecBaseWithApplication
import forms.ClaimPeriodEndFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, PaymentFrequencyPage, SalaryQuestionPage}
import play.api.inject.bind
import play.api.libs.json.{JsNumber, JsString, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.ClaimPeriodEndView

import scala.concurrent.Future

class ClaimPeriodEndControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  val formProvider = new ClaimPeriodEndFormProvider(frontendAppConfig)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val claimPeriodEndRoute = routes.ClaimPeriodEndController.onPageLoad(NormalMode).url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  val claimStart = LocalDate.of(2020, 3, 1)

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      ClaimPeriodStartPage.toString -> JsString(claimStart.toString)
    )
  )

  private def form = formProvider(claimStart)

  val getRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, claimPeriodEndRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val postRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, claimPeriodEndRoute).withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
      .withFormUrlEncodedBody(
        "endDate.day"   -> validAnswer.getDayOfMonth.toString,
        "endDate.month" -> validAnswer.getMonthValue.toString,
        "endDate.year"  -> validAnswer.getYear.toString
      )

  "ClaimPeriodEnd Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[ClaimPeriodEndView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(getRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(
        userAnswersId,
        Json.obj(
          ClaimPeriodStartPage.toString -> JsString("2020-03-10"),
          ClaimPeriodEndPage.toString   -> JsString(validAnswer.toString)
        )
      )

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val view = application.injector.instanceOf[ClaimPeriodEndView]

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

    "redirect to the /claim-period-start if there is no claim-start stored in mongo" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId)))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ClaimPeriodStartController.onPageLoad(NormalMode).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, claimPeriodEndRoute).withCSRFToken
          .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[ClaimPeriodEndView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }
  }
}
