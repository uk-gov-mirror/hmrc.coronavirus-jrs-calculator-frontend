/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FurloughTopUpStatus

object FurloughTopUpStatus extends Enumerable.Implicits {

  case object ToppedUp extends WithName("toppedUp") with FurloughTopUpStatus
  case object NotToppedUp extends WithName("notToppedUp") with FurloughTopUpStatus

  val values: Seq[FurloughTopUpStatus] = Seq(
    ToppedUp,
    NotToppedUp
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"furloughTopUp.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[FurloughTopUpStatus] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
