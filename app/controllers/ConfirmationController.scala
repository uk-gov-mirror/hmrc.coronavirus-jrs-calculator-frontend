/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import java.time.LocalDate

import cats.data.{NonEmptyChain, Validated}
import cats.data.Validated.{Invalid, Valid}
import config.{CalculatorVersionConfiguration, FrontendAppConfig}
import controllers.actions._
import handlers.{ConfirmationControllerRequestHandler, ErrorHandler}
import javax.inject.Inject
import models.EmployeeRTISubmission._
import models.{AnswerValidation, EmployeeRTISubmission, Period, UserAnswers}
import models.UserAnswers.AnswerV
import models.requests.DataRequest
import navigation.Navigator
import pages.{EmployeeRTISubmissionPage, EmployeeStartDatePage, PreviousFurloughPeriodsPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{AuditService, EmployeeTypeService}
import utils.ConfirmationTestCasesUtil.printOutConfirmationTestCases
import utils.PagerDutyHelper
import utils.PagerDutyHelper.PagerDutyKeys._
import viewmodels.{ConfirmationDataResult, ConfirmationDataResultWithoutNicAndPension, ConfirmationViewBreakdownWithoutNicAndPension, PhaseOneConfirmationDataResult, PhaseTwoConfirmationDataResult, ViewBreakdown}
import views.html._

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  employeeTypeService: EmployeeTypeService,
  viewWithDetailedBreakdowns: ConfirmationViewWithDetailedBreakdowns,
  phaseTwoView: PhaseTwoConfirmationView,
  noNicAndPensionView: NoNicAndPensionConfirmationView,
  septemberConfirmationView: SeptemberConfirmationView,
  octoberConfirmationView: OctoberConfirmationView,
  extensionView: JrsExtensionConfirmationView,
  auditService: AuditService,
  val navigator: Navigator)(implicit val errorHandler: ErrorHandler, ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends BaseController with ConfirmationControllerRequestHandler with CalculatorVersionConfiguration {

  //scalastyle:off
  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    /** Uncomment line to create integration test cases when going through journeys, either manually or via test packs.
      * Set the number of cases to the amount of cases that will be executed. */
    //    printOutConfirmationTestCases(request.userAnswers, loadResultData(request.userAnswers), 3)

    loadResultData(request.userAnswers) match {
      case Valid(data: PhaseOneConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        Future.successful(Ok(viewWithDetailedBreakdowns(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
      case Valid(data: PhaseTwoConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        Future.successful(Ok(phaseTwoView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
      case Valid(data: ConfirmationDataResultWithoutNicAndPension) =>
        data.metaData.claimPeriod.start.getMonthValue match {
          case 8 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(Ok(noNicAndPensionView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
          case 9 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(
              Ok(septemberConfirmationView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
          case 10 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(Ok(octoberConfirmationView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
          case 11 | 12 | 1 | 2 | 3 | 4 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(
              Ok(
                extensionView(
                  data.confirmationViewBreakdown,
                  data.metaData.claimPeriod,
                  calculatorVersionConf,
                  employeeTypeService.isType5NewStarter()
                ))
            )
          case _ => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        }
      case Invalid(e) =>
        auditService.sendCalculationFailed(request.userAnswers)
        PagerDutyHelper.alert(CALCULATION_FAILED)
        UserAnswers.logErrors(e)(logger)
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }
  }

}
