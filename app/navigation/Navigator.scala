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

package navigation

import java.time.LocalDate

import cats.data.Validated.{Invalid, Valid}
import config.SchemeConfiguration
import controllers.routes
import handlers.LastYearPayControllerRequestHandler
import javax.inject.Singleton
import models.PartTimeQuestion.{PartTimeNo, PartTimeYes}
import models.PayMethod.{Regular, Variable}
import models.{UserAnswers, _}
import org.slf4j
import org.slf4j.LoggerFactory
import pages.{PayDatePage, _}
import play.api.Logger
import play.api.mvc.Call
import services.PartialPayExtractor
import utils.LocalDateHelpers

@Singleton
class Navigator extends LastYearPayControllerRequestHandler with LocalDateHelpers with PartialPayExtractor with SchemeConfiguration {

  implicit val logger: slf4j.Logger = LoggerFactory.getLogger(getClass)

  final val apr6th2019: LocalDate = LocalDate.of(2019, 4, 6)

  private[this] val normalRoutes: Page => UserAnswers => Call = {
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
    case PayPeriodsListPage =>
      payPeriodsListRoute
    case RegularPayAmountPage =>
      regularPayAmountRoute
    case EmployeeStartedPage =>
      variableLengthEmployedRoutes
    case PartialPayBeforeFurloughPage =>
      partialPayBeforeFurloughRoutes
    case PartialPayAfterFurloughPage =>
      _ =>
        routes.TopUpStatusController.onPageLoad()
    case AnnualPayAmountPage =>
      annualPayAmountRoutes
    case LastPayDatePage =>
      lastPayDateRoutes
    case NicCategoryPage =>
      _ =>
        routes.PensionContributionController.onPageLoad()
    case PensionStatusPage =>
      _ =>
        routes.ConfirmationController.onPageLoad()
    case TopUpStatusPage =>
      topUpStatusRoutes
    case TopUpPeriodsPage =>
      _ =>
        routes.TopUpAmountController.onPageLoad(1)
    case AdditionalPaymentStatusPage =>
      additionalPaymentStatusRoutes
    case AdditionalPaymentPeriodsPage =>
      _ =>
        routes.AdditionalPaymentAmountController.onPageLoad(1)

    case FurloughPeriodQuestionPage =>
      furloughPeriodQuestionRoutes

    case ClaimPeriodQuestionPage =>
      claimPeriodQuestionRoutes
    case EmployeeStartDatePage => employeeStartDateRoutes
    case PayPeriodQuestionPage =>
      payPeriodQuestionRoutes
    case PartTimeQuestionPage => partTimeQuestionRoute
    case PartTimePeriodsPage =>
      _ =>
        routes.PartTimeNormalHoursController.onPageLoad(1)
    case _ =>
      _ =>
        routes.RootPageController.onPageLoad()
  }

  private[this] def regularPayAmountRoute: UserAnswers => Call = { userAnswer =>
    if (isPhaseTwo(userAnswer)) {
      routes.PartTimeQuestionController.onPageLoad()
    } else {
      routes.TopUpStatusController.onPageLoad()
    }
  }

  private[this] def partTimeQuestionRoute: UserAnswers => Call =
    userAnswer =>
      (userAnswer.getV(PartTimeQuestionPage), userAnswer.getV(ClaimPeriodStartPage)) match {
        case (Valid(PartTimeYes), _)           => routes.PartTimePeriodsController.onPageLoad()
        case (Valid(PartTimeNo), Valid(start)) => skipNicAndPensionAfterJuly(start)
        case _                                 => routes.PartTimePeriodsController.onPageLoad()
    }

