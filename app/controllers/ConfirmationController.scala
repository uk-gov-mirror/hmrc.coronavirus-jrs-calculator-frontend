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

package controllers

import cats.data.Validated.Valid
import config.FrontendAppConfig
import controllers.actions._
import handlers.{ConfirmationControllerRequestHandler, ErrorHandler}
import javax.inject.Inject
import navigation.Navigator
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import viewmodels.{PhaseOneConfirmationDataResult, PhaseTwoConfirmationDataResult}
import views.html.{ConfirmationView, ConfirmationViewWithDetailedBreakdowns, PhaseTwoConfirmationView}

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmationView,
  viewWithDetailedBreakdowns: ConfirmationViewWithDetailedBreakdowns,
  phaseTwoView: PhaseTwoConfirmationView,
  auditService: AuditService,
  val navigator: Navigator
)(implicit val errorHandler: ErrorHandler, ec: ExecutionContext)
    extends BaseController with ConfirmationControllerRequestHandler {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    loadResultData(request.userAnswers) match {
      case Valid(data: PhaseOneConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)

        if (config.confirmationWithDetailedBreakdowns) {
          Future.successful(
            Ok(viewWithDetailedBreakdowns(data.confirmationViewBreakdown, data.metaData.claimPeriod, config.calculatorVersion)))
        } else {
          Future.successful(Ok(view(data.metaData, data.confirmationViewBreakdown, config.calculatorVersion)))
        }
      case Valid(data: PhaseTwoConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        Future.successful(Ok(phaseTwoView(data.confirmationViewBreakdown, data.metaData.claimPeriod, config.calculatorVersion)))
      case _ =>
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }
  }
}
