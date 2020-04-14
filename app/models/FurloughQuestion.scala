/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FurloughQuestion

object FurloughQuestion extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with FurloughQuestion
  case object No extends WithName("no") with FurloughQuestion

  val values: Seq[FurloughQuestion] = Seq(
    Yes,
    No
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"furloughQuestion.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[FurloughQuestion] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
