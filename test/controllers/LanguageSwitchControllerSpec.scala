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
import config.FrontendAppConfig
import models.Language
import play.api.test.FakeRequest
import play.api.test.Helpers._

class LanguageSwitchControllerSpec extends SpecBaseControllerSpecs {

  def languageSwitchController(appConf: FrontendAppConfig) = new LanguageSwitchController(appConf, messagesApi, component)

  "switching language when translation is enabled" should {
    "should set the language to Cymraeg" in {

      val controller = languageSwitchController(new FrontendAppConfig() {
        override lazy val languageTranslationEnabled: Boolean = true
      })

      val request = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.Cymraeg).url)

      val result = controller.switchToLanguage(Language.Cymraeg)(request)

      status(result) mustEqual SEE_OTHER
      cookies(result).get("PLAY_LANG").value.value mustEqual "cy"
    }

    "set the language to English" in {
      val controller = languageSwitchController(new FrontendAppConfig() {
        override lazy val languageTranslationEnabled: Boolean = true
      })

      val request = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.English).url)

      val result = controller.switchToLanguage(Language.English)(request)

      status(result) mustEqual SEE_OTHER
      cookies(result).get("PLAY_LANG").value.value mustEqual "en"
    }
  }

  "when translation is disabled" should {

    "should set the language to English regardless of what is requested" in {
      implicit val appConf: FrontendAppConfig = new FrontendAppConfig {
        override lazy val languageTranslationEnabled: Boolean = false
      }
      val controller = new LanguageSwitchController(appConf, messagesApi, component)

      val cymraegRequest = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.Cymraeg).url)
      val englishRequest = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.English).url)

      val cymraegResult = controller.switchToLanguage(Language.Cymraeg)(cymraegRequest)
      val englishResult = controller.switchToLanguage(Language.English)(englishRequest)

      status(cymraegResult) mustEqual SEE_OTHER
      cookies(cymraegResult).get("PLAY_LANG").value.value mustEqual "en"

      status(englishResult) mustEqual SEE_OTHER
      cookies(englishResult).get("PLAY_LANG").value.value mustEqual "en"
    }
  }
}
