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

package handlers

import java.time.LocalDate

import cats.data.Validated.{Invalid, Valid}
import models.UserAnswers
import models.UserAnswers.AnswerV
import pages._
import services.PreviousYearPeriod

trait LastYearPayControllerRequestHandler extends DataExtractor with PreviousYearPeriod {

  def getPayDatesV(userAnswers: UserAnswers): AnswerV[Seq[LocalDate]] = {
    import cats.syntax.apply._

    (
      userAnswers.getV(PaymentFrequencyPage),
      userAnswers.getV(LastPayDatePage),
      extractFurloughWithinClaimV(userAnswers)
    ).mapN { (frequency, lastPayDay, furloughPeriod) =>
      val payDates = userAnswers.getList(PayDatePage)
      val periods = generatePeriodsWithFurlough(payDates, furloughPeriod)
      val periodsWithPayDates = assignPayDates(frequency, periods, lastPayDay)
      val datesWithDuplicates = periodsWithPayDates.flatMap(p => previousYearPayDate(frequency, p))
      datesWithDuplicates.distinct
    }
  }

  def cylbCutoff(userAnswers: UserAnswers): LocalDate =
    getPayDatesV(userAnswers) match {
      case Valid(dates) => dates.head
      case Invalid(_)   => LocalDate.of(2019, 4, 6)
    }

}
