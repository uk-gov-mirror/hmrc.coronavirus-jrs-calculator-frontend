/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import javax.inject.Inject
import models.UserAnswers
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.IndexView

import scala.concurrent.ExecutionContext

class IndexController @Inject()(
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  sessionRepository: SessionRepository,
  view: IndexView,
  val controllerComponents: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def keepalive: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse[UserAnswers](UserAnswers(request.internalId))
    sessionRepository.set(userAnswers).map { _ =>
      NoContent
    }
  }

  def restart: Action[AnyContent] = Action { _ =>
    Redirect(routes.ClaimPeriodStartController.onPageLoad()).withNewSession
  }
}
