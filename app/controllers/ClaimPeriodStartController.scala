/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.ClaimPeriodStartFormProvider
import javax.inject.Inject
import models.{Mode, UserAnswers}
import navigation.Navigator
import pages.ClaimPeriodStartPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ClaimPeriodStartView

import scala.concurrent.{ExecutionContext, Future}

class ClaimPeriodStartController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ClaimPeriodStartFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ClaimPeriodStartView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    val preparedForm = request.userAnswers.getOrElse(UserAnswers(request.internalId)).get(ClaimPeriodStartPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(
                               request.userAnswers.getOrElse(UserAnswers(request.internalId)).set(ClaimPeriodStartPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ClaimPeriodStartPage, mode, updatedAnswers))
      )
  }
}
