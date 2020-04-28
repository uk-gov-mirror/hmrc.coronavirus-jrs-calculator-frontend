/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package navigation

import java.time.LocalDate

import config.FrontendAppConfig
import controllers.routes
import handlers.LastYearPayControllerRequestHandler
import javax.inject.{Inject, Singleton}
import models.PayQuestion.{Regularly, Varies}
import models.{UserAnswers, _}
import pages.{PayDatePage, _}
import play.api.mvc.Call
import services.PartialPayHelper
import utils.LocalDateHelpers

@Singleton
class Navigator @Inject()(appConfig: FrontendAppConfig)
    extends LastYearPayControllerRequestHandler with LocalDateHelpers with PartialPayHelper {

  val apr7th2019 = LocalDate.of(2019, 4, 7)
  val apr6th2019 = LocalDate.of(2019, 4, 6)
  val apr5th2019 = LocalDate.of(2019, 4, 5)

  private val normalRoutes: Page => UserAnswers => Call = {
    case ClaimPeriodStartPage =>
      _ =>
        routes.ClaimPeriodEndController.onPageLoad()

    case ClaimPeriodEndPage =>
      _ =>
        routes.FurloughStartDateController.onPageLoad()
    case FurloughOngoingPage =>
      furloughOngoingRoutes
    case FurloughStartDatePage =>
      furloughOngoingRoutes
    case FurloughEndDatePage =>
      _ =>
        routes.PaymentFrequencyController.onPageLoad()
    case PaymentFrequencyPage =>
      _ =>
        routes.PayQuestionController.onPageLoad()
    case PayQuestionPage =>
      payQuestionRoutes
    case SalaryQuestionPage =>
      _ =>
        routes.PayDateController.onPageLoad(1)
    case VariableLengthEmployedPage =>
      variableLengthEmployedRoutes
    case EmployeeStartDatePage =>
      _ =>
        routes.VariableGrossPayController.onPageLoad()
    case PartialPayBeforeFurloughPage =>
      partialPayBeforeFurloughRoutes
    case PartialPayAfterFurloughPage =>
      partialPayAfterFurloughRoutes
    case VariableGrossPayPage =>
      _ =>
        routes.PayDateController.onPageLoad(1)
    case LastPayDatePage =>
      lastPayDateRoutes
    case NicCategoryPage =>
      _ =>
        routes.PensionContributionController.onPageLoad()
    case PensionContributionPage =>
      _ =>
        routes.FurloughCalculationsController.onPageLoad()
    case FurloughCalculationsPage =>
      furloughCalculationsRoutes
    case _ =>
      _ =>
        routes.RootPageController.onPageLoad()
  }

  private val payDateRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    (for {
      claimEndDate <- userAnswers.get(ClaimPeriodEndPage)
      lastPayDate  <- userAnswers.getList(PayDatePage).lastOption
    } yield {
      val endDate = userAnswers
        .get(FurloughEndDatePage)
        .fold(
          claimEndDate
        ) { furloughEndDate =>
          earliestOf(claimEndDate, furloughEndDate)
        }

      if (lastPayDate.isAfter(endDate.minusDays(1))) {
        routes.LastPayDateController.onPageLoad()
      } else {
        routes.PayDateController.onPageLoad(previousIdx + 1)
      }
    }).getOrElse(routes.ErrorController.internalServerError())
  }

  private val lastYearPayRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    getPayDates(userAnswers).fold(
      routes.ErrorController.somethingWentWrong()
    ) { payDates =>
      payDates.lift.apply(previousIdx) match {
        case Some(_) => routes.LastYearPayController.onPageLoad(previousIdx + 1)
        case None    => routes.NicCategoryController.onPageLoad()
      }
    }
  }

  private val idxRoutes: Page => (Int, UserAnswers) => Call = {
    case PayDatePage     => payDateRoutes
    case LastYearPayPage => lastYearPayRoutes
  }

  private def partialPayBeforeFurloughRoutes: UserAnswers => Call = { userAnswers =>
    if (hasPartialPayAfter(userAnswers)) {
      routes.PartialPayAfterFurloughController.onPageLoad()
    } else {
      nextPage(PartialPayAfterFurloughPage, userAnswers)
    }
  }

  def nextPage(page: Page, userAnswers: UserAnswers, idx: Option[Int] = None): Call =
    idx.fold(normalRoutes(page)(userAnswers))(idx => idxRoutes(page)(idx, userAnswers))

  private def partialPayAfterFurloughRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(VariableLengthEmployedPage) match {
      case Some(VariableLengthEmployed.Yes) => routes.LastYearPayController.onPageLoad(1)
      case Some(VariableLengthEmployed.No) =>
        userAnswers.get(EmployeeStartDatePage) match {
          case Some(date) if date.isBefore(apr6th2019) => routes.LastYearPayController.onPageLoad(1)
          case _                                       => routes.NicCategoryController.onPageLoad()
        }
      case None => routes.NicCategoryController.onPageLoad()
    }
  }

  private def furloughOngoingRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(FurloughOngoingPage) match {
      case Some(FurloughOngoing.Yes) => routes.FurloughEndDateController.onPageLoad()
      case Some(FurloughOngoing.No)  => routes.PaymentFrequencyController.onPageLoad()
      case None                      => routes.FurloughOngoingController.onPageLoad()
    }
  }

  private def payQuestionRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(PayQuestionPage) match {
      case Some(Regularly) => routes.SalaryQuestionController.onPageLoad()
      case Some(Varies)    => routes.VariableLengthEmployedController.onPageLoad()
      case None            => routes.PayQuestionController.onPageLoad()
    }
  }

  private def variableLengthEmployedRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(VariableLengthEmployedPage) match {
      case Some(VariableLengthEmployed.Yes) => routes.VariableGrossPayController.onPageLoad()
      case Some(VariableLengthEmployed.No)  => routes.EmployeeStartDateController.onPageLoad()
      case _                                => routes.VariableLengthEmployedController.onPageLoad()
    }
  }

  private def furloughCalculationsRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(FurloughCalculationsPage) match {
      case Some(FurloughCalculations.Yes) => routes.ComingSoonController.onPageLoad(true)
      case Some(FurloughCalculations.No)  => routes.ConfirmationController.onPageLoad()
      case _                              => routes.FurloughCalculationsController.onPageLoad()
    }
  }

  private def lastPayDateRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(PayQuestionPage) match {
      case Some(Regularly) => routes.NicCategoryController.onPageLoad()
      case Some(Varies) =>
        if (hasPartialPayBefore(userAnswers)) {
          routes.PartialPayBeforeFurloughController.onPageLoad()
        } else if (hasPartialPayAfter(userAnswers)) {
          routes.PartialPayAfterFurloughController.onPageLoad()
        } else {
          userAnswers.get(VariableLengthEmployedPage) match {
            case Some(VariableLengthEmployed.Yes) =>
              routes.LastYearPayController.onPageLoad(1)
            case _ =>
              userAnswers.get(EmployeeStartDatePage) match {
                case Some(date) if date.isBefore(apr6th2019) => routes.LastYearPayController.onPageLoad(1)
                case _                                       => routes.NicCategoryController.onPageLoad()
              }
          }
        }
      case None => routes.PayQuestionController.onPageLoad()
    }
  }

}
