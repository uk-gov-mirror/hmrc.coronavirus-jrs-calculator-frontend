/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms

import java.time.LocalDate

import forms.behaviours.DateBehaviours
import play.api.data.FormError
import views.ViewUtils._

class EmployeeStartDateFormProviderSpec extends DateBehaviours {

  val validStart = LocalDate.of(2019, 2, 2)
  val validEnd = LocalDate.of(2020, 3, 19)

  val form = new EmployeeStartDateFormProvider()()

  ".value" should {

    val validData = datesBetween(
      min = validStart,
      max = validEnd
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMax(
      form,
      "value",
      validEnd,
      FormError("value", "employeeStartDate.error.outofrange", Array(dateToString(validStart), dateToString(validEnd))))

    behave like dateFieldWithMin(
      form,
      "value",
      validStart,
      FormError("value", "employeeStartDate.error.outofrange", Array(dateToString(validStart), dateToString(validEnd))))

    behave like mandatoryDateField(form, "value", "employeeStartDate.error.required.all")
  }
}
