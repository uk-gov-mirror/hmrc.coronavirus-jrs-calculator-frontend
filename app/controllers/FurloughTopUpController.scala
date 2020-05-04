/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.FurloughTopUpFormProvider
import handlers.FurloughTopUpControllerRequestHandler
import javax.inject.Inject
import navigation.Navigator
import pages.FurloughTopUpStatusPage
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.FurloughTopUpView

import scala.concurrent.{ExecutionContext, Future}

class FurloughTopUpController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughTopUpFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughTopUpView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with FurloughTopUpControllerRequestHandler {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    handleCalculationFurlough(request.userAnswers).fold {
      Logger.warn("couldn't calculate Furlough out of UserAnswers, restarting the journey")
      Redirect(routes.ClaimPeriodStartController.onPageLoad())
    } { data =>
      val preparedForm = request.userAnswers.get(FurloughTopUpStatusPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, data))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    handleCalculationFurlough(request.userAnswers)
      .fold {
        Logger.warn("couldn't calculate Furlough out of UserAnswers, restarting the journey")
        Future.successful(Redirect(routes.ClaimPeriodStartController.onPageLoad()))
      } { data =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, data))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughTopUpStatusPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(FurloughTopUpStatusPage, updatedAnswers))
          )
      }

  }
}
