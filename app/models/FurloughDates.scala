/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FurloughDates

object FurloughDates extends Enumerable.Implicits {

  case object StartedInClaim extends WithName("startedInClaim") with FurloughDates
  case object EndedInClaim extends WithName("endedInClaim") with FurloughDates
  case object StartedAndEndedInClaim extends WithName("startedAndEndedInClaim") with FurloughDates

  val values: Seq[FurloughDates] = Seq(
    StartedInClaim,
    EndedInClaim,
    StartedAndEndedInClaim
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"furloughDates.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[FurloughDates] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
