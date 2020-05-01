/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait EmployeeStarted

object EmployeeStarted extends Enumerable.Implicits {

  case object OnOrBefore1Feb2019 extends WithName("onOrBefore1Feb2019") with EmployeeStarted
  case object After1Feb2019 extends WithName("after1Feb2019") with EmployeeStarted

  val values: Seq[EmployeeStarted] = Seq(
    OnOrBefore1Feb2019,
    After1Feb2019
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"variableLengthEmployed.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[EmployeeStarted] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
