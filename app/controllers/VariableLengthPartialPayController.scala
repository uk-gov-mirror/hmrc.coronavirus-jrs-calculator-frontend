/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.VariableLengthPartialPayFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{ClaimPeriodStartPage, FurloughStartDatePage, VariableLengthPartialPayPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.VariableLengthPartialPayView

import scala.concurrent.{ExecutionContext, Future}

class VariableLengthPartialPayController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: VariableLengthPartialPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthPartialPayView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
      val result = if (furloughStartDate.isAfter(claimStartDate.plusDays(1))) {
        val preparedForm = request.userAnswers.get(VariableLengthPartialPayPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, mode, claimStartDate, furloughStartDate.minusDays(1)))
      } else {
        Redirect((routes.VariableGrossPayController.onPageLoad(mode)))
      }
      Future.successful(result)
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, claimStartDate, furloughStartDate.minusDays(1)))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(VariableLengthPartialPayPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(VariableLengthPartialPayPage, mode, updatedAnswers))
        )
    }
  }
}
