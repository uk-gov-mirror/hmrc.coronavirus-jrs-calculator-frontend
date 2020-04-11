/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.UnauthorisedView

class UnauthorisedControllerSpecWithApplication extends SpecBaseWithApplication {

  "Unauthorised Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, routes.UnauthorisedController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[UnauthorisedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }
  }
}
