/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.FurloughQuestion
import pages.behaviours.PageBehaviours

class FurloughQuestionSpec extends PageBehaviours {

  "FurloughQuestionPage" must {

    beRetrievable[FurloughQuestion](FurloughQuestionPage)

    beSettable[FurloughQuestion](FurloughQuestionPage)

    beRemovable[FurloughQuestion](FurloughQuestionPage)
  }
}
