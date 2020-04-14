/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait NicCategory

object NicCategory extends Enumerable.Implicits {

  case object Payable extends WithName("payable") with NicCategory
  case object Nonpayable extends WithName("nonPayable") with NicCategory

  val values: Seq[NicCategory] = Seq(
    Payable,
    Nonpayable
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"nicCategory.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[NicCategory] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
