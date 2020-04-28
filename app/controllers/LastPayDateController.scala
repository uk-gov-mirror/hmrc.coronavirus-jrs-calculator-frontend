/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.LastPayDateFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.{LastPayDatePage, PayDatePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.LastPayDateView

import scala.concurrent.{ExecutionContext, Future}

class LastPayDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: LastPayDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: LastPayDateView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form(latestPayDate: LocalDate) = formProvider(latestPayDate)

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    request.userAnswers.getList(PayDatePage).lastOption match {
      case Some(date) =>
        val preparedForm = request.userAnswers.get(LastPayDatePage) match {
          case None        => form(date)
          case Some(value) => form(date).fill(value)
        }

        Ok(view(preparedForm, date))

      case None => Redirect(routes.PayDateController.onPageLoad(1))
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.getList(PayDatePage).lastOption match {
      case Some(date) =>
        form(date)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, date))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(LastPayDatePage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(LastPayDatePage, updatedAnswers))
          )

      case None => Future.successful(Redirect(routes.PayDateController.onPageLoad(1)))
    }
  }
}
