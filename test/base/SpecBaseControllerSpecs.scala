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

package base

import com.typesafe.config.ConfigValue
import config.FrontendAppConfig
import controllers.actions._
import handlers.ErrorHandler
import models.UserAnswers.AnswerV
import navigation.Navigator
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import repositories.SessionRepository
import utils.CoreTestData
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait SpecBaseControllerSpecs extends PlaySpec with GuiceOneAppPerSuite with CoreTestData with MockitoSugar {

  def injector: Injector = app.injector
  def messagesApi = app.injector.instanceOf[MessagesApi]
  val component = app.injector.instanceOf[MessagesControllerComponents]
  val identifier = app.injector.instanceOf[FakeIdentifierAction]
  val dataRequired = app.injector.instanceOf[DataRequiredActionImpl]
  val navigator = app.injector.instanceOf[Navigator]
  val dataRetrieval = new DataRetrievalActionImpl(mockSessionRepository)
  implicit val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]
  implicit val appConf: FrontendAppConfig = new FrontendAppConfig

  implicit class AnswerHelpers[A](val answer: AnswerV[A]) {}
  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  private val configKeyValues: Set[(String, ConfigValue)] = app.injector.instanceOf[Configuration].entrySet

  def configValues(kv: (String, Any)): List[(String, Any)] =
    configKeyValues.toMap.+(kv._1 -> kv._2).toList

  //TODO controllers should not have this!
  lazy val mockSessionRepository: SessionRepository = {
    val mockSession = mock[SessionRepository]
    when(mockSession.set(any())) thenReturn Future.successful(true)
    mockSession
  }
}
