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

import cats.data.Validated.{Invalid, Valid}
import config.{CalculatorVersionConfiguration, FrontendAppConfig}
import controllers.actions._
import handlers.{ConfirmationControllerRequestHandler, ErrorHandler}
import models.UserAnswers
import navigation.Navigator
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AuditService, EmployeeTypeService}
import utils.PagerDutyHelper.PagerDutyKeys._
import utils.{PagerDutyHelper, YearMonthHelper}
import viewmodels.{ConfirmationDataResultWithoutNicAndPension, PhaseOneConfirmationDataResult, PhaseTwoConfirmationDataResult}
import views.html._

import java.time.Month._
import java.time.YearMonth
import javax.inject.Inject
import scala.concurrent.ExecutionContext

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
    extends BaseController with ConfirmationControllerRequestHandler with CalculatorVersionConfiguration with YearMonthHelper {

  //scalastyle:off
  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    /** Uncomment line to create integration test cases when going through journeys, either manually or via test packs.
      * Set the number of cases to the amount of cases that will be executed. */
//    printOutConfirmationTestCases(request.userAnswers, loadResultData(request.userAnswers), 6)

    loadResultData(request.userAnswers) match {
      case Valid(data: PhaseOneConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        Ok(viewWithDetailedBreakdowns(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf))
      case Valid(data: PhaseTwoConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        Ok(phaseTwoView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf))
      case Valid(data: ConfirmationDataResultWithoutNicAndPension) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        data.metaData.claimPeriod.start.getYearMonth match {
          case yearMonth if yearMonth == AUGUST.inYear(y2020) =>
            Ok(noNicAndPensionView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf))
          case yearMonth if yearMonth == SEPTEMBER.inYear(y2020) || yearMonth == JULY.inYear(y2021) =>
            Ok(septemberConfirmationView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf))
          case yearMonth
              if yearMonth == OCTOBER.inYear(y2020) || yearMonth == AUGUST.inYear(y2021) || yearMonth == SEPTEMBER.inYear(y2021) =>
            Ok(octoberConfirmationView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf))
          case yearMonth if yearMonth.isBetweenInclusive(appConfig.extensionStartDate.getYearMonth, appConfig.schemeEndDate.getYearMonth) =>
            Ok(
              extensionView(
                data.confirmationViewBreakdown,
                data.metaData.claimPeriod,
                calculatorVersionConf,
                employeeTypeService.isType5NewStarter()
              ))
          case _ =>
            Redirect(routes.ErrorController.somethingWentWrong())
        }
      case Invalid(e) =>
        auditService.sendCalculationFailed(request.userAnswers)
        PagerDutyHelper.alert(CALCULATION_FAILED)
        UserAnswers.logErrors(e)(logger)
        Redirect(routes.ErrorController.somethingWentWrong())
    }
  }

}
