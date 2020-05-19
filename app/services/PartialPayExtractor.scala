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

package services

import models.{PartialPeriod, Period, UserAnswers}
import pages.PayDatePage

trait PartialPayExtractor extends PeriodHelper with FurloughPeriodExtractor {

  def hasPartialPayBefore(userAnswers: UserAnswers): Boolean =
    getPartialPeriods(userAnswers).exists(isFurloughStart)

  def hasPartialPayAfter(userAnswers: UserAnswers): Boolean =
    getPartialPeriods(userAnswers).exists(isFurloughEnd)

  def getPartialPeriods(userAnswers: UserAnswers): Seq[PartialPeriod] =
    extractFurloughWithinClaim(userAnswers).fold(
      Seq.empty[PartialPeriod]
    ) { furloughPeriod =>
      val payDates = userAnswers.getList(PayDatePage)
      generatePeriodsWithFurlough(payDates, furloughPeriod).collect {
        case pp: PartialPeriod => pp
      }
    }

  def getBeforeFurloughPeriodRemainder(partialPeriod: PartialPeriod): Period =
    Period(partialPeriod.original.start, partialPeriod.partial.start.minusDays(1))

  def getAfterFurloughPeriodRemainder(partialPeriod: PartialPeriod): Period =
    Period(partialPeriod.partial.end.plusDays(1), partialPeriod.original.end)
}
