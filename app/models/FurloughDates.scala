/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import java.time.LocalDate

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FurloughDates

object FurloughDates extends Enumerable.Implicits {
  val values: Seq[FurloughDates] = Seq(
    StartedInClaim,
    EndedInClaim,
    StartedAndEndedInClaim
  )

  def options(form: Form[_], claimStartDate: String, claimEndDate: String)(implicit messages: Messages): Seq[RadioItem] =
    Seq(
      RadioItem(
        value = Some(StartedInClaim.toString),
        content = Text(messages(s"furloughDates.${StartedInClaim.toString}", claimStartDate)),
        checked = form("value").value.contains(StartedInClaim.toString)
      ),
      RadioItem(
        value = Some(EndedInClaim.toString),
        content = Text(messages(s"furloughDates.${EndedInClaim.toString}", claimEndDate)),
        checked = form("value").value.contains(EndedInClaim.toString)
      ),
      RadioItem(
        value = Some(StartedAndEndedInClaim.toString),
        content = Text(messages(s"furloughDates.${StartedAndEndedInClaim.toString}", claimStartDate, claimEndDate)),
        checked = form("value").value.contains(StartedAndEndedInClaim.toString)
      )
    )

  case object StartedInClaim extends WithName("startedInClaim") with FurloughDates

  case object EndedInClaim extends WithName("endedInClaim") with FurloughDates

  case object StartedAndEndedInClaim extends WithName("startedAndEndedInClaim") with FurloughDates

  implicit val enumerable: Enumerable[FurloughDates] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
