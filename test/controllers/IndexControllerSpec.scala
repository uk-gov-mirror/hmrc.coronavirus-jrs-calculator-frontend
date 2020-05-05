/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository

import scala.concurrent.Future

class IndexControllerSpec extends SpecBaseWithApplication with MockitoSugar {

  lazy val keepAliveRoute = routes.IndexController.keepalive().url

  lazy val getKeepAliveRequest = FakeRequest(GET, keepAliveRoute)

  "StartAgainController Controller" must {

    "keepAlive request should return 204 as expected" in {

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      val result = route(application, getKeepAliveRequest).value

      status(result) mustEqual NO_CONTENT

      application.stop()
    }
  }
}
