/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import config.FrontendAppConfig
import controllers.actions.FeatureFlag.VariableJourneyFlag
import controllers.actions._
import forms.VariableGrossPayFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import navigation.Navigator
import pages.{FurloughStartDatePage, VariableGrossPayPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.VariableGrossPayView

import scala.concurrent.{ExecutionContext, Future}

class VariableGrossPayController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: VariableGrossPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableGrossPayView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler, appConfig: FrontendAppConfig)
    extends BaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] =
    (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async { implicit request =>
      request.userAnswers.get(FurloughStartDatePage) match {
        case Some(furloughStart) =>
          val preparedForm = request.userAnswers.get(VariableGrossPayPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Future.successful(Ok(view(preparedForm, furloughStart)))

        case None => Future.successful(Redirect(routes.FurloughStartDateController.onPageLoad()))
      }
    }

  def onSubmit(): Action[AnyContent] =
    (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async { implicit request =>
      request.userAnswers.get(FurloughStartDatePage) match {
        case Some(furloughStart) =>
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, furloughStart))),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(VariableGrossPayPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(VariableGrossPayPage, updatedAnswers))
            )

        case None => Future.successful(Redirect(routes.FurloughStartDateController.onPageLoad()))
      }
    }
}
