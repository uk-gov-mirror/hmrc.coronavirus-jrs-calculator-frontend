/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBaseWithApplication
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import views.html.ComingSoonView

class ComingSoonControllerSpec extends SpecBaseWithApplication {

  "ComingSoon Controller" must {

    val getRequest: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest(GET, routes.ComingSoonController.onPageLoad().url).withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val result = route(application, getRequest).value

      val view = application.injector.instanceOf[ComingSoonView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view()(getRequest, messages).toString

      application.stop()
    }
  }
}
