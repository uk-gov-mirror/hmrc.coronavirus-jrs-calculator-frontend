/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait PensionStatus

object PensionStatus extends Enumerable.Implicits {

  case object OptedIn extends WithName("optedIn") with PensionStatus
  case object OptedOut extends WithName("optedOut") with PensionStatus

  val values: Seq[PensionStatus] = Seq(
    OptedIn,
    OptedOut
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"pensionStatus.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[PensionStatus] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
