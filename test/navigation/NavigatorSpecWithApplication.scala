/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package navigation

import java.time.LocalDate

import base.SpecBaseWithApplication
import controllers.routes
import pages._
import models._

class NavigatorSpecWithApplication extends SpecBaseWithApplication {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.RootPageController.onPageLoad()
      }

      "loop around pay date if last pay date isn't claim end date or after" in {
        val userAnswers = UserAnswers("id")
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 29), Some(1))
          .get

        navigator.nextPage(PayDatePage, NormalMode, userAnswers, Some(1)) mustBe routes.PayDateController.onPageLoad(2)
      }

      "stop loop around pay date if last pay date is claim end date" in {
        val userAnswers = UserAnswers("id")
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 30), Some(1))
          .get

        navigator.nextPage(PayDatePage, NormalMode, userAnswers, Some(1)) mustBe routes.NicCategoryController.onPageLoad(NormalMode)
      }

      "stop loop around pay date if last pay date is after claim end date" in {
        val userAnswers = UserAnswers("id")
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 31), Some(1))
          .get

        navigator.nextPage(PayDatePage, NormalMode, userAnswers, Some(1)) mustBe routes.NicCategoryController.onPageLoad(NormalMode)
      }

      "go from furlough question" must {

        "to pay question when employee has been furloughed the whole period" in {
          val answers = emptyUserAnswers.set(FurloughQuestionPage, FurloughQuestion.Yes).success.value
          navigator.nextPage(FurloughQuestionPage, NormalMode, answers) mustBe routes.PaymentFrequencyController.onPageLoad(NormalMode)
        }

        "to furlough dates when employee has not been furloughed the whole period" in {
          val answers = emptyUserAnswers.set(FurloughQuestionPage, FurloughQuestion.No).success.value
          navigator.nextPage(FurloughQuestionPage, NormalMode, answers) mustBe routes.FurloughDatesController.onPageLoad(NormalMode)
        }

        "to furlough question when unanswered" in {
          val answers = emptyUserAnswers
          navigator.nextPage(FurloughQuestionPage, NormalMode, answers) mustBe routes.FurloughQuestionController.onPageLoad(NormalMode)
        }
      }

      "go from furlough dates" must {

        "to start date when started in claim" in {
          val answers = emptyUserAnswers.set(FurloughDatesPage, FurloughDates.StartedInClaim).success.value
          navigator.nextPage(FurloughDatesPage, NormalMode, answers) mustBe routes.FurloughStartDateController.onPageLoad(NormalMode)
        }

        "to end date when ended in claim" in {
          val answers = emptyUserAnswers.set(FurloughDatesPage, FurloughDates.EndedInClaim).success.value
          navigator.nextPage(FurloughDatesPage, NormalMode, answers) mustBe routes.FurloughEndDateController.onPageLoad(NormalMode)
        }

        "to start date when started and ended in claim" in {
          val answers = emptyUserAnswers.set(FurloughDatesPage, FurloughDates.StartedAndEndedInClaim).success.value
          navigator.nextPage(FurloughDatesPage, NormalMode, answers) mustBe routes.FurloughStartDateController.onPageLoad(NormalMode)
        }

        "to furlough question when unanswered" in {
          val answers = emptyUserAnswers
          navigator.nextPage(FurloughDatesPage, NormalMode, answers) mustBe routes.FurloughDatesController.onPageLoad(NormalMode)
        }
      }

      "go from furlough start date" must {

        "to end date when started and ended in claim" in {
          val answers = emptyUserAnswers.set(FurloughDatesPage, FurloughDates.StartedAndEndedInClaim).success.value
          navigator.nextPage(FurloughStartDatePage, NormalMode, answers) mustBe routes.FurloughEndDateController.onPageLoad(NormalMode)
        }

        "to pay question when started in claim" in {
          val answers = emptyUserAnswers.set(FurloughDatesPage, FurloughDates.StartedInClaim).success.value
          navigator.nextPage(FurloughStartDatePage, NormalMode, answers) mustBe routes.PayQuestionController.onPageLoad(NormalMode)
        }
      }

      "go from furlough end date" must {

        "to pay question" in {
          navigator.nextPage(FurloughEndDatePage, NormalMode, emptyUserAnswers) mustBe routes.PayQuestionController.onPageLoad(NormalMode)
        }
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from a page that doesn't exist in the edit route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController
          .onPageLoad()
      }
    }
  }
}
