/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html._

class AccessibilityStatementController @Inject()(
  val controllerComponents: MessagesControllerComponents,
  view: AccessibilityStatementView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    Ok(view())
  }
}
