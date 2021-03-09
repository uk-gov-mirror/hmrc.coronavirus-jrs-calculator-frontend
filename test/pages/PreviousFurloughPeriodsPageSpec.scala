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

import models.UserAnswers
import pages.behaviours.PageBehaviours

import java.time.LocalDate
import scala.util.Success

class PreviousFurloughPeriodsPageSpec extends PageBehaviours {

  "PreviousFurloughPeriodsPage" must {

    beRetrievable[Boolean](PreviousFurloughPeriodsPage)

    beSettable[Boolean](PreviousFurloughPeriodsPage)

    beRemovable[Boolean](PreviousFurloughPeriodsPage)

    val emptyAnswers    = UserAnswers("test")
    val testUserAnswers = emptyAnswers.set(FirstFurloughDatePage, LocalDate.of(2020, 1, 1)).get

    "remove the First Furlough Date when answer is false OR None" in {
      PreviousFurloughPeriodsPage.cleanup(Some(false), testUserAnswers) mustBe Success(emptyAnswers)
      PreviousFurloughPeriodsPage.cleanup(None, testUserAnswers) mustBe Success(emptyAnswers)
    }

    "NOT remove the First Furlough Date when answer is true" in {
      PreviousFurloughPeriodsPage.cleanup(Some(true), testUserAnswers) mustBe Success(testUserAnswers)
    }
  }
}
