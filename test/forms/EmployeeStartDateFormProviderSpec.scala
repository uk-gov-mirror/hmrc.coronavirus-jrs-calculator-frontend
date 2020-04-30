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

  val furloughStart = LocalDate.of(2020, 3, 10)

  val formProvider = new EmployeeStartDateFormProvider()

  val form = formProvider(furloughStart)

  ".value" should {

    val validData = datesBetween(
      min = formProvider.feb2nd2019,
      max = furloughStart
    )

    behave like dateField(form, "value", validData)

    behave like dateFieldWithMax(form, "value", furloughStart.minusDays(1), FormError("value", "employeeStartDate.error.max"))

    behave like dateFieldWithMin(
      form,
      "value",
      formProvider.feb2nd2019,
      FormError("value", "employeeStartDate.error.min", Array(dateToString(formProvider.feb2nd2019))))

    behave like mandatoryDateField(form, "value", "employeeStartDate.error.required.all")

  }
}
