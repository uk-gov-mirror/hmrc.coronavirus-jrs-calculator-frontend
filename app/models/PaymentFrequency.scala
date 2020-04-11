/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.label.Label
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait PaymentFrequency

object PaymentFrequency extends Enumerable.Implicits {

  case object Weekly extends WithName("weekly") with PaymentFrequency
  case object FortNightly extends WithName("fortnightly") with PaymentFrequency
  case object FourWeekly extends WithName("fourweekly") with PaymentFrequency
  case object Monthly extends WithName("monthly") with PaymentFrequency

  val values: Set[PaymentFrequency] = Set(
    Weekly,
    FortNightly,
    FourWeekly,
    Monthly
  )

  val items: Seq[RadioItem] = values.toSeq.map { value =>
    RadioItem(
      content = Text(value.toString),
      id = Some("frequency"),
      value = Some(value.toString),
      label = Some(Label(attributes = Map("id" -> "frequency"))))
  }

  implicit val enumerable: Enumerable[PaymentFrequency] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
