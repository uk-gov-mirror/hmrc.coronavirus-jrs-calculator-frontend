/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.FurloughEndDateFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import navigation.Navigator
import pages.{ClaimPeriodEndPage, FurloughEndDatePage, FurloughStartDatePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.FurloughEndDateView

import scala.concurrent.{ExecutionContext, Future}

class FurloughEndDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughEndDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughEndDateView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodEndPage, FurloughStartDatePage) { (claimEndDate, furloughStart) =>
      val preparedForm = request.userAnswers.get(FurloughEndDatePage) match {
        case None        => form(claimEndDate, furloughStart)
        case Some(value) => form(claimEndDate, furloughStart).fill(value)
      }

      Future.successful(Ok(view(preparedForm)))
    }
  }

  def form(claimEndDate: LocalDate, furloughStartDate: LocalDate) =
    formProvider(claimEndDate, furloughStartDate)

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodEndPage, FurloughStartDatePage) { (claimEndDate, furloughStart) =>
      form(claimEndDate, furloughStart)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughEndDatePage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(FurloughEndDatePage, updatedAnswers))
        )
    }
  }
}
