/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

import models.{FurloughStatus, UserAnswers}
import pages.behaviours.PageBehaviours

class FurloughOngoingSpec extends PageBehaviours {

  "furloughOngoingPage" must {

    beRetrievable[FurloughStatus](FurloughStatusPage)

    beSettable[FurloughStatus](FurloughStatusPage)

    beRemovable[FurloughStatus](FurloughStatusPage)

    "remove furlough end date when answered 'No'" in {
      val initialAnswers = UserAnswers("id")
        .set(FurloughStatusPage, FurloughStatus.FurloughEnded)
        .success
        .get
        .set(FurloughEndDatePage, LocalDate.of(2020, 3, 1))
        .success
        .get

      val updatedAnswers = initialAnswers.set(FurloughStatusPage, FurloughStatus.FurloughOngoing).success.value

      updatedAnswers.get(FurloughEndDatePage) must not be defined
    }

  }
}
