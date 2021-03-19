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

package utils

import cats.data.Validated.Valid
import models.requests.DataRequest
import pages.{FirstFurloughDatePage, FurloughStartDatePage}
import uk.gov.hmrc.http.InternalServerException

import java.time.LocalDate

trait KeyDatesUtil {

  def firstFurloughDate()(implicit request: DataRequest[_]): LocalDate =
    request.userAnswers.getV(FirstFurloughDatePage) match {
      case Valid(firstFurloughDate) => firstFurloughDate
      case _ =>
        request.userAnswers.getV(FurloughStartDatePage) match {
          case Valid(furloughStartDate) => furloughStartDate
          case _ =>
            throw new InternalServerException("[KeyDatesUtil][firstFurloughDate] could not determine first furlough date")
        }
    }
}
