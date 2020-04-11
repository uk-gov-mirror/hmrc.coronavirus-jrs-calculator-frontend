/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.{LocalDate, ZoneOffset}

import base.{SpecBase, SpecBaseWithApplication}
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class TestOnlyNICGrantCalculatorFormProviderSpec extends SpecBaseWithApplication {

  val form = new TestOnlyNICGrantCalculatorFormProvider(frontendAppConfig)
  val dateBehaviours = new DateBehaviours

  import dateBehaviours._

  ".value" should {}
}
