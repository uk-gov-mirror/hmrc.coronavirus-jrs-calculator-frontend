/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package navigation

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import controllers.routes
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
      userAnswers =>
        {
          val fq = Option((userAnswers.data \ "furloughQuestion").as[String]).getOrElse("no")
          if (fq == "yes")
            routes.PayQuestionController.onPageLoad(NormalMode)
          else routes.RootPageController.onPageLoad()
        }
    case PayQuestionPage =>
      _ =>
        routes.PaymentFrequencyController.onPageLoad(NormalMode)
    case PaymentFrequencyPage =>
      _ =>
        routes.SalaryQuestionController.onPageLoad(NormalMode)
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
}
