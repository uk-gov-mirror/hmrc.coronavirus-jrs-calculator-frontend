/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models
import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait FrequencyWithPreviousYearDaysCount extends PaymentFrequency

sealed trait PaymentFrequency

object PaymentFrequency extends Enumerable.Implicits {

  case object Weekly extends WithName("weekly") with FrequencyWithPreviousYearDaysCount
  case object FortNightly extends WithName("fortnightly") with FrequencyWithPreviousYearDaysCount
  case object FourWeekly extends WithName("fourweekly") with FrequencyWithPreviousYearDaysCount
  case object Monthly extends WithName("monthly") with PaymentFrequency

  val values: Set[PaymentFrequency] = Set(
    Weekly,
    FortNightly,
    FourWeekly,
    Monthly
  )

  def options(form: Form[_])(implicit messages: Messages): Seq[RadioItem] = values.toSeq.map { value =>
    RadioItem(
      value = Some(value.toString),
      content = Text(messages(s"payFrequency.${value.toString}")),
      checked = form("value").value.contains(value.toString)
    )
  }

  implicit val enumerable: Enumerable[PaymentFrequency] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
