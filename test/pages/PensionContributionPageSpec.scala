/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.PensionContribution
import pages.behaviours.PageBehaviours

class PensionContributionPageSpec extends PageBehaviours {

  "PensionAutoEnrolmentPage" must {

    beRetrievable[PensionContribution](PensionContributionPage)

    beSettable[PensionContribution](PensionContributionPage)

    beRemovable[PensionContribution](PensionContributionPage)
  }
}
