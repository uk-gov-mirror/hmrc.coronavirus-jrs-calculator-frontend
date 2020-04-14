/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class PensionAutoEnrolmentPageSpec extends PageBehaviours {

  "PensionAutoEnrolmentPage" must {

    beRetrievable[Boolean](PensionAutoEnrolmentPage)

    beSettable[Boolean](PensionAutoEnrolmentPage)

    beRemovable[Boolean](PensionAutoEnrolmentPage)
  }
}
