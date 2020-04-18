/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.VariableLengthPartialPayFormProvider
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

  def onPageLoadBeforeFurlough: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
      val result =
        if (furloughStartDate.isAfter(claimStartDate.plusDays(1))) {
          val preparedForm = request.userAnswers.get(VariableLengthPartialPayPage, Some(1)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
          Ok(
            view(
              preparedForm,
              latestOf(claimStartDate, furloughStartDate, request.userAnswers.get(PaymentFrequencyPage)),
              furloughStartDate.minusDays(1),
              routes.VariableLengthPartialPayController.onSubmitBeforeFurlough()
            ))
        } else {
          Redirect(routes.VariableLengthPartialPayController.onPageLoadAfterFurlough)
        }
      Future.successful(result)
    }
  }

  def onPageLoadAfterFurlough: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val claimEnd = request.userAnswers.get(ClaimPeriodEndPage)
    val furloughEnd = request.userAnswers.get(FurloughEndDatePage)
    (claimEnd, furloughEnd) match {
      case (Some(cEnd), Some(fEnd)) if cEnd.isAfter(fEnd.plusDays(1)) =>
        val preparedForm = request.userAnswers.get(VariableLengthPartialPayPage, Some(2)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(
          view(
            preparedForm,
            fEnd.plusDays(1),
            earliestOf(cEnd, fEnd, request.userAnswers.get(PaymentFrequencyPage)),
            routes.VariableLengthPartialPayController.onSubmitAfterFurlough()
          ))

      case (Some(_), _) =>
        Redirect((routes.VariableGrossPayController.onPageLoad(NormalMode)))

      case (None, _) => Redirect((routes.ClaimPeriodEndController.onPageLoad(NormalMode)))
    }
  }

  def onSubmitBeforeFurlough: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswers(ClaimPeriodStartPage, FurloughStartDatePage) { (claimStartDate, furloughStartDate) =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(
              BadRequest(
                view(
                  formWithErrors,
                  claimStartDate,
                  furloughStartDate.minusDays(1),
                  routes.VariableLengthPartialPayController.onSubmitBeforeFurlough()))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(VariableLengthPartialPayPage, value, Some(1)))
              _              <- sessionRepository.set(updatedAnswers)
            } yield {
              Redirect(routes.VariableLengthPartialPayController.onPageLoadAfterFurlough)
          }
        )
    }
  }

  def onSubmitAfterFurlough: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val claimEnd = request.userAnswers.get(ClaimPeriodEndPage)
    val furloughEnd = request.userAnswers.get(FurloughEndDatePage)
    (claimEnd, furloughEnd) match {
      case (Some(cEnd), Some(fEnd)) if cEnd.isAfter(fEnd.plusDays(1)) =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors =>
              Future.successful(
                BadRequest(
                  view(formWithErrors, fEnd.plusDays(1), cEnd, routes.VariableLengthPartialPayController.onSubmitAfterFurlough()))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(VariableLengthPartialPayPage, value, Some(2)))
                _              <- sessionRepository.set(updatedAnswers)
              } yield {
                Redirect(routes.VariableGrossPayController.onPageLoad(NormalMode))
            }
          )
      case (Some(_), None) =>
        Future.successful(Redirect((routes.VariableGrossPayController.onPageLoad(NormalMode))))

      case (None, _) =>
        Future.successful(Redirect((routes.ClaimPeriodEndController.onPageLoad(NormalMode))))
    }
  }

  private def latestOf(claimStart: LocalDate, furloughStart: LocalDate, paymentFrequency: Option[PaymentFrequency]): LocalDate = {
    def latestOf(a: LocalDate, b: LocalDate): LocalDate = if (a.isAfter(b)) a else b
    paymentFrequency match {
      case Some(Weekly)      => latestOf(furloughStart.minusDays(7), claimStart)
      case Some(FortNightly) => latestOf(furloughStart.minusDays(14), claimStart)
      case Some(FourWeekly)  => latestOf(furloughStart.minusDays(28), claimStart)
      case Some(Monthly)     => latestOf(furloughStart.minusMonths(1), claimStart)
    }
  }

  private def earliestOf(claimStart: LocalDate, furloughStart: LocalDate, paymentFrequency: Option[PaymentFrequency]): LocalDate = {
    def earliestOf(a: LocalDate, b: LocalDate): LocalDate = if (a.isAfter(b)) b else a
    paymentFrequency match {
      case Some(Weekly)      => earliestOf(furloughStart.plusDays(7), claimStart)
      case Some(FortNightly) => earliestOf(furloughStart.plusDays(14), claimStart)
      case Some(FourWeekly)  => earliestOf(furloughStart.plusDays(28), claimStart)
      case Some(Monthly)     => earliestOf(furloughStart.plusMonths(1), claimStart)
    }
  }

}
