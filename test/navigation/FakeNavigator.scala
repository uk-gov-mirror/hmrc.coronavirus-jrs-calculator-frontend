/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package navigation

import play.api.mvc.Call
import pages._
import models.{Mode, NormalMode, UserAnswers}

class FakeNavigator(desiredRoute: Call, mode: Mode = NormalMode) extends Navigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, idx: Option[Int] = None): Call =
    desiredRoute
}
