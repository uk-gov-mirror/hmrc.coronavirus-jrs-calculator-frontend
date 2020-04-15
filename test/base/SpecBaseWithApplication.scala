/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package base

import config.FrontendAppConfig
import controllers.actions._
import models.UserAnswers
import org.scalatest.TryValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest

trait SpecBaseWithApplication extends PlaySpec with GuiceOneAppPerSuite with TryValues with ScalaFutures with IntegrationPatience {

  val userAnswersId = "id"

  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withCSRFToken.asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )

  def dummyUserAnswers = Json.parse(userAnswersJson).as[UserAnswers]
  private val userAnswersJson: String =
    """
      |{
      |    "_id" : "session-3fdd2682-dad1-48e1-80d6-8c1480696811",
      |    "data" : {
      |        "taxYearPayDate" : "2020-04-20",
      |        "furloughQuestion" : "yes",
      |        "payQuestion" : "regularly",
      |        "pensionAutoEnrolment" : "optedIn",
      |        "claimPeriodEnd" : "2020-04-30",
      |        "paymentFrequency" : "monthly",
      |        "salary" : {
      |            "amount" : 2000.0
      |        },
      |        "nicCategory" : "payable",
      |        "claimPeriodStart" : "2020-03-01",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-31",
      |            "2020-04-30"
      |        ]
      |    },
      |    "lastUpdated" : {
      |        "$date": 1586873457650
      |    }
      |}
      |""".stripMargin
}