  private[this] val payDateRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    (for {
      claimEndDate <- userAnswers.getV(ClaimPeriodEndPage).toOption
      lastPayDate  <- userAnswers.getList(PayDatePage).lastOption
    } yield {
      val endDate = userAnswers
        .getV(FurloughEndDatePage)
        .fold(nel => {
          UserAnswers.logWarnings(nel)
          claimEndDate
        }, { furloughEndDate =>
          earliestOf(claimEndDate, furloughEndDate)
        })

      if (lastPayDate.isAfter(endDate.minusDays(1))) {
        routes.PayPeriodsListController.onPageLoad()
      } else {
        routes.PayDateController.onPageLoad(previousIdx + 1)
      }
    }).getOrElse(routes.ErrorController.internalServerError())
  }

  private[this] val lastYearPayRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    getLastYearPeriods(userAnswers).fold(
      nel => {
        UserAnswers.logErrors(nel)
        routes.ErrorController.somethingWentWrong()
      }, { periods =>
        periods.lift.apply(previousIdx) match {
          case Some(_) => routes.LastYearPayController.onPageLoad(previousIdx + 1)
          case None    => routes.AnnualPayAmountController.onPageLoad()
        }
      }
    )
  }

  private[this] val topUpAmountRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    userAnswers
      .getV(TopUpPeriodsPage)
      .map { topUpPeriods =>
        if (topUpPeriods.isDefinedAt(previousIdx)) {
          routes.TopUpAmountController.onPageLoad(previousIdx + 1)
        } else {
          routes.AdditionalPaymentStatusController.onPageLoad()
        }
      }
      .getOrElse(routes.TopUpPeriodsController.onPageLoad())
  }

  private[this] val partTimeHoursRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    (userAnswers.getV(PartTimePeriodsPage), userAnswers.getV(ClaimPeriodStartPage)) match {
      case (Valid(periods), _) if periods.isDefinedAt(previousIdx) => routes.PartTimeNormalHoursController.onPageLoad(previousIdx + 1)
      case (Valid(_), Valid(start)) =>
        skipNicAndPensionAfterJuly(start)
      case _ => routes.PartTimePeriodsController.onPageLoad()
    }
  }

  private def skipNicAndPensionAfterJuly(start: LocalDate): Call =
    if (start.getMonthValue > 7) routes.ConfirmationController.onPageLoad() else routes.NicCategoryController.onPageLoad()

  private[this] val partTimeNormalHoursRoutes: (Int, UserAnswers) => Call = { (previousIdx, userAnswers) =>
    userAnswers
      .getV(PartTimePeriodsPage)
      .map { _ =>
        routes.PartTimeHoursController.onPageLoad(previousIdx)
      }
      .getOrElse(routes.PartTimePeriodsController.onPageLoad())
  }

  private[this] val additionalPaymentAmountRoutes: (Int, UserAnswers) => Call = (previousIdx, userAnswers) =>
    (userAnswers.getV(AdditionalPaymentPeriodsPage), userAnswers.getV(ClaimPeriodStartPage)) match {
      case (Valid(additionalPaymentPeriods), _) if additionalPaymentPeriods.isDefinedAt(previousIdx) =>
        routes.AdditionalPaymentAmountController.onPageLoad(previousIdx + 1)
      case (Valid(_), Valid(start)) => skipNicAndPensionAfterJuly(start)
      case _                        => routes.AdditionalPaymentPeriodsController.onPageLoad()
  }

  private val idxRoutes: Page => (Int, UserAnswers) => Call = {
    case PayDatePage                 => payDateRoutes
    case LastYearPayPage             => lastYearPayRoutes
    case TopUpAmountPage             => topUpAmountRoutes
    case PartTimeHoursPage           => partTimeHoursRoutes
    case PartTimeNormalHoursPage     => partTimeNormalHoursRoutes
    case AdditionalPaymentAmountPage => additionalPaymentAmountRoutes
    case _ =>
      (_, _) =>
        routes.RootPageController.onPageLoad()
  }

  def routeFor(page: Page): Call =
    page match {
      case FurloughStartDatePage => routes.FurloughStartDateController.onPageLoad()
      case TopUpPeriodsPage      => routes.TopUpPeriodsController.onPageLoad()
      case p =>
        Logger.warn(s"can't find the route for the page: $p")
        routes.ErrorController.internalServerError()
    }

  private[this] def partialPayBeforeFurloughRoutes: UserAnswers => Call = { userAnswers =>
    if (hasPartialPayAfter(userAnswers)) {
      routes.PartialPayAfterFurloughController.onPageLoad()
    } else {
      nextPage(PartialPayAfterFurloughPage, userAnswers)
    }
  }

  def nextPage(page: Page, userAnswers: UserAnswers, idx: Option[Int] = None): Call =
    idx.fold(normalRoutes(page)(userAnswers))(idx => idxRoutes(page)(idx, userAnswers))

  private[this] def furloughOngoingRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.getV(FurloughStatusPage) match {
      case Valid(FurloughStatus.FurloughEnded)   => routes.FurloughEndDateController.onPageLoad()
      case Valid(FurloughStatus.FurloughOngoing) => routes.PaymentFrequencyController.onPageLoad()
      case Invalid(err) =>
        UserAnswers.logWarnings(err)
        routes.FurloughOngoingController.onPageLoad()
    }
  }

  private[this] def employeeStartDateRoutes: UserAnswers => Call = { userAnswers =>
    if (userAnswers.getList(PayDatePage).isEmpty) {
      routes.PayDateController.onPageLoad(1)
    } else {
      routes.AnnualPayAmountController.onPageLoad()
    }
  }

  private[this] def payMethodRoutes: UserAnswers => Call = { userAnswers =>
    (userAnswers.getV(PayMethodPage), userAnswers.getList(PayDatePage)) match {
      case (Valid(Regular), dates) if dates.isEmpty => routes.PayDateController.onPageLoad(1)
      case (Valid(Regular), _)                      => routes.RegularPayAmountController.onPageLoad()
      case (Valid(Variable), _)                     => routes.VariableLengthEmployedController.onPageLoad()
      case (Invalid(errors), _)                     => routes.PayMethodController.onPageLoad()
    }
  }

  private[this] def variableLengthEmployedRoutes: UserAnswers => Call = { userAnswers =>
    (userAnswers.getV(EmployeeStartedPage), userAnswers.getList(PayDatePage)) match {
      case (Valid(EmployeeStarted.OnOrBefore1Feb2019), dates) if dates.isEmpty => routes.PayDateController.onPageLoad(1)
      case (Valid(EmployeeStarted.OnOrBefore1Feb2019), _)                      => routes.LastYearPayController.onPageLoad(1)
      case (Valid(EmployeeStarted.After1Feb2019), _)                           => routes.EmployeeStartDateController.onPageLoad()
      case _                                                                   => routes.VariableLengthEmployedController.onPageLoad()
    }
  }

  private[this] def topUpStatusRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.getV(TopUpStatusPage) match {
      case Valid(TopUpStatus.ToppedUp)    => routes.TopUpPeriodsController.onPageLoad()
      case Valid(TopUpStatus.NotToppedUp) => routes.AdditionalPaymentStatusController.onPageLoad()
      case _                              => routes.TopUpStatusController.onPageLoad()
    }
  }

  private[this] def additionalPaymentStatusRoutes: UserAnswers => Call = { userAnswers =>
    (userAnswers.getV(AdditionalPaymentStatusPage), userAnswers.getV(ClaimPeriodStartPage)) match {
      case (Valid(AdditionalPaymentStatus.YesAdditionalPayments), _)           => routes.AdditionalPaymentPeriodsController.onPageLoad()
      case (Valid(AdditionalPaymentStatus.NoAdditionalPayments), Valid(start)) => skipNicAndPensionAfterJuly(start)
      case _                                                                   => routes.AdditionalPaymentStatusController.onPageLoad()
    }
  }

  private[this] def payPeriodsListRoute: UserAnswers => Call = { userAnswers =>
    userAnswers.getV(PayPeriodsListPage) match {
      case Valid(PayPeriodsList.Yes) => routes.LastPayDateController.onPageLoad()
      case Valid(PayPeriodsList.No)  => routes.PayDateController.onPageLoad(1)
      case Invalid(_)                => routes.PayDateController.onPageLoad(1)
    }
  }

  private[this] def lastPayDateRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.getV(PayMethodPage) match {
      case Valid(Regular) => routes.RegularPayAmountController.onPageLoad()
      case Valid(Variable) =>
        userAnswers.getV(EmployeeStartedPage) match {
          case Valid(EmployeeStarted.OnOrBefore1Feb2019) =>
            routes.LastYearPayController.onPageLoad(1)
          case Valid(EmployeeStarted.After1Feb2019) =>
            userAnswers.getV(EmployeeStartDatePage) match {
              case Valid(date) if date.isBefore(dynamicCylbCutoff(userAnswers)) => routes.LastYearPayController.onPageLoad(1)
              case Valid(_)                                                     => routes.AnnualPayAmountController.onPageLoad()
              case Invalid(err) =>
                UserAnswers.logErrors(err)
                routes.EmployeeStartDateController.onPageLoad()
            }
          case Invalid(err) =>
            UserAnswers.logErrors(err)
            routes.VariableLengthEmployedController.onPageLoad()
        }
      case Invalid(err) =>
        UserAnswers.logErrors(err)
        routes.PayMethodController.onPageLoad()
    }
  }

  private def annualPayAmountRoutes: UserAnswers => Call =
    userAnswers =>
      if (isPhaseTwo(userAnswers)) {
        routes.PartTimeQuestionController.onPageLoad()
      } else {
        phaseOneAnnualPayAmountRoute(userAnswers)
    }

  private def phaseOneAnnualPayAmountRoute(userAnswers: UserAnswers): Call =
    if (hasPartialPayBefore(userAnswers)) {
      routes.PartialPayBeforeFurloughController.onPageLoad()
    } else if (hasPartialPayAfter(userAnswers)) {
      routes.PartialPayAfterFurloughController.onPageLoad()
    } else {
      routes.TopUpStatusController.onPageLoad()
    }

  private def furloughPeriodQuestionRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.getV(FurloughPeriodQuestionPage) match {
      case Valid(FurloughPeriodQuestion.FurloughedOnSamePeriod)      => routes.PayPeriodQuestionController.onPageLoad()
      case Valid(FurloughPeriodQuestion.FurloughedOnDifferentPeriod) => routes.FurloughStartDateController.onPageLoad()
      case Invalid(e) =>
        UserAnswers.logErrors(e)
        routes.FurloughPeriodQuestionController.onPageLoad()
    }
  }

  private def claimPeriodQuestionRoutes: UserAnswers => Call =
    userAnswers =>
      userAnswers
        .getV(ClaimPeriodQuestionPage)
        .fold(
          nel => {
            UserAnswers.logErrors(nel)
            routes.ClaimPeriodQuestionController.onPageLoad()
          }, {
            case ClaimPeriodQuestion.ClaimOnSamePeriod      => routes.FurloughPeriodQuestionController.onPageLoad()
            case ClaimPeriodQuestion.ClaimOnDifferentPeriod => routes.ClaimPeriodStartController.onPageLoad()
          }
      )

  private def payPeriodQuestionRoutes: UserAnswers => Call = { userAnswers =>
    userAnswers.getV(PayPeriodQuestionPage) match {
      case Valid(PayPeriodQuestion.UseSamePayPeriod)      => routes.PayMethodController.onPageLoad()
      case Valid(PayPeriodQuestion.UseDifferentPayPeriod) => routes.PaymentFrequencyController.onPageLoad()
      case Invalid(e)                                     => routes.PayPeriodQuestionController.onPageLoad()
    }
  }

  private def isPhaseTwo: UserAnswers => Boolean =
    userAnswer => userAnswer.getV(ClaimPeriodStartPage).exists(!_.isBefore(phaseTwoStartDate))
}
