/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait VariableLengthEmployed

object VariableLengthEmployed extends Enumerable.Implicits {

  case object Lessthan12months extends WithName("lessThan12Months") with VariableLengthEmployed
  case object Morethan12months extends WithName("morethan12Months") with VariableLengthEmployed

  val values: Seq[VariableLengthEmployed] = Seq(
    Lessthan12months,
    Morethan12months
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"variableLengthEmployed.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[VariableLengthEmployed] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
