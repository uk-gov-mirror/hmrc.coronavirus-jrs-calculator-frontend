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

package base

import java.util.UUID

import com.typesafe.config.ConfigValue
import config.FrontendAppConfig
import controllers.actions.{DataRequiredActionImpl, FakeIdentifierAction}
import handlers.ErrorHandler
import models.UserAnswers
import models.UserAnswers.AnswerV
import models.requests.{DataRequest, OptionalDataRequest}
import navigation.Navigator
import org.jsoup.Jsoup
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Call, MessagesControllerComponents}
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.concurrent.{Await, ExecutionContext, Future}

trait SpecBase
    extends WordSpec with MustMatchers with GuiceOneAppPerSuite with TryValues with OptionValues with ScalaFutures with IntegrationPatience
    with BeforeAndAfterEach {

  override def beforeEach(): Unit =
    super.beforeEach()

  lazy val injector: Injector = app.injector

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  implicit val defaultTimeout: FiniteDuration = 5.seconds

  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  implicit lazy val frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit val appConf: FrontendAppConfig = new FrontendAppConfig

  implicit lazy val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  implicit class AnswerHelpers[A](val answer: AnswerV[A]) {}

  def onwardRoute: Call = Call("GET", "/foo")

  def blankUserAnswers: UserAnswers = UserAnswers(UUID.randomUUID().toString, Json.obj())

  lazy val messagesControllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  val component: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]
  val identifier: FakeIdentifierAction = app.injector.instanceOf[FakeIdentifierAction]
  val dataRequired: DataRequiredActionImpl = app.injector.instanceOf[DataRequiredActionImpl]
  val navigator: Navigator = app.injector.instanceOf[Navigator]
  lazy val dataRequiredAction: DataRequiredActionImpl = injector.instanceOf[DataRequiredActionImpl]

  val internalId = "id"

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = {
    FakeRequest("", "").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  }

  lazy val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = {
    DataRequest(fakeRequest, internalId, blankUserAnswers)
  }

  def fakeDataRequest(headers: (String, String)*): DataRequest[_] =
    DataRequest(fakeRequest.withHeaders(headers: _*), internalId, blankUserAnswers)

  def fakeDataRequest(userAnswers: UserAnswers, headers: (String, String)*): DataRequest[_] =
    DataRequest(fakeRequest.withHeaders(headers: _*), internalId, userAnswers)

  def fakeDataRequest(userAnswers: UserAnswers): DataRequest[_] = DataRequest(fakeRequest, internalId, userAnswers)

  def fakeOptDataRequest(userAnswers: Option[UserAnswers] = None): OptionalDataRequest[_] =
    OptionalDataRequest(fakeRequest, internalId, userAnswers)

  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  def title(heading: String, section: Option[String] = None)(implicit messages: Messages) =
    s"$heading - ${section.fold("")(_ + " - ")}${messages("service.name")} - ${messages("site.govuk")}"

  def titleOf(result: String): String = Jsoup.parse(result).title

  private val configKeyValues: Set[(String, ConfigValue)] = app.injector.instanceOf[Configuration].entrySet

  def configValues(kv: (String, Any)): List[(String, Any)] =
    configKeyValues.toMap.+(kv._1 -> kv._2).toList

}
