/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait PayMethod

object PayMethod extends Enumerable.Implicits {

  case object Regular extends WithName("regular") with PayMethod
  case object Variable extends WithName("variable") with PayMethod

  val values: Seq[PayMethod] = Seq(
    Variable,
    Regular
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"payMethod.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[PayMethod] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
