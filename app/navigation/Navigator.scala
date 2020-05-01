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
import models.PayMethod.{Regular, Variable}
import models.{UserAnswers, _}
import pages.{PayDatePage, _}
import play.api.Logger
import play.api.mvc.Call
import services.PartialPayExtractor
import utils.LocalDateHelpers

@Singleton
class Navigator @Inject()(appConfig: FrontendAppConfig)
    extends LastYearPayControllerRequestHandler with LocalDateHelpers with PartialPayExtractor {

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
    case FurloughStatusPage =>
      furloughOngoingRoutes
    case FurloughStartDatePage =>
      furloughOngoingRoutes
    case FurloughEndDatePage =>
      _ =>
        routes.PaymentFrequencyController.onPageLoad()
    case PaymentFrequencyPage =>
      _ =>
        routes.PayMethodController.onPageLoad()
    case PayMethodPage =>
      payMethodRoutes
    case SalaryQuestionPage =>
      _ =>
        routes.PayDateController.onPageLoad(1)
    case EmployedStartedPage =>
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
    case PensionStatusPage =>
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

  def routeFor(page: Page): Call =
    page match {
      case FurloughStartDatePage => routes.FurloughStartDateController.onPageLoad()
      case p =>
        Logger.warn(s"can't find the route for the page: $p")
        routes.ErrorController.internalServerError()
    }

  private def partialPayAfterFurloughRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(EmployedStartedPage) match {
      case Some(EmployeeStarted.OnOrBefore1Feb2019) => routes.LastYearPayController.onPageLoad(1)
      case Some(EmployeeStarted.After1Feb2019) =>
        userAnswers.get(EmployeeStartDatePage) match {
          case Some(date) if date.isBefore(apr6th2019) => routes.LastYearPayController.onPageLoad(1)
          case _                                       => routes.NicCategoryController.onPageLoad()
        }
      case None => routes.NicCategoryController.onPageLoad()
    }
  }

  private def furloughOngoingRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(FurloughStatusPage) match {
      case Some(FurloughStatus.FurloughEnded)   => routes.FurloughEndDateController.onPageLoad()
      case Some(FurloughStatus.FurloughOngoing) => routes.PaymentFrequencyController.onPageLoad()
      case None                                 => routes.FurloughOngoingController.onPageLoad()
    }
  }

  private def payMethodRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(PayMethodPage) match {
      case Some(Regular)  => routes.SalaryQuestionController.onPageLoad()
      case Some(Variable) => routes.VariableLengthEmployedController.onPageLoad()
      case None           => routes.PayMethodController.onPageLoad()
    }
  }

  private def variableLengthEmployedRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.get(EmployedStartedPage) match {
      case Some(EmployeeStarted.OnOrBefore1Feb2019) => routes.VariableGrossPayController.onPageLoad()
      case Some(EmployeeStarted.After1Feb2019)      => routes.EmployeeStartDateController.onPageLoad()
      case _                                        => routes.VariableLengthEmployedController.onPageLoad()
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
    userAnswers.get(PayMethodPage) match {
      case Some(Regular) => routes.NicCategoryController.onPageLoad()
      case Some(Variable) =>
        if (hasPartialPayBefore(userAnswers)) {
          routes.PartialPayBeforeFurloughController.onPageLoad()
        } else if (hasPartialPayAfter(userAnswers)) {
          routes.PartialPayAfterFurloughController.onPageLoad()
        } else {
          userAnswers.get(EmployedStartedPage) match {
            case Some(EmployeeStarted.OnOrBefore1Feb2019) =>
              routes.LastYearPayController.onPageLoad(1)
            case _ =>
              userAnswers.get(EmployeeStartDatePage) match {
                case Some(date) if date.isBefore(apr6th2019) => routes.LastYearPayController.onPageLoad(1)
                case _                                       => routes.NicCategoryController.onPageLoad()
              }
          }
        }
      case None => routes.PayMethodController.onPageLoad()
    }
  }

}
