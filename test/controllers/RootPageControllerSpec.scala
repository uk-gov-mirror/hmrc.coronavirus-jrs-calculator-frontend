/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.{SpecBase, SpecBaseWithApplication}
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import views.html.RootPageView

class RootPageControllerSpec extends SpecBaseWithApplication {

  "RootPage Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.RootPageController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[RootPageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }
  }
}
