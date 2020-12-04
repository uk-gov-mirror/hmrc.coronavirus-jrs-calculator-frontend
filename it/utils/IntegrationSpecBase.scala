package utils

import models.UserAnswers
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{TryValues, _}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Environment, Mode}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

trait IntegrationSpecBase extends WordSpec
  with GivenWhenThen with TestSuite with ScalaFutures with IntegrationPatience with Matchers
  with WiremockHelper
  with GuiceOneServerPerSuite with TryValues
  with BeforeAndAfterEach with BeforeAndAfterAll with Eventually with CreateRequestHelper with CustomMatchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build
  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
    "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "microservice.services.job-retention-scheme-calculator.host" -> mockHost,
    "microservice.services.job-retention-scheme-calculator.port" -> mockPort,
  )

  lazy val mongo: SessionRepository = app.injector.instanceOf[SessionRepository]

  def setAnswers(userAnswers: UserAnswers)(implicit timeout: Duration): Unit = Await.result(mongo.set(userAnswers), timeout)
  def getAnswers(id: String)(implicit timeout: Duration): Option[UserAnswers] = Await.result(mongo.get(id), timeout)

  override def beforeAll(): Unit = {
    super.beforeAll()
    stopWiremock()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

}
