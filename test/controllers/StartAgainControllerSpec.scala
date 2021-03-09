/*
 * Copyright 2021 HM Revenue & Customs
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

import base.SpecBaseControllerSpecs
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimPeriodStartPage
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class StartAgainControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  lazy val startAgainRoute = routes.StartAgainController.startAgain().url
  lazy val getRequest      = FakeRequest(GET, startAgainRoute)

  val controller = new StartAgainController(component, navigator, identifier, dataRetrieval, mockSessionRepository)

  "StartAgainController Controller" must {

    "clear user answers and redirect" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(
        Some(emptyUserAnswers.set(ClaimPeriodStartPage, LocalDate.now()).success.value))

      val result = controller.startAgain()(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.RootPageController.onPageLoad().url)

      verify(mockSessionRepository, times(1)).set(any())
    }
  }
}
