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

import base.SpecBaseControllerSpecs
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexControllerSpec extends SpecBaseControllerSpecs with MockitoSugar {

  lazy val keepAliveRoute = routes.IndexController.keepalive().url

  lazy val getKeepAliveRequest = FakeRequest(GET, keepAliveRoute)

  val controller = new IndexController(identifier, dataRetrieval, mockSessionRepository, component)

  "StartAgainController Controller" must {

    "keepAlive request should return 204 as expected" in {
      when(mockSessionRepository.get(any())) thenReturn Future.successful(Some(emptyUserAnswers))

      val result = controller.keepalive()(getKeepAliveRequest)

      status(result) mustEqual NO_CONTENT
    }
  }
}
