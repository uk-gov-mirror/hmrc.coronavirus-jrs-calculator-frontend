/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.LastYearPayFormProvider
import handlers.LastYearPayControllerRequestHandler
import javax.inject.Inject
import models.{CylbPayment, CylbPaymentWith2020Periods, NormalMode, PaymentFrequency, PeriodWithPaymentDate}
import navigation.Navigator
import pages.{LastYearPayPage, PaymentFrequencyPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.LastYearPayView

import scala.concurrent.{ExecutionContext, Future}

class LastYearPayController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: LastYearPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: LastYearPayView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with LastYearPayControllerRequestHandler {

  val form = formProvider()

  def onPageLoad(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getPayDates(request.userAnswers).fold(
      Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    ) { payDatesWith2020Periods =>
      withValidPayDate(payDatesWith2020Periods, idx) { (date, _) =>
        val preparedForm = request.userAnswers.get(LastYearPayPage) match {
          case None        => form
          case Some(value) => form.fill(value.cylbPayment.amount)
        }

        val isMonthlyFrequency = request.userAnswers.get(PaymentFrequencyPage).contains(PaymentFrequency.Monthly)

        Future.successful(Ok(view(preparedForm, idx, date, isMonthlyFrequency)))
      }
    }
  }

  def withValidPayDate(payDates: Seq[(LocalDate, Seq[PeriodWithPaymentDate])], idx: Int)(
    f: (LocalDate, Seq[PeriodWithPaymentDate]) => Future[Result]): Future[Result] =
    payDates.lift(idx - 1) match {
      case Some((date, periods)) => f(date, periods)
      case None                  => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }

  def onSubmit(idx: Int): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getPayDates(request.userAnswers).fold(
      Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    ) { payDatesWith2020Periods =>
      withValidPayDate(payDatesWith2020Periods, idx) { (date, relevantPeriods) =>
        val isMonthlyFrequency = request.userAnswers.get(PaymentFrequencyPage).contains(PaymentFrequency.Monthly)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, idx, date, isMonthlyFrequency))),
            value => {
              val valueToSave = CylbPaymentWith2020Periods(CylbPayment(date, value), relevantPeriods)
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(LastYearPayPage, valueToSave, Some(idx)))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(LastYearPayPage, NormalMode, updatedAnswers, Some(idx)))
            }
          )
      }
    }
  }
}
