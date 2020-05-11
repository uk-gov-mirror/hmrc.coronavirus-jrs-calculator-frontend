/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package viewmodels

import controllers.routes
import models.{CheckMode, FurloughEnded, FurloughOngoing, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import services.FurloughPeriodExtractor
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, SummaryListRow}
import views.ViewUtils._

class CheckYourAnswersRowHelper(val userAnswers: UserAnswers)(implicit val messages: Messages)
    extends SummaryListRowHelper with FurloughPeriodExtractor {

  def rows: Option[Seq[SummaryListRow]] =
    for {
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
      furloughRow      <- furloughRow(userAnswers)
    } yield {
      Seq(
        summaryListRow(
          label = messages("claimPeriodStartAndEnd.checkYourAnswersLabel"),
          value = messages("claimPeriodStartAndEnd.checkYourAnswersValue", dateToString(claimPeriodStart), dateToString(claimPeriodEnd)),
          actions = Some(
            Actions(items = Seq(ActionItem(
              href = routes.ClaimPeriodStartController.onPageLoad().url,
              content = Text(messages("site.edit")),
              visuallyHiddenText = Some(messages("claimPeriodStartAndEnd.change.hidden"))
            ))))
        ),
        furloughRow
      )
    }

  private def furloughRow(userAnswers: UserAnswers): Option[SummaryListRow] =
    for {
      furloughPeriod <- extractFurloughPeriod(userAnswers)
    } yield {
      val value = furloughPeriod match {
        case FurloughOngoing(start)    => messages("furloughPeriod.ongoing.checkYourAnswersValue", dateToString(start))
        case FurloughEnded(start, end) => messages("furloughPeriod.ended.checkYourAnswersValue", dateToString(start), dateToString(end))
      }

      summaryListRow(
        label = messages("furloughPeriod.checkYourAnswersLabel"),
        value = value,
        actions = Some(
          Actions(items = Seq(ActionItem(
            href = routes.FurloughStartDateController.onPageLoad().url,
            content = Text(messages("site.edit")),
            visuallyHiddenText = Some(messages("furloughPeriod.change.hidden"))
          ))))
      )
    }

}
