/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package pages

import models.Salary
import pages.behaviours.PageBehaviours

class SalaryQuestionPageSpec extends PageBehaviours {

  "SalaryQuestionPage" must {

    beRetrievable[Salary](SalaryQuestionPage)

    beSettable[Salary](SalaryQuestionPage)

    beRemovable[Salary](SalaryQuestionPage)
  }
}
