/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.ComingSoonView

class ComingSoonController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ComingSoonView
) extends FrontendBaseController with I18nSupport {

  def onPageLoad(showCalculateTopupsLink: Boolean = false): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      Ok(view(showCalculateTopupsLink))
  }
}
