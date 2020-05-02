/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.{FurloughEnded, FurloughOngoing, FurloughWithinClaim, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}

class FurloughPeriodExtractorSpec extends SpecBase with CoreTestDataBuilder {

  "extractFurloughPeriod2" must {

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

    "use claim period start if after furlough start" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 31))
    }

    "use furlough start if after claim period start" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 3))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 31))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 3), LocalDate.of(2020, 3, 31))
    }

    "use claim period end if furlough end is missing" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 30))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 30))
    }

    "use claim period end if before furlough end" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(FurloughEndDatePage, LocalDate.of(2020, 3, 31))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 30))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 30))
    }

    "use furlough end if before claim period end" in new FurloughPeriodExtractor {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 3, 1))
        .setValue(FurloughEndDatePage, LocalDate.of(2020, 3, 29))
        .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 3, 2))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 3, 30))

      extractFurloughWithinClaim(userAnswers).value mustBe FurloughWithinClaim(LocalDate.of(2020, 3, 2), LocalDate.of(2020, 3, 29))
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
