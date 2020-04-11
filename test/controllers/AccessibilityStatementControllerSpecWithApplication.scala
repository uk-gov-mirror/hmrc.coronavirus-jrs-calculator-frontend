/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.AccessibilityStatementView

class AccessibilityStatementControllerSpecWithApplication extends SpecBaseWithApplication {

  "Accessibility Statement Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.AccessibilityStatementController.onPageLoad().url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AccessibilityStatementView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(request, messages).toString

      application.stop()
    }
  }
}
