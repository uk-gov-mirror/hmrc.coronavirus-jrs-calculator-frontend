/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.FurloughDatesFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughDatesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.FurloughDatesView

import scala.concurrent.{ExecutionContext, Future}

class FurloughDatesController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughDatesFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughDatesView,
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, ClaimPeriodEndPage) { (claimStartDate, claimEndDate) =>
      val preparedForm = request.userAnswers.get(FurloughDatesPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Future.successful(Ok(view(preparedForm, mode, claimStartDate, claimEndDate)))
    }

  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, ClaimPeriodEndPage) { (claimStartDate, claimEndDate) =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, claimStartDate, claimEndDate))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughDatesPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(FurloughDatesPage, mode, updatedAnswers))
        )
    }
  }
}
