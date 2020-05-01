/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PensionStatus
import pages.behaviours.PageBehaviours

class PensionStatusPageSpec extends PageBehaviours {

  "PensionAutoEnrolmentPage" must {

    beRetrievable[PensionStatus](PensionStatusPage)

    beSettable[PensionStatus](PensionStatusPage)

    beRemovable[PensionStatus](PensionStatusPage)
  }
}
