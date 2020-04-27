/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import java.time.LocalDate

import base.{CoreDataBuilder, SpecBase}
import models.{Period, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}

class FurloughPeriodHelperSpec extends SpecBase with CoreDataBuilder {

  "extractFurloughPeriod" must {

    "span furlough start to claim period end if furlough end is not set" in new FurloughPeriodHelper {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 4, 1))
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 5, 1))

      extractFurloughPeriod(userAnswers).value mustBe Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 1))
    }

    "span furlough start to furlough end if furlough end is set" when {

      "claim period end is before furlough end" in {
        new FurloughPeriodHelper {
          val userAnswers = UserAnswers("id")
            .setValue(FurloughStartDatePage, LocalDate.of(2020, 4, 1))
            .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 5, 1))
            .setValue(FurloughEndDatePage, LocalDate.of(2020, 5, 2))

          extractFurloughPeriod(userAnswers).value mustBe Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 2))
        }
      }

      "claim period end is after furlough end" in {
        new FurloughPeriodHelper {
          val userAnswers = UserAnswers("id")
            .setValue(FurloughStartDatePage, LocalDate.of(2020, 4, 1))
            .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 5, 2))
            .setValue(FurloughEndDatePage, LocalDate.of(2020, 5, 1))

          extractFurloughPeriod(userAnswers).value mustBe Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 5, 1))
        }
      }

    }

    "return none if furlough start is not set" in new FurloughPeriodHelper {
      val userAnswers = UserAnswers("id")
        .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 5, 1))

      extractFurloughPeriod(userAnswers) mustBe None
    }

    "return none if claim end is not set" in new FurloughPeriodHelper {
      val userAnswers = UserAnswers("id")
        .setValue(FurloughStartDatePage, LocalDate.of(2020, 5, 1))

      extractFurloughPeriod(userAnswers) mustBe None
    }

  }

  "extractRelevantFurloughPeriod" must {

    "use furlough start as start date when it is later than claim start" in new FurloughPeriodHelper {
      val furloughStart = LocalDate.of(2020, 4, 2)
      val furloughEnd = None
      val claimStart = LocalDate.of(2020, 4, 1)
      val claimEnd = LocalDate.of(2020, 5, 1)

      extractRelevantFurloughPeriod(furloughStart, furloughEnd, claimStart, claimEnd) mustBe Period(furloughStart, claimEnd)
    }

    "use claim start as start date when it is later than furlough start" in new FurloughPeriodHelper {
      val furloughStart = LocalDate.of(2020, 4, 1)
      val furloughEnd = None
      val claimStart = LocalDate.of(2020, 4, 2)
      val claimEnd = LocalDate.of(2020, 5, 1)

      extractRelevantFurloughPeriod(furloughStart, furloughEnd, claimStart, claimEnd) mustBe Period(claimStart, claimEnd)
    }

    "use claim end as end date when furlough end is not set" in new FurloughPeriodHelper {
      val furloughStart = LocalDate.of(2020, 4, 2)
      val furloughEnd = None
      val claimStart = LocalDate.of(2020, 4, 1)
      val claimEnd = LocalDate.of(2020, 5, 1)

      extractRelevantFurloughPeriod(furloughStart, furloughEnd, claimStart, claimEnd) mustBe Period(furloughStart, claimEnd)
    }

    "use furlough end as end date when furlough end is set" in new FurloughPeriodHelper {
      val furloughStart = LocalDate.of(2020, 4, 2)
      val furloughEnd = LocalDate.of(2020, 5, 2)
      val claimStart = LocalDate.of(2020, 4, 1)
      val claimEnd = LocalDate.of(2020, 5, 1)

      extractRelevantFurloughPeriod(furloughStart, Some(furloughEnd), claimStart, claimEnd) mustBe Period(furloughStart, furloughEnd)
    }

    "pull values from user answers" when {

      "furlough end is not set" in new FurloughPeriodHelper {
        val userAnswers = UserAnswers("id")
          .setValue(FurloughStartDatePage, LocalDate.of(2020, 4, 2))
          .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 4, 1))
          .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 5, 1))

        extractFurloughPeriod(userAnswers).value mustBe Period(LocalDate.of(2020, 4, 2), LocalDate.of(2020, 5, 1))
      }

      "furlough end is set" in new FurloughPeriodHelper {
        val userAnswers = UserAnswers("id")
          .setValue(FurloughStartDatePage, LocalDate.of(2020, 4, 2))
          .setValue(FurloughEndDatePage, LocalDate.of(2020, 4, 25))
          .setValue(ClaimPeriodStartPage, LocalDate.of(2020, 4, 1))
          .setValue(ClaimPeriodEndPage, LocalDate.of(2020, 5, 1))

        extractFurloughPeriod(userAnswers).value mustBe Period(LocalDate.of(2020, 4, 2), LocalDate.of(2020, 4, 25))
      }
    }

  }

}
