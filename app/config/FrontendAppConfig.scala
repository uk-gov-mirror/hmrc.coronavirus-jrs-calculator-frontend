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

package config

import java.time.LocalDate

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import scala.util.Try

@Singleton
class FrontendAppConfig @Inject()(val configuration: Configuration)
    extends UrlConfiguration with SchemeConfiguration with MongoConfiguration {

  lazy val host: String = configuration.get[String]("host")
  lazy val appName: String = configuration.get[String]("appName")

  private val contactHost = configuration.get[String]("contact-frontend.host")

  private val serviceIdentifier = "jrsc"

  def reportAccessibilityIssueUrl(problemPageUri: String): String =
    s"$contactHost/contact/accessibility-unauthenticated?service=$serviceIdentifier&userAction=${SafeRedirectUrl(host + problemPageUri).encodedUrl}"

  val gtmContainer: Option[String] = (Try {
    configuration.get[String]("gtm.container")
  } map {
    case "main"         => Some("GTM-NDJKHWK")
    case "transitional" => Some("GTM-TSFTCWZ")
  }) getOrElse None

  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$serviceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$serviceIdentifier"

  val contactStandaloneForm = s"$contactHost/contact/contact-hmrc-unauthenticated?service=$serviceIdentifier"

  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$serviceIdentifier"

  lazy val timeout: Int = configuration.get[Int]("timeout.timeout")
  lazy val countdown: Int = configuration.get[Int]("timeout.countdown")

  lazy val authUrl: String = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  private val feedbackSurveyFEUrl: String = configuration.get[String]("microservice.services.feedback-survey.url")
  val feedbackUrl: String = s"$feedbackSurveyFEUrl/$serviceIdentifier"

  lazy val languageTranslationEnabled: Boolean = configuration.get[Boolean]("features.welsh-translation")

  lazy val cookies: String = host + configuration.get[String]("urls.footer.cookies")
  lazy val privacy: String = host + configuration.get[String]("urls.footer.privacy")
  lazy val termsConditions: String = host + configuration.get[String]("urls.footer.termsConditions")
  lazy val govukHelp: String = configuration.get[String]("urls.footer.govukHelp")
}

import pureconfig.ConfigSource
import pureconfig.generic.auto._ // Do not remove this

trait SchemeConfiguration extends CamelCaseConf {
  lazy val schemeConf: SchemeConf = ConfigSource.default.at("scheme").loadOrThrow[SchemeConf]

  lazy val schemeStartDate = LocalDate.parse(schemeConf.startDate)
  lazy val schemeEndDate = LocalDate.parse(schemeConf.endDate)
  lazy val phaseTwoStartDate = LocalDate.parse(schemeConf.phaseTwoStartDate)
}

trait MongoConfiguration extends CamelCaseConf {
  lazy val mongoConf: MongoConf = ConfigSource.default.at("mongodb").loadOrThrow[MongoConf]
}

trait CalculatorVersionConfiguration extends CamelCaseConf {
  lazy val calculatorVersionConf: String = ConfigSource.default.at("calculator.version").loadOrThrow[String]
}

final case class SchemeConf(startDate: String, endDate: String, phaseTwoStartDate: String)
final case class MongoConf(uri: String, timeToLiveInSeconds: String)
