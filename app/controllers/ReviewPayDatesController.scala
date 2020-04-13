/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.ReviewPayDatesFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{PayDatePage, ReviewPayDatesPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ReviewPayDatesView

import scala.concurrent.{ExecutionContext, Future}

class ReviewPayDatesController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ReviewPayDatesFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ReviewPayDatesView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val payDateList = request.userAnswers.getList(PayDatePage)
    if (payDateList.nonEmpty) {
      Ok(view(payDateList, form, mode))
    } else {
      Redirect(routes.PayDateController.onPageLoad(1))
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      val payDateList = request.userAnswers.getList(PayDatePage)
      Future.successful(
        form
          .bindFromRequest()
          .fold(
            formWithErrors => BadRequest(view(payDateList, formWithErrors, mode)), {
              case true => {
                Redirect(routes.PayDateController.onPageLoad(payDateList.size + 1))
              }
              case false => Redirect(navigator.nextPage(ReviewPayDatesPage, mode, request.userAnswers))
            }
          ))
  }
}
