/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.SalaryQuestionFormProvider
import javax.inject.Inject
import models.{Mode, PaymentFrequency}
import navigation.Navigator
import pages.{PaymentFrequencyPage, SalaryQuestionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.SalaryQuestionView

import scala.concurrent.{ExecutionContext, Future}

class SalaryQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: SalaryQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SalaryQuestionView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val maybePf = request.userAnswers.get[PaymentFrequency](PaymentFrequencyPage)
    val maybeSalary = request.userAnswers.get(SalaryQuestionPage)

    (maybePf, maybeSalary) match {
      case (Some(pf), Some(sq)) => Ok(view(form.fill(sq), pf, mode))
      case (Some(pf), None)     => Ok(view(form, pf, mode))
      case (None, _)            => Redirect(routes.PaymentFrequencyController.onPageLoad(mode))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val maybePf = request.userAnswers.get[PaymentFrequency](PaymentFrequencyPage)

          val result = maybePf match {
            case Some(pf) => BadRequest(view(formWithErrors, pf, mode))
            case None     => Redirect(routes.PaymentFrequencyController.onPageLoad(mode))
          }
          Future.successful(result)
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SalaryQuestionPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SalaryQuestionPage, mode, updatedAnswers))
      )
  }
}
