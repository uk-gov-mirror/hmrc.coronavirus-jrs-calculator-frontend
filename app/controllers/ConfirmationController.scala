/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import handlers.ConfirmationControllerRequestHandler
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ConfirmationView

import scala.concurrent.ExecutionContext

class ConfirmationController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with ConfirmationControllerRequestHandler {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    loadResultData(request.userAnswers).fold(InternalServerError("Something went horribly wrong"))(data =>
      Ok(view(data.confirmationMetadata, data.confirmationViewBreakdown)))
  }
}
