/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package views.components

import config.FrontendAppConfig
import controllers.routes
import javax.inject.Inject
import play.api.i18n.Messages
import play.api.mvc.Request
import uk.gov.hmrc.govukfrontend.views.viewmodels.footer.FooterItem

class FooterLinks @Inject()(appConfig: FrontendAppConfig) {

  def cookieLink(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footer.cookies")),
    Some(appConfig.cookies)
  )

  def privacyLink(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footer.privacy")),
    Some(appConfig.privacy)
  )

  def termsConditionsLink(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footer.termsConditions")),
    Some(appConfig.termsConditions)
  )

  def govukHelpLink(implicit messages: Messages): FooterItem = FooterItem(
    Some(messages("footer.govukHelp")),
    Some(appConfig.govukHelp)
  )

  def accessibilityLink(implicit messages: Messages, request: Request[_]): FooterItem = FooterItem(
    Some(messages("footer.accessibility")),
    Some(routes.AccessibilityStatementController.onPageLoad(request.uri).url)
  )

  def items(implicit messages: Messages, request: Request[_]) = Seq(
    cookieLink,
    accessibilityLink,
    privacyLink,
    termsConditionsLink,
    govukHelpLink
  )
}
