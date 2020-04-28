/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.FurloughStartDateFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import navigation.Navigator
import pages.{ClaimPeriodEndPage, FurloughStartDatePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.FurloughStartDateView

import scala.concurrent.{ExecutionContext, Future}

class FurloughStartDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughStartDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughStartDateView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  def form(claimEndDate: LocalDate) = formProvider(claimEndDate)

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswer(ClaimPeriodEndPage) { claimEndDate =>
      val preparedForm = request.userAnswers.get(FurloughStartDatePage) match {
        case None        => form(claimEndDate)
        case Some(value) => form(claimEndDate).fill(value)
      }

      Future.successful(Ok(view(preparedForm)))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswer(ClaimPeriodEndPage) { claimEndDate =>
      form(claimEndDate)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughStartDatePage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(FurloughStartDatePage, updatedAnswers))
        )
    }
  }
}
