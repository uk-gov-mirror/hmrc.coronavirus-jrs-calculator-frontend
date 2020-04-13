/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import models.Language
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController

class LanguageSwitchController @Inject()(
  appConfig: FrontendAppConfig,
  override implicit val messagesApi: MessagesApi,
  val controllerComponents: MessagesControllerComponents
) extends FrontendBaseController with I18nSupport {

  private def fallbackURL: String = routes.RootPageController.onPageLoad().url

  def switchToLanguage(language: Language): Action[AnyContent] = Action { implicit request =>
    val languageToUse = if (appConfig.languageTranslationEnabled) {
      language
    } else {
      Language.English
    }

    val redirectURL = request.headers.get(REFERER).getOrElse(fallbackURL)
    Redirect(redirectURL).withLang(languageToUse.lang)
  }
}
