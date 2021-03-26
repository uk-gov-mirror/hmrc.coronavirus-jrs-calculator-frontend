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

package config

import java.time.LocalDate
import config.featureSwitch.{FeatureSwitching, WelshLanguageFeature}
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl

import scala.util.Try
import pureconfig.ConfigSource
import pureconfig.generic.auto._ // do not remove this
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class FrontendAppConfig @Inject()(val servicesConfig: ServicesConfig)
    extends UrlConfiguration with SchemeConfiguration with MongoConfiguration with FeatureSwitching {

  private val configSource: String => ConfigSource = ConfigSource.default.at
  private val serviceIdentifier                    = "jrsc"

  private val contactHost = configSource("contact-frontend.host").loadOrThrow[String]

  lazy val host: String = configSource("host").loadOrThrow[String]

  lazy val appName: String = configSource("appName").loadOrThrow[String]

  def reportAccessibilityIssueUrl(problemPageUri: String): String =
    s"$contactHost/contact/accessibility-unauthenticated?service=$serviceIdentifier&userAction=${SafeRedirectUrl(host + problemPageUri).encodedUrl}"

  val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$serviceIdentifier"
  val reportAProblemNonJSUrl   = s"$contactHost/contact/problem_reports_nonjs?service=$serviceIdentifier"
  val contactStandaloneForm    = s"$contactHost/contact/contact-hmrc-unauthenticated?service=$serviceIdentifier"
  val betaFeedbackUrl          = s"$contactHost/contact/beta-feedback-unauthenticated?service=$serviceIdentifier"

  lazy val timeout: Int   = configSource("timeout.timeout").loadOrThrow[Int]
  lazy val countdown: Int = configSource("timeout.countdown").loadOrThrow[Int]

  lazy val authUrl: String          = configSource("auth").loadOrThrow[Service].baseUrl
  lazy val loginUrl: String         = configSource("urls.login").loadOrThrow[String]
  lazy val loginContinueUrl: String = configSource("urls.loginContinue").loadOrThrow[String]

  private val feedbackSurveyFEUrl: String = configSource("microservice.services.feedback-survey.url").loadOrThrow[String]
  val feedbackUrl: String                 = s"$feedbackSurveyFEUrl/$serviceIdentifier"

  def languageTranslationEnabled: Boolean = isEnabled(WelshLanguageFeature)(this)

  lazy val cookies: String         = host + configSource("urls.footer.cookies").loadOrThrow[String]
  lazy val privacy: String         = host + configSource("urls.footer.privacy").loadOrThrow[String]
  lazy val termsConditions: String = host + configSource("urls.footer.termsConditions").loadOrThrow[String]
  lazy val govukHelp: String       = configSource("urls.footer.govukHelp").loadOrThrow[String]

  lazy val usualHours: String             = configSource("usualHours").loadOrThrow[String]
  lazy val stepsBeforeCalculation: String = configSource("stepsBeforeCalculation").loadOrThrow[String]
  lazy val exampleWages: String           = configSource("exampleWages").loadOrThrow[String]
  lazy val calculateClaimAmount: String   = configSource("calculateClaimAmount").loadOrThrow[String]

  lazy val phaseTwoReferencePayBreakdownDynamicMessageDate =
    LocalDate.parse(configSource("phaseTwoReferencePayBreakdownDynamicMessageDate").loadOrThrow[String])

  lazy val employeeStartDatePostCovid = LocalDate.parse(configSource("employeeStartDatePostCovid").loadOrThrow[String])

}

trait SchemeConfiguration extends CamelCaseConf {

  lazy val schemeConf: SchemeConf = ConfigSource.default.at("scheme").loadOrThrow[SchemeConf]

  lazy val schemeStartDate           = LocalDate.parse(schemeConf.startDate)
  lazy val schemeEndDate             = LocalDate.parse(schemeConf.endDate)
  lazy val phaseTwoStartDate         = LocalDate.parse(schemeConf.phaseTwoStartDate)
  lazy val extensionStartDate        = LocalDate.parse(schemeConf.extensionStartDate)
  lazy val may2021extensionStartDate = LocalDate.parse(schemeConf.may2021extensionStartDate)
}

trait MongoConfiguration extends CamelCaseConf {
  lazy val mongoConf: MongoConf = ConfigSource.default.at("mongodb").loadOrThrow[MongoConf]
}

trait CalculatorVersionConfiguration extends CamelCaseConf {
  lazy val calculatorVersionConf: String = ConfigSource.default.at("calculator.version").loadOrThrow[String]
}

final case class SchemeConf(startDate: String,
                            endDate: String,
                            phaseTwoStartDate: String,
                            extensionStartDate: String,
                            may2021extensionStartDate: String)

final case class MongoConf(uri: String, timeToLiveInSeconds: Int)
