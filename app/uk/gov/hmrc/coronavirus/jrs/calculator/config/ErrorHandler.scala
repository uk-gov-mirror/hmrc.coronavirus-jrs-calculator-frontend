/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.coronavirus.jrs.calculator.config

import javax.inject.{Inject, Singleton}

import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.http.FrontendErrorHandler
import uk.gov.hmrc.coronavirus.jrs.calculator.views.html.error_template

@Singleton
class ErrorHandler @Inject()(
  val messagesApi: MessagesApi,
  implicit val appConfig: AppConfig,
  errorTemplateView: error_template)
    extends FrontendErrorHandler {
  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(
    implicit request: Request[_]): Html =
    errorTemplateView(pageTitle, heading, message)
}
