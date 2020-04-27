/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions.FeatureFlag.VariableJourneyFlag
import controllers.actions._
import forms.FurloughPartialPayFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.NormalMode
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.PartialPayHelper
import views.html.VariableLengthPartialPayView

import scala.concurrent.{ExecutionContext, Future}

class PartialPayBeforeFurloughController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughPartialPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthPartialPayView,
  eh: ErrorHandler
)(implicit ec: ExecutionContext)
    extends BaseController with I18nSupport with PartialPayHelper {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getPartialPeriods(request.userAnswers)
        .find(isFurloughStart)
        .map(getPeriodRemainder)
        .fold(
          Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        ) { beforeFurlough =>
          val preparedForm = request.userAnswers.get(PartialPayBeforeFurloughPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Future.successful(
            Ok(
              view(
                preparedForm,
                beforeFurlough.start,
                beforeFurlough.end,
                routes.PartialPayBeforeFurloughController.onSubmit()
              )))
        }
  }

  def onSubmit: Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getPartialPeriods(request.userAnswers)
        .find(isFurloughStart)
        .map(getPeriodRemainder)
        .fold(
          Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        ) { partialPeriodBefore =>
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful(
                  BadRequest(
                    view(
                      formWithErrors,
                      partialPeriodBefore.start,
                      partialPeriodBefore.end,
                      routes.PartialPayBeforeFurloughController.onSubmit()
                    ))), { value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(PartialPayBeforeFurloughPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield {
                  Redirect(navigator.nextPage(PartialPayBeforeFurloughPage, NormalMode, updatedAnswers))
                }
              }
            )
        }
  }
}
