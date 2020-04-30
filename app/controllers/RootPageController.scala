/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.RootPageView

class RootPageController @Inject()(
  override val messagesApi: MessagesApi,
  val controllerComponents: MessagesControllerComponents,
  view: RootPageView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    Ok(view())
  }
}
