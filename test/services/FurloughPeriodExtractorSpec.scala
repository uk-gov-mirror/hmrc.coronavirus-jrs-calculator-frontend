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

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import generators.Generators
import models.{FurloughEnded, FurloughOngoing, FurloughWithinClaim, UserAnswers}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}

class FurloughPeriodExtractorSpec extends SpecBase with CoreTestDataBuilder with ScalaCheckPropertyChecks with Generators {

  "extractFurloughPeriod" must {

    "span furlough start to furlough end if furlough start and end is set" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 4, 1))
        .setValue(FurloughEndDatePage, LocalDate.of(2020, 5, 2))

      extractFurloughPeriod(userAnswers).value mustBe FurloughEnded(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2))
    }

    "be ongoing if furlough start is set" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 4, 1))

      extractFurloughPeriod(userAnswers).value mustBe FurloughOngoing(LocalDate.of(2020, 4, 1))
    }

    "return none if furlough start is not set" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughEndDatePage, LocalDate.of(2020, 5, 1))

      extractFurloughPeriod(userAnswers) mustBe None
    }

  }

  "extractFurloughWithinClaim" must {
    val policyStart: LocalDate = LocalDate.of(2020, 3, 1)
    val policyEnd: LocalDate = LocalDate.of(2020, 6, 30)

    "use claim period start if after furlough start" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 31))
    }

    "use claim period start if after furlough start (generated)" in new FurloughPeriodExtractor {
      val gen = for {
        furloughStart <- datesBetween(policyStart, policyEnd.minusDays(22))
        claimStart    <- datesBetween(furloughStart, policyEnd.minusDays(21))
        claimEnd      <- datesBetween(claimStart.plusDays(21), policyEnd)
      } yield (furloughStart, claimStart, claimEnd)

      forAll(gen) {
        case (furloughStart: LocalDate, claimStart: LocalDate, claimEnd: LocalDate) =>
          val userAnswers = UserAnswers("id")
            .setValue(FurloughStartDatePage, furloughStart)
            .setValue(ClaimPeriodStartPage, claimStart)
            .setValue(ClaimPeriodEndPage, claimEnd)

          extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(claimStart, claimEnd)
      }
    }

    "use furlough start if after claim period start" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 3))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 3), LocalDate.of(2020, 3, 31))
    }

    "use furlough start if after claim period start (generated)" in new FurloughPeriodExtractor {
      val gen = for {
        claimStart    <- datesBetween(policyStart, policyEnd.minusDays(22))
        furloughStart <- datesBetween(claimStart, policyEnd.minusDays(21))
        claimEnd      <- datesBetween(furloughStart.plusDays(21), policyEnd)
      } yield (claimStart, furloughStart, claimEnd)

      forAll(gen) {
        case (claimStart: LocalDate, furloughStart: LocalDate, claimEnd: LocalDate) =>
          val userAnswers = UserAnswers("id")
            .setValue(FurloughStartDatePage, furloughStart)
            .setValue(ClaimPeriodStartPage, claimStart)
            .setValue(ClaimPeriodEndPage, claimEnd)

          extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(furloughStart, claimEnd)
      }
    }

    "use claim period end if furlough end is missing" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 30))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 30))
    }

    "use claim period end if furlough end is missing (generated)" in new FurloughPeriodExtractor {
      val gen = for {
        furloughStart <- datesBetween(policyStart, policyEnd.minusDays(22))
        claimStart    <- datesBetween(furloughStart, policyEnd.minusDays(21))
        claimEnd      <- datesBetween(claimStart.plusDays(21), policyEnd)
      } yield (furloughStart, claimStart, claimEnd)

      forAll(gen) {
        case (furloughStart: LocalDate, claimStart: LocalDate, claimEnd: LocalDate) =>
          val userAnswers = UserAnswers("id")
            .setValue(FurloughStartDatePage, furloughStart)
            .setValue(ClaimPeriodStartPage, claimStart)
            .setValue(ClaimPeriodEndPage, claimEnd)

          extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(claimStart, claimEnd)
      }
    }

    "use claim period end if before furlough end" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(FurloughEndDatePage, LocalDate.of(2020, 3, 31))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 30))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 30))
    }

    "use claim period end if before furlough end (generated)" in new FurloughPeriodExtractor {
      val gen = for {
        furloughStart <- datesBetween(policyStart, policyEnd.minusDays(22))
        claimStart    <- datesBetween(furloughStart, policyEnd.minusDays(21))
        furloughEnd   <- datesBetween(claimStart.plusDays(21), policyEnd.plusDays(30))
        claimEnd      <- datesBetween(claimStart.plusDays(21), policyEnd) suchThat (_.isBefore(furloughEnd))
      } yield (furloughStart, furloughEnd, claimStart, claimEnd)

      forAll(gen) {
        case (furloughStart: LocalDate, furloughEnd: LocalDate, claimStart: LocalDate, claimEnd: LocalDate) =>
          val userAnswers = UserAnswers("id")
            .setValue(FurloughStartDatePage, furloughStart)
            .setValue(FurloughEndDatePage, furloughEnd)
            .setValue(ClaimPeriodStartPage, claimStart)
            .setValue(ClaimPeriodEndPage, claimEnd)

          extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(claimStart, claimEnd)
      }
    }

    "use furlough end if before claim period end" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(FurloughEndDatePage, LocalDate.of(2020, 3, 29))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 30))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 29))
    }

    "use furlough end if before claim period end (generated)" in new FurloughPeriodExtractor {
      val gen = for {
        furloughStart <- datesBetween(policyStart, policyEnd.minusDays(22))
        claimStart    <- datesBetween(furloughStart, policyEnd.minusDays(21))
        furloughEnd   <- datesBetween(claimStart.plusDays(21), policyEnd.minusDays(1))
        claimEnd      <- datesBetween(claimStart.plusDays(21), policyEnd) suchThat (_.isAfter(furloughEnd))
      } yield (furloughStart, furloughEnd, claimStart, claimEnd)

      forAll(gen) {
        case (furloughStart: LocalDate, furloughEnd: LocalDate, claimStart: LocalDate, claimEnd: LocalDate) =>
          val userAnswers = UserAnswers("id")
            .setValue(FurloughStartDatePage, furloughStart)
            .setValue(FurloughEndDatePage, furloughEnd)
            .setValue(ClaimPeriodStartPage, claimStart)
            .setValue(ClaimPeriodEndPage, claimEnd)

          extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(claimStart, furloughEnd)
      }
    }

    "return none" when {

      "claim period start is missing" in new FurloughPeriodExtractor {
        val userAnswers = UserAnswers("id")
          .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
          .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))

        extractFurloughWithinClaim(userAnswers) mustBe None
      }

      "claim period end is missing" in new FurloughPeriodExtractor {
        val userAnswers = UserAnswers("id")
          .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
          .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))

        extractFurloughWithinClaim(userAnswers) mustBe None
      }

      "furlough start is missing" in new FurloughPeriodExtractor {
        val userAnswers = UserAnswers("id")
          .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
          .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))

        extractFurloughWithinClaim(userAnswers) mustBe None
      }

    }

  }

}
