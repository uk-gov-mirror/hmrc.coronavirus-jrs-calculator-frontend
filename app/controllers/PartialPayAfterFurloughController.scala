/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions.FeatureFlag.VariableJourneyFlag
import controllers.actions._
import forms.FurloughPartialPayFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.PartialPayHelper
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.VariableLengthPartialPayView

import scala.concurrent.{ExecutionContext, Future}

class PartialPayAfterFurloughController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughPartialPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthPartialPayView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with PartialPayHelper {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getPartialPeriods(request.userAnswers)
        .find(isFurloughEnd)
        .map(getPeriodRemainder)
        .fold(
          Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        ) { afterFurlough =>
          val preparedForm = request.userAnswers.get(PartialPayAfterFurloughPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Future.successful(
            Ok(
              view(
                preparedForm,
                afterFurlough.start,
                afterFurlough.end,
                routes.PartialPayAfterFurloughController.onSubmit()
              )))
        }
  }

  def onSubmit: Action[AnyContent] = (identify andThen feature(VariableJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getPartialPeriods(request.userAnswers)
        .find(isFurloughEnd)
        .map(getPeriodRemainder)
        .fold(
          Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        ) { afterFurlough =>
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful(
                  BadRequest(
                    view(
                      formWithErrors,
                      afterFurlough.start,
                      afterFurlough.end,
                      routes.PartialPayAfterFurloughController.onSubmit()
                    ))), { value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(PartialPayAfterFurloughPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield {
                  Redirect(navigator.nextPage(PartialPayAfterFurloughPage, updatedAnswers))
                }
              }
            )
        }
  }
}
