/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import config.FrontendAppConfig
import controllers.actions.FeatureFlag.VariableJourneyFlag
import controllers.actions._
import forms.VariableLengthEmployedFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.VariableLengthEmployedPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.VariableLengthEmployedView

import scala.concurrent.{ExecutionContext, Future}

class VariableLengthEmployedController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: VariableLengthEmployedFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthEmployedView
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.get(VariableLengthEmployedPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(VariableLengthEmployedPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(VariableLengthEmployedPage, mode, updatedAnswers))
        )
  }
}
