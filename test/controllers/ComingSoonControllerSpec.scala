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
