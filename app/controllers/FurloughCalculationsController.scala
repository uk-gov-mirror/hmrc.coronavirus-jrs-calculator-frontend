/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.FurloughCalculationsFormProvider
import handlers.ConfirmationControllerRequestHandler
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.FurloughCalculationsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.FurloughCalculationsView

import scala.concurrent.{ExecutionContext, Future}

class FurloughCalculationsController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughCalculationsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughCalculationsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with ConfirmationControllerRequestHandler {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    loadResultData(request.userAnswers).fold(InternalServerError("Something went horribly wrong")) { data =>
      val preparedForm = request.userAnswers.get(FurloughCalculationsPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, data.confirmationViewBreakdown.furlough))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    loadResultData(request.userAnswers).fold(Future.successful(InternalServerError("Something went horribly wrong"))) { data =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, data.confirmationViewBreakdown.furlough))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughCalculationsPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(FurloughCalculationsPage, mode, updatedAnswers))
        )
    }

  }
}
