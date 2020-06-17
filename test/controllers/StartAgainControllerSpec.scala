/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import java.time.LocalDate

import base.SpecBaseWithApplication
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimPeriodStartPage
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

      val application = applicationBuilder(
        userAnswers = Some(emptyUserAnswers.set(ClaimPeriodStartPage, LocalDate.now()).success.value),
        sessionRepository = mockSessionRepository
      ).build()

      val result = route(application, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.RootPageController.onPageLoad().url)

      verify(mockSessionRepository, times(1)).set(any())

      application.stop()
    }
  }
}
