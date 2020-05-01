/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import base.SpecBaseWithApplication
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class FurloughStartDateFormProviderSpec extends SpecBaseWithApplication {

  val dateBehaviours = new DateBehaviours
  import dateBehaviours._

  private val startDate = LocalDate.of(2020, 3, 1)
  private val endDate = LocalDate.of(2020, 6, 1)
  val form = new FurloughStartDateFormProvider(frontendAppConfig)(endDate)

  ".value" should {

    val validData = datesBetween(
      min = startDate,
      max = endDate.minusDays(1)
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMin(form, "value", startDate, FormError("value", "furloughStartDate.error.minimum", Array("1 March 2020")))

    behave like dateFieldWithMax(form, "value", endDate, FormError("value", "furloughStartDate.error.maximum", Array("1 May 2020")))

    behave like mandatoryDateField(form, "value", "furloughStartDate.error.required.all")
  }
}
