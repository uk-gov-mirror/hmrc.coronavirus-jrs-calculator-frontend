/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.coronavirus.jrs.calculator.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.coronavirus.jrs.calculator.views.html.warm_up
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CalculatorController @Inject()(mcc: MessagesControllerComponents, warmUpView: warm_up)
    extends FrontendController(mcc) {

  def warmUp = Action.async { implicit request =>
    Future successful Ok(warmUpView())
  }

}
