/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimPeriodStartPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class StartAgainControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  lazy val startAgainRoute = routes.StartAgainController.startAgain().url
  lazy val getRequest = FakeRequest(GET, startAgainRoute)

  "StartAgainController Controller" must {

    "clear user answers and redirect" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers.set(ClaimPeriodStartPage, LocalDate.now()).success.value))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.RootPageController.onPageLoad().url)

      verify(mockSessionRepository, times(1)).set(any())

      application.stop()
    }
  }
}
