/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.FurloughPartialPayFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{NormalMode, PaymentFrequency}
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.VariableLengthPartialPayView

import scala.concurrent.{ExecutionContext, Future}

class PartialPayBeforeFurloughController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughPartialPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthPartialPayView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    (request.userAnswers.getList(PayDatePage), request.userAnswers.get(FurloughStartDatePage)) match {
      case (Nil, _) => Redirect(routes.PayDateController.onPageLoad(1))
      case (payPeriods, Some(furloughStartDate)) =>
        latestBeforeFurloughStart(payPeriods, furloughStartDate) match {
          case Some(payPeriod) if payPeriod.plusDays(1).isBefore(furloughStartDate) =>
            val preparedForm = request.userAnswers.get(PartialPayBeforeFurloughPage) match {
              case None        => form
              case Some(value) => form.fill(value)
            }
            Ok(
              view(
                preparedForm,
                messageDate(payPeriod.plusDays(1), furloughStartDate, request.userAnswers.get(PaymentFrequencyPage)),
                furloughStartDate.minusDays(1),
                routes.PartialPayBeforeFurloughController.onSubmit()
              ))
          case _ => Redirect(navigator.nextPage(PartialPayBeforeFurloughPage, NormalMode, request.userAnswers))
        }

      case (_, None) => Redirect(routes.FurloughStartDateController.onPageLoad(NormalMode))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    (request.userAnswers.getList(PayDatePage), request.userAnswers.get(FurloughStartDatePage)) match {
      case (Nil, _) => Future.successful(Redirect(routes.PayDateController.onPageLoad(1)))
      case (payPeriods, Some(furloughStartDate)) =>
        latestBeforeFurloughStart(payPeriods, furloughStartDate) match {
          case Some(payPeriod) if payPeriod.plusDays(1).isBefore(furloughStartDate) =>
            form
              .bindFromRequest()
              .fold(
                formWithErrors =>
                  Future.successful(
                    BadRequest(view(
                      formWithErrors,
                      messageDate(payPeriod.plusDays(1), furloughStartDate, request.userAnswers.get(PaymentFrequencyPage)),
                      furloughStartDate.minusDays(1),
                      routes.PartialPayBeforeFurloughController.onSubmit()
                    ))),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(PartialPayBeforeFurloughPage, value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield {
                    Redirect(navigator.nextPage(PartialPayBeforeFurloughPage, NormalMode, updatedAnswers))
                }
              )
          case None => Future.successful(Redirect(routes.PartialPayAfterFurloughController.onPageLoad()))
        }
      case (_, None) => Future.successful(Redirect(routes.FurloughStartDateController.onPageLoad(NormalMode)))
    }
  }

  private def messageDate(payPeriod: LocalDate, furloughStart: LocalDate, paymentFrequency: Option[PaymentFrequency]): LocalDate = {
    def latestOf(a: LocalDate, b: LocalDate): LocalDate = if (a.isAfter(b)) a else b
    paymentFrequency match {
      case Some(Weekly)      => latestOf(furloughStart.minusDays(7), payPeriod)
      case Some(FortNightly) => latestOf(furloughStart.minusDays(14), payPeriod)
      case Some(FourWeekly)  => latestOf(furloughStart.minusDays(28), payPeriod)
      case Some(Monthly)     => latestOf(furloughStart.minusMonths(1), payPeriod)
    }
  }

  private def latestBeforeFurloughStart(payPeriods: Seq[LocalDate], furloughStart: LocalDate) =
    payPeriods.reverse.collectFirst {
      case pp if pp.isBefore(furloughStart) || pp.isEqual(furloughStart) => pp
    }
}
