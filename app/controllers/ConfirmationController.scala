/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import config.FrontendAppConfig
import controllers.actions._
import handlers.{ConfirmationControllerRequestHandler, ErrorHandler}
import javax.inject.Inject
import pages.FurloughOngoingPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import views.html.ConfirmationView

import scala.concurrent.Future

class ConfirmationController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  config: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmationView
)(implicit val errorHandler: ErrorHandler)
    extends BaseController with ConfirmationControllerRequestHandler {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswer(FurloughOngoingPage) { furlough =>
      loadResultData(request.userAnswers).fold(Future.successful(Redirect(routes.ErrorController.somethingWentWrong())))(data =>
        Future.successful(Ok(view(data.confirmationMetadata, data.confirmationViewBreakdown, config.calculatorVersion, furlough))))
    }
  }
}
