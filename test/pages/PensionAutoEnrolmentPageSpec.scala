/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PensionStatus
import pages.behaviours.PageBehaviours

class PensionAutoEnrolmentPageSpec extends PageBehaviours {

  "PensionAutoEnrolmentPage" must {

    beRetrievable[PensionStatus](PensionAutoEnrolmentPage)

    beSettable[PensionStatus](PensionAutoEnrolmentPage)

    beRemovable[PensionStatus](PensionAutoEnrolmentPage)
  }
}
