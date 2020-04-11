/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SessionExpiredView

class SessionExpiredControllerSpecWithApplication extends SpecBaseWithApplication {

  "SessionExpired Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.SessionExpiredController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SessionExpiredView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }
  }
}
