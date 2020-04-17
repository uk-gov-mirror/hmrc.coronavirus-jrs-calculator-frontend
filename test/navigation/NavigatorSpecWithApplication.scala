/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package navigation

import java.time.LocalDate

import base.SpecBaseWithApplication
import controllers.routes
import models._
import pages._

class NavigatorSpecWithApplication extends SpecBaseWithApplication {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from a page that doesn't exist in the route map" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.RootPageController.onPageLoad()
      }

      "go to ClaimPeriodEndPage after ClaimPeriodStartPage" in {
        navigator.nextPage(ClaimPeriodStartPage, NormalMode, UserAnswers("id")) mustBe routes.ClaimPeriodEndController
          .onPageLoad(NormalMode)
      }

      "go to FurloughQuestionPage after ClaimPeriodEndPage" in {
        navigator.nextPage(ClaimPeriodEndPage, NormalMode, UserAnswers("id")) mustBe routes.FurloughStartDateController
          .onPageLoad(NormalMode)
      }

      "go to correct page after FurloughQuestionPage" in {
        navigator.nextPage(
          FurloughQuestionPage,
          NormalMode,
          UserAnswers("id")
            .set(FurloughQuestionPage, FurloughQuestion.No)
            .success
            .value) mustBe routes.PaymentFrequencyController.onPageLoad(NormalMode)
        navigator.nextPage(
          FurloughQuestionPage,
          NormalMode,
          UserAnswers("id")
            .set(FurloughQuestionPage, FurloughQuestion.Yes)
            .success
            .value) mustBe routes.FurloughEndDateController.onPageLoad(NormalMode)
      }

      "go to PaymentFrequencyPage after FurloughEndDatePage" in {
        navigator.nextPage(FurloughEndDatePage, NormalMode, UserAnswers("id")) mustBe routes.PaymentFrequencyController
          .onPageLoad(NormalMode)
      }

      "go to correct page after SalaryQuestionPage" in {
        navigator.nextPage(
          PayQuestionPage,
          NormalMode,
          UserAnswers("id")
            .set(PayQuestionPage, PayQuestion.Regularly)
            .success
            .value) mustBe routes.SalaryQuestionController.onPageLoad(NormalMode)
        navigator.nextPage(
          PayQuestionPage,
          NormalMode,
          UserAnswers("id")
            .set(PayQuestionPage, PayQuestion.Varies)
            .success
            .value) mustBe routes.VariableLengthEmployedController.onPageLoad(NormalMode)
      }

      "go to SalaryQuestionPage after PaymentQuestionPage" in {
        navigator.nextPage(PaymentFrequencyPage, NormalMode, UserAnswers("id")) mustBe routes.PayQuestionController
          .onPageLoad(NormalMode)
      }

      "go to PayDatePage after SalaryQuestionPage" in {
        navigator.nextPage(SalaryQuestionPage, NormalMode, UserAnswers("id")) mustBe routes.PayDateController
          .onPageLoad(1)
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

        navigator.nextPage(PayDatePage, NormalMode, userAnswers, Some(1)) mustBe routes.NicCategoryController
          .onPageLoad(NormalMode)
      }

      "stop loop around pay date if last pay date is after claim end date" in {
        val userAnswers = UserAnswers("id")
          .set(ClaimPeriodEndPage, LocalDate.of(2020, 5, 30))
          .get
          .set(PayDatePage, LocalDate.of(2020, 5, 31), Some(1))
          .get

        navigator.nextPage(PayDatePage, NormalMode, userAnswers, Some(1)) mustBe routes.NicCategoryController
          .onPageLoad(NormalMode)
      }

      "go from furlough start date to furlough question" in {
        val answers = emptyUserAnswers
        navigator.nextPage(FurloughStartDatePage, NormalMode, answers) mustBe routes.FurloughQuestionController.onPageLoad(NormalMode)
      }

      "go from furlough end date" must {

        "to pay question" in {
          navigator.nextPage(FurloughEndDatePage, NormalMode, emptyUserAnswers) mustBe routes.PaymentFrequencyController
            .onPageLoad(NormalMode)
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
