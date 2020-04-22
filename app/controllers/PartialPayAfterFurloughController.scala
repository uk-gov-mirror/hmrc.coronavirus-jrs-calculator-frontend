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
import models.{NormalMode, PaymentFrequency}
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
    (request.userAnswers.getList(PayDatePage), request.userAnswers.get(FurloughEndDatePage)) match {
      case (Nil, _) => Redirect(routes.PayDateController.onPageLoad(1))
      case (payPeriods, Some(furloughEndDate)) =>
        earliestAfterFurloughEnd(payPeriods, furloughEndDate) match {
          case Some(payPeriod) if furloughEndDate.isBefore(payPeriod) =>
            val preparedForm = request.userAnswers.get(PartialPayAfterFurloughPage) match {
              case None        => form
              case Some(value) => form.fill(value)
            }
            Ok(
              view(
                preparedForm,
                furloughEndDate.plusDays(1),
                messageDate(payPeriod, furloughEndDate, request.userAnswers.get(PaymentFrequencyPage)),
                routes.PartialPayAfterFurloughController.onSubmit()
              ))

          case _ => Redirect(navigator.nextPage(PartialPayAfterFurloughPage, NormalMode, request.userAnswers))
        }

      case (_, None) =>
        request.userAnswers.get(FurloughQuestionPage) match {
          case Some(_) =>
            //this must be Furlough ongoing
            Redirect(navigator.nextPage(PartialPayAfterFurloughPage, NormalMode, request.userAnswers))
          case None =>
            //User not answered FurloughQuestion page, so redirect them to FurloughQuestion
            Redirect(routes.FurloughQuestionController.onPageLoad(NormalMode))
        }
    }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    (request.userAnswers.getList(PayDatePage), request.userAnswers.get(FurloughEndDatePage)) match {
      case (Nil, _) => Future.successful(Redirect(routes.PayDateController.onPageLoad(1)))
      case (payPeriods, Some(furloughEndDate)) =>
        earliestAfterFurloughEnd(payPeriods, furloughEndDate) match {
          case Some(payPeriod) if furloughEndDate.isBefore(payPeriod) =>
            form
              .bindFromRequest()
              .fold(
                formWithErrors =>
                  Future.successful(
                    BadRequest(view(
                      formWithErrors,
                      furloughEndDate.plusDays(1),
                      messageDate(payPeriod, furloughEndDate, request.userAnswers.get(PaymentFrequencyPage)),
                      routes.PartialPayAfterFurloughController.onSubmit()
                    ))),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(PartialPayAfterFurloughPage, value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield {
                    Redirect(navigator.nextPage(PartialPayAfterFurloughPage, NormalMode, updatedAnswers))
                }
              )

          case _ => Future.successful(Redirect(navigator.nextPage(PartialPayAfterFurloughPage, NormalMode, request.userAnswers)))
        }

      case (_, None) =>
        request.userAnswers.get(FurloughQuestionPage) match {
          case Some(_) =>
            //this must be Furlough ongoing
            Future.successful(Redirect(navigator.nextPage(PartialPayAfterFurloughPage, NormalMode, request.userAnswers)))
          case None =>
            //User not answered FurloughQuestion page, so redirect them to FurloughQuestion
            Future.successful(Redirect(routes.FurloughQuestionController.onPageLoad(NormalMode)))
        }
    }
  }

  private def messageDate(payperiod: LocalDate, furloughEnd: LocalDate, paymentFrequency: Option[PaymentFrequency]): LocalDate = {
    def earliestOf(a: LocalDate, b: LocalDate): LocalDate = if (a.isAfter(b)) b else a
    paymentFrequency match {
      case Some(Weekly)      => earliestOf(furloughEnd.plusDays(7), payperiod)
      case Some(FortNightly) => earliestOf(furloughEnd.plusDays(14), payperiod)
      case Some(FourWeekly)  => earliestOf(furloughEnd.plusDays(28), payperiod)
      case Some(Monthly)     => earliestOf(furloughEnd.plusMonths(1), payperiod)
    }
  }

  private def earliestAfterFurloughEnd(payPeriods: Seq[LocalDate], furloughEnd: LocalDate) =
    payPeriods.collectFirst {
      case pp if pp.isAfter(furloughEnd) || pp.isEqual(furloughEnd) => pp
    }
}
