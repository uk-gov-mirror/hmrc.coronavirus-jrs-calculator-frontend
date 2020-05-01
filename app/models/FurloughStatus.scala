/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FurloughStatus

object FurloughStatus extends Enumerable.Implicits {

  case object FurloughEnded extends WithName("ended") with FurloughStatus
  case object FurloughOngoing extends WithName("ongoing") with FurloughStatus

  val values: Seq[FurloughStatus] = Seq(
    FurloughEnded,
    FurloughOngoing
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"furloughOngoing.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[FurloughStatus] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
