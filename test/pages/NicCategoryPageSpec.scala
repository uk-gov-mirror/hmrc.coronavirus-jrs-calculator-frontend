/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.NicCategory
import pages.behaviours.PageBehaviours

class NicCategorySpec extends PageBehaviours {

  "NicCategoryPage" must {

    beRetrievable[NicCategory](NicCategoryPage)

    beSettable[NicCategory](NicCategoryPage)

    beRemovable[NicCategory](NicCategoryPage)
  }
}
