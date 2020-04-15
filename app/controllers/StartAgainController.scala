/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import javax.inject.Inject
import models.UserAnswers
import navigation.Navigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

class StartAgainController @Inject()(
  val controllerComponents: MessagesControllerComponents,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  sessionRepository: SessionRepository,
) extends FrontendBaseController with I18nSupport {

  def startAgain: Action[AnyContent] = (identify andThen getData) { implicit request =>
    sessionRepository.set(UserAnswers(request.internalId))
    Redirect(routes.RootPageController.onPageLoad())
  }
}
