/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PayQuestion
import pages.behaviours.PageBehaviours

class PayQuestionSpec extends PageBehaviours {

  "PayQuestionPage" must {

    beRetrievable[PayQuestion](PayQuestionPage)

    beSettable[PayQuestion](PayQuestionPage)

    beRemovable[PayQuestion](PayQuestionPage)
  }
}
