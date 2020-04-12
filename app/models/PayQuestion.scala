/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait PayQuestion

object PayQuestion extends Enumerable.Implicits {

  case object Regularly extends WithName("regularly") with PayQuestion
  case object Varies extends WithName("varies") with PayQuestion

  val values: Seq[PayQuestion] = Seq(
    Regularly,
    Varies
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"payQuestion.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[PayQuestion] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
