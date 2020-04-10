/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import models.Language
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

class LanguageSwitchControllerSpec extends FreeSpec with MustMatchers with OptionValues with ScalaFutures {

  "switching language" - {

    "when translation is enabled" - {

      "should set the language to Cymraeg" in {

        val application = new GuiceApplicationBuilder()
          .configure(
            "features.welsh-translation" -> true
          )
          .build()

        running(application) {
          val request = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.Cymraeg).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          cookies(result).get("PLAY_LANG").value.value mustEqual "cy"
        }
      }

      "should set the language to English" in {

        val application = new GuiceApplicationBuilder()
          .configure(
            "features.welsh-translation" -> true
          )
          .build()

        running(application) {
          val request = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.English).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          cookies(result).get("PLAY_LANG").value.value mustEqual "en"
        }
      }
    }

    "when translation is disabled" - {

      "should set the language to English regardless of what is requested" in {

        val application = new GuiceApplicationBuilder()
          .configure(
            "features.welsh-translation" -> false
          )
          .build()

        running(application) {
          val cymraegRequest = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.Cymraeg).url)
          val englishRequest = FakeRequest(GET, routes.LanguageSwitchController.switchToLanguage(Language.English).url)

          val cymraegResult = route(application, cymraegRequest).value
          val englishResult = route(application, englishRequest).value

          status(cymraegResult) mustEqual SEE_OTHER
          cookies(cymraegResult).get("PLAY_LANG").value.value mustEqual "en"

          status(englishResult) mustEqual SEE_OTHER
          cookies(englishResult).get("PLAY_LANG").value.value mustEqual "en"
        }
      }
    }
  }
}
