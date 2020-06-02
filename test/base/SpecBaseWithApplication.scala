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

import cats.data.Chain
import cats.data.Validated.Invalid
import config.FrontendAppConfig
import controllers.actions._
import models.UserAnswers
import models.UserAnswers.AnswerV
import org.scalatest.TryValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.{JsError, JsPath, JsonValidationError}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import utils.CoreTestData

trait SpecBaseWithApplication
    extends PlaySpec with GuiceOneAppPerSuite with TryValues with ScalaFutures with IntegrationPatience with CoreTestData {

  def injector: Injector = app.injector

  implicit class AnswerHelpers[A](val answer: AnswerV[A]) {}

  implicit class JsErrorValidationHelpers(source: JsError) {
    import cats.syntax.validated._
    def invalidated[A]: AnswerV[A] = source.invalidNec[A]
  }

  def emptyError(
    path: JsPath,
    error: String = "error.path.missing"
  ): Invalid[Chain[JsError]] =
    Invalid(Chain(JsError(path -> JsonValidationError(List(error)))))

  def emptyError(
    path: JsPath,
    idx: Int,
    error: String
  ): Invalid[Chain[JsError]] =
    Invalid(Chain(JsError((path \ (idx - 1)) -> JsonValidationError(List(error)))))

  implicit def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None, config: Map[String, Any] = Map()): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )
      .configure(config)
}
