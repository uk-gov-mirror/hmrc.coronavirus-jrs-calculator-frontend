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

import com.google.inject.Singleton
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import scala.util.Try
import pureconfig.ConfigSource
import pureconfig.generic.auto._ // Do not remove this

@Singleton
class FrontendAppConfig() extends UrlConfiguration with SchemeConfiguration with MongoConfiguration {

  private val configSource: String => ConfigSource = ConfigSource.default.at _
  private val serviceIdentifier = "jrsc"

  private val contactHost = configSource("contact-frontend.host").loadOrThrow[String]

  lazy val host: String = configSource("host").loadOrThrow[String]

  lazy val appName: String = configSource("appName").loadOrThrow[String]

  def reportAccessibilityIssueUrl(problemPageUri: String): String =
    s"$contactHost/contact/accessibility-unauthenticated?service=$serviceIdentifier&userAction=${SafeRedirectUrl(host + problemPageUri).encodedUrl}"

  val gtmContainer: Option[String] = (Try {
    configSource("gtm.container").loadOrThrow[String]
  } map {
    case "main"         => Some("GTM-NDJKHWK")
    case "transitional" => Some("GTM-TSFTCWZ")
  }) getOrElse None

  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$serviceIdentifier"
  val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$serviceIdentifier"
  val contactStandaloneForm = s"$contactHost/contact/contact-hmrc-unauthenticated?service=$serviceIdentifier"
  val betaFeedbackUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$serviceIdentifier"

  lazy val timeout: Int = configSource("timeout.timeout").loadOrThrow[Int]
  lazy val countdown: Int = configSource("timeout.countdown").loadOrThrow[Int]

  lazy val authUrl: String = configSource("auth").loadOrThrow[Service].baseUrl
  lazy val loginUrl: String = configSource("urls.login").loadOrThrow[String]
  lazy val loginContinueUrl: String = configSource("urls.loginContinue").loadOrThrow[String]

  private val feedbackSurveyFEUrl: String = configSource("microservice.services.feedback-survey.url").loadOrThrow[String]
  val feedbackUrl: String = s"$feedbackSurveyFEUrl/$serviceIdentifier"

  lazy val languageTranslationEnabled: Boolean = configSource("features.welsh-translation").loadOrThrow[Boolean]

  lazy val cookies: String = host + configSource("urls.footer.cookies").loadOrThrow[String]
  lazy val privacy: String = host + configSource("urls.footer.privacy").loadOrThrow[String]
  lazy val termsConditions: String = host + configSource("urls.footer.termsConditions").loadOrThrow[String]
  lazy val govukHelp: String = configSource("urls.footer.govukHelp").loadOrThrow[String]
}

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
