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

  case object DoesContribute extends WithName("doesContribute") with PensionStatus
  case object DoesNotContribute extends WithName("doesNotContribute") with PensionStatus

  val values: Seq[PensionStatus] = Seq(
    DoesContribute,
    DoesNotContribute
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"pensionContribution.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[PensionStatus] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
