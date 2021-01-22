package assets.constants

import models.PaymentFrequency.{FortNightly, FourWeekly, Monthly, Weekly}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

object PaymentFrequencyConstants {

  def allRadioOptions(checked: Boolean = false)(implicit messages: Messages) = Seq(
    RadioItem(
      value = Some(Weekly.toString),
      content = Text(messages(s"selectClaimPeriod.${Weekly.toString}")),
      checked = checked,
      id = Some(Weekly.toString)
    ),
    RadioItem(
      value = Some(FortNightly.toString),
      content = Text(messages(s"selectClaimPeriod.${FortNightly.toString}")),
      checked = checked,
      id = Some(FortNightly.toString)
    ),
    RadioItem(
      value = Some(FourWeekly.toString),
      content = Text(messages(s"selectClaimPeriod.${FourWeekly.toString}")),
      checked = checked,
      id = Some(FourWeekly.toString)
    ),
    RadioItem(
      value = Some(Monthly.toString),
      content = Text(messages(s"selectClaimPeriod.${Monthly.toString}")),
      checked = checked,
      id = Some(Monthly.toString)
    )
  )

}
