/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.PayDateFormProvider
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages.PayDatePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.PayDateView

import scala.concurrent.{ExecutionContext, Future}

class PayDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PayDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PayDateView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(PayDatePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, idx))
  }

  def form = formProvider()

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, idx))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PayDatePage, value, Some(idx)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(PayDatePage, NormalMode, updatedAnswers))
        )
  }
}
