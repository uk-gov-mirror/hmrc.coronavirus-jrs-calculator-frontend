/*
 * Copyright 2021 HM Revenue & Customs
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

package pages

import cats.data.Validated.Valid
import models.{Amount, UserAnswers}
import pages.behaviours.PageBehaviours

import scala.util.Success

class HasEmployeeBeenOnStatutoryLeavePageSpec extends PageBehaviours {

  "HasEmployeeBeenOnStatutoryLeavePage" must {

    beRetrievable[Boolean](HasEmployeeBeenOnStatutoryLeavePage)

    beSettable[Boolean](HasEmployeeBeenOnStatutoryLeavePage)

    beRemovable[Boolean](HasEmployeeBeenOnStatutoryLeavePage)

    "cleanup the stat leave answers when false" in {

      val userAnswers =
        UserAnswers("foo")
          .set(StatutoryLeavePayPage, Amount(1000))
          .get
          .set(NumberOfStatLeaveDaysPage, 5)
          .get

      val result = HasEmployeeBeenOnStatutoryLeavePage.cleanup(Some(false), userAnswers)

      result.get.getO(StatutoryLeavePayPage) mustBe None
      result.get.getO(NumberOfStatLeaveDaysPage) mustBe None
    }

    "NOT cleanup the stat leave answers when true" in {

      val userAnswers =
        UserAnswers("foo")
          .set(StatutoryLeavePayPage, Amount(1000))
          .get
          .set(NumberOfStatLeaveDaysPage, 5)
          .get

      val result = HasEmployeeBeenOnStatutoryLeavePage.cleanup(Some(true), userAnswers)

      result.get.getO(StatutoryLeavePayPage) mustBe Some(Valid(Amount(1000)))
      result.get.getO(NumberOfStatLeaveDaysPage) mustBe Some(Valid(5))
    }
  }
}
