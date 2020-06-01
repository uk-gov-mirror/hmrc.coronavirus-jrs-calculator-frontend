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

import models.UserAnswers.AnswerV
import models.{FurloughDates, FurloughEnded, FurloughOngoing, FurloughWithinClaim, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import utils.LocalDateHelpers
import cats.syntax.apply._

trait FurloughPeriodExtractor extends LocalDateHelpers {

  @deprecated("Use validated API instead", "1.0.0")
  def extractFurloughWithinClaim(userAnswers: UserAnswers): Option[FurloughWithinClaim] =
    for {
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
      furloughDates    <- extractFurloughPeriod(userAnswers)
    } yield {
      val startDate = latestOf(claimPeriodStart, furloughDates.start)
      val endDate = furloughDates match {
        case FurloughOngoing(_)            => claimPeriodEnd
        case FurloughEnded(_, furloughEnd) => earliestOf(claimPeriodEnd, furloughEnd)
      }
      FurloughWithinClaim(startDate, endDate)
    }

  def extractFurloughWithinClaimV(userAnswers: UserAnswers): AnswerV[FurloughWithinClaim] =
    (
      userAnswers.getV(ClaimPeriodStartPage),
      userAnswers.getV(ClaimPeriodEndPage),
      extractFurloughPeriodV(userAnswers)
    ).mapN { (claimPeriodStart, claimPeriodEnd, furloughDates) =>
      val startDate = latestOf(claimPeriodStart, furloughDates.start)
      val endDate = furloughDates match {
        case FurloughOngoing(_)            => claimPeriodEnd
        case FurloughEnded(_, furloughEnd) => earliestOf(claimPeriodEnd, furloughEnd)
      }
      FurloughWithinClaim(startDate, endDate)
    }

  @deprecated("Use validated API instead", "1.0.0")
  def extractFurloughPeriod(userAnswers: UserAnswers): Option[FurloughDates] =
    userAnswers
      .get(FurloughStartDatePage)
      .map(FurloughDates(_, userAnswers.get(FurloughEndDatePage)))

  def extractFurloughPeriodV(
    userAnswers: UserAnswers
  ): AnswerV[FurloughDates] =
    userAnswers.getV(FurloughStartDatePage).map { startDate =>
      FurloughDates(startDate, userAnswers.getV(FurloughEndDatePage).toOption)
    }
}
