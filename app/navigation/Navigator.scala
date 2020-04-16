/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package navigation

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
import models.PayQuestion.Regularly
import pages.{PayDatePage, _}
import models._

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case ClaimPeriodStartPage =>
      _ =>
        routes.ClaimPeriodEndController.onPageLoad(NormalMode)

    case ClaimPeriodEndPage =>
      _ =>
        routes.FurloughQuestionController.onPageLoad(NormalMode)
    case FurloughQuestionPage =>
      furloughQuestionRoutes
    case FurloughDatesPage =>
      furloughDatesRoutes
    case FurloughStartDatePage =>
      furloughStartDateRoutes
    case FurloughEndDatePage =>
      _ =>
        routes.PayQuestionController.onPageLoad(NormalMode)
    case PaymentFrequencyPage =>
      _ =>
        routes.PayQuestionController.onPageLoad(NormalMode)
    case PayQuestionPage =>
      payQuestionRoutes
    case SalaryQuestionPage =>
      _ =>
        routes.PayDateController.onPageLoad(1)
    case NicCategoryPage =>
      _ =>
        routes.PensionAutoEnrolmentController.onPageLoad(NormalMode)
    case PensionAutoEnrolmentPage =>
      _ =>
        routes.TaxYearPayDateController.onPageLoad(NormalMode)
    case TaxYearPayDatePage =>
      _ =>
        routes.ConfirmationController.onPageLoad()
    case _ =>
      _ =>
        routes.RootPageController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      _ =>
        routes.CheckYourAnswersController.onPageLoad()
  }
  private val payDateRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    (for {
      claimEndDate <- userAnswers.get(ClaimPeriodEndPage)
      lastPayDate  <- userAnswers.getList(PayDatePage).lastOption
    } yield {
      if (lastPayDate.isAfter(claimEndDate.minusDays(1))) {
        routes.NicCategoryController.onPageLoad(NormalMode)
      } else {
        routes.PayDateController.onPageLoad(previousIdx + 1)
      }
    }).getOrElse(routes.ErrorController.internalServerError())
  }
  private val idxRoutes: Page => (Int, UserAnswers) => Call = {
    case PayDatePage => payDateRoutes
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers, idx: Option[Int] = None): Call = mode match {
    case NormalMode =>
      idx.fold(normalRoutes(page)(userAnswers))(idx => idxRoutes(page)(idx, userAnswers))
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  private def furloughQuestionRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(FurloughQuestionPage) match {
      case Some(FurloughQuestion.Yes) => routes.PaymentFrequencyController.onPageLoad(NormalMode)
      case Some(FurloughQuestion.No)  => routes.FurloughDatesController.onPageLoad(NormalMode)
      case None                       => routes.FurloughQuestionController.onPageLoad(NormalMode)
    }
  }

  private def furloughDatesRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(FurloughDatesPage) match {
      case Some(FurloughDates.StartedInClaim)         => routes.FurloughStartDateController.onPageLoad(NormalMode)
      case Some(FurloughDates.EndedInClaim)           => routes.FurloughEndDateController.onPageLoad(NormalMode)
      case Some(FurloughDates.StartedAndEndedInClaim) => routes.FurloughStartDateController.onPageLoad(NormalMode)
      case None                                       => routes.FurloughDatesController.onPageLoad(NormalMode)
    }
  }

  private def furloughStartDateRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(FurloughDatesPage) match {
      case Some(FurloughDates.StartedAndEndedInClaim) => routes.FurloughEndDateController.onPageLoad(NormalMode)
      case _                                          => routes.PayQuestionController.onPageLoad(NormalMode)
    }
  }

  private def payQuestionRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(PayQuestionPage) match {
      case Some(Regularly) => routes.SalaryQuestionController.onPageLoad(NormalMode)
      case _               => routes.VariableLengthEmployedController.onPageLoad(NormalMode)
    }
  }

}
