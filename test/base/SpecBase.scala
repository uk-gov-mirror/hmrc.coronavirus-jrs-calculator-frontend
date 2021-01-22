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

  //  val emptyUserAnswers: UserAnswers = UserAnswers(internalId, Json.obj())

  val internalId = "id"
  val empref = "840-GZ00064"
  val viewClaimBlockedEmpref = "123-abc13d"
  val arn = "AB123456"
  val claimID = "123ABCD"
  val fakeClaimID = "F4K3-C141M"

  def onwardRoute = Call("GET", "/foo")

  def blankUserAnswers: UserAnswers = UserAnswers(UUID.randomUUID().toString, Json.obj())

//  lazy val fakeRequest = FakeRequest("GET", "/foo").withSession(SessionKeys.sessionId -> "foo")

  lazy val fakeDataRequest = DataRequest(fakeRequest, internalId, blankUserAnswers)

  def fakeDataRequest(headers: (String, String)*): DataRequest[_] =
    DataRequest(fakeRequest.withHeaders(headers: _*), internalId, blankUserAnswers)

  def fakeDataRequest(userAnswers: UserAnswers, headers: (String, String)*): DataRequest[_] =
    DataRequest(fakeRequest.withHeaders(headers: _*), internalId, userAnswers)

  def fakeDataRequest(userAnswers: UserAnswers): DataRequest[_] = DataRequest(fakeRequest, internalId, userAnswers)

  def fakeOptDataRequest(userAnswers: Option[UserAnswers] = None): OptionalDataRequest[_] =
    OptionalDataRequest(fakeRequest, internalId, userAnswers)

  implicit val defaultTimeout: FiniteDuration = 5.seconds

  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  def title(heading: String, section: Option[String] = None)(implicit messages: Messages) =
    s"$heading - ${section.fold("")(_ + " - ")}${messages("service.name")} - ${messages("site.govuk")}"

  def titleOf(result: String): String = Jsoup.parse(result).title

  lazy val injector: Injector = app.injector

  implicit lazy val frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

  //    lazy val userAnswersService = injector.instanceOf[UserAnswersService]

  lazy val messagesControllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  implicit lazy val errorHandler = injector.instanceOf[ErrorHandler]

  lazy val dataRequiredAction = injector.instanceOf[DataRequiredActionImpl]

  implicit val hc = HeaderCarrier()

  val component = app.injector.instanceOf[MessagesControllerComponents]
  val identifier = app.injector.instanceOf[FakeIdentifierAction]
  val dataRequired = app.injector.instanceOf[DataRequiredActionImpl]
  val navigator = app.injector.instanceOf[Navigator]
//  val dataRetrieval = new DataRetrievalActionImpl(mockSessionRepository)
  implicit val appConf: FrontendAppConfig = new FrontendAppConfig

  implicit class AnswerHelpers[A](val answer: AnswerV[A]) {}

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  implicit val messages: Messages = messagesApi.preferred(fakeRequest)

  private val configKeyValues: Set[(String, ConfigValue)] = app.injector.instanceOf[Configuration].entrySet

  def configValues(kv: (String, Any)): List[(String, Any)] =
    configKeyValues.toMap.+(kv._1 -> kv._2).toList

}
