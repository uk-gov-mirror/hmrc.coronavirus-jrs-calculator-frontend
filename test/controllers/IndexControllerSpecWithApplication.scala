/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.IndexView

class IndexControllerSpecWithApplication extends SpecBaseWithApplication {

  "Index Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[IndexView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }
  }
}
