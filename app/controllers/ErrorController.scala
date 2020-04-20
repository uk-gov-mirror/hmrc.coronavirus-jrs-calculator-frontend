/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ErrorView

class ErrorController @Inject()(
  val controllerComponents: MessagesControllerComponents,
  view: ErrorView
) extends FrontendBaseController with I18nSupport {

  def internalServerError: Action[AnyContent] = Action { implicit request =>
    InternalServerError(view())
  }

  def somethingWentWrong: Action[AnyContent] = Action { implicit request =>
    InternalServerError(view(true))
  }
}
