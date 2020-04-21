/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import controllers.actions._
import forms.FurloughPartialPayFormProvider
import javax.inject.Inject
import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import models.{Mode, NormalMode, PaymentFrequency}
import navigation.Navigator
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.VariableLengthPartialPayView

import scala.concurrent.{ExecutionContext, Future}

class PartialPayAfterFurloughController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughPartialPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthPartialPayView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val claimEnd = request.userAnswers.get(ClaimPeriodEndPage)
    val furloughEnd = request.userAnswers.get(FurloughEndDatePage)
    (claimEnd, furloughEnd) match {
      case (Some(cEnd), Some(fEnd)) if cEnd.isAfter(fEnd.plusDays(1)) =>
        val preparedForm = request.userAnswers.get(PartialPayAfterFurloughPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(
          view(
            preparedForm,
            fEnd.plusDays(1),
            earliestOf(cEnd, fEnd, request.userAnswers.get(PaymentFrequencyPage)),
            routes.PartialPayAfterFurloughController.onSubmit()
          ))

      case (Some(_), None) =>
        Redirect(routes.NicCategoryController.onPageLoad(NormalMode))

      case (None, _) => Redirect(routes.ClaimPeriodEndController.onPageLoad(NormalMode))

      case _ =>
        Redirect(routes.NicCategoryController.onPageLoad(NormalMode))
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    val claimEnd = request.userAnswers.get(ClaimPeriodEndPage)
    val furloughEnd = request.userAnswers.get(FurloughEndDatePage)
    (claimEnd, furloughEnd) match {
      case (Some(cEnd), Some(fEnd)) if cEnd.isAfter(fEnd.plusDays(1)) =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors =>
              Future.successful(
                BadRequest(view(formWithErrors, fEnd.plusDays(1), cEnd, routes.PartialPayAfterFurloughController.onSubmit()))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(PartialPayAfterFurloughPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield {
                Redirect(navigator.nextPage(PartialPayAfterFurloughPage, NormalMode, updatedAnswers))
            }
          )
      case (Some(_), None) =>
        Future.successful(Redirect((routes.NicCategoryController.onPageLoad(NormalMode))))

      case (None, _) =>
        Future.successful(Redirect((routes.ClaimPeriodEndController.onPageLoad(NormalMode))))

      case _ =>
        Future.successful(Redirect((routes.NicCategoryController.onPageLoad(NormalMode))))
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
