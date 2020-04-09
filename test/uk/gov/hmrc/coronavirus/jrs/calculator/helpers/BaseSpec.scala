/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.coronavirus.jrs.calculator.helpers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.Injector
import play.api.mvc.MessagesControllerComponents

trait BaseSpec extends PlaySpec with GuiceOneAppPerSuite {

  lazy val injector: Injector = app.injector

  lazy val mcc = injector.instanceOf[MessagesControllerComponents]

}
