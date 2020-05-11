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
import models.{CheckMode, FurloughEnded, FurloughOngoing, PaymentFrequency, PeriodWithPaymentDate, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import services.{FurloughPeriodExtractor, PeriodHelper}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, SummaryListRow}
import views.ViewUtils._

class CheckYourAnswersRowHelper(val userAnswers: UserAnswers)(implicit val messages: Messages)
    extends SummaryListRowHelper with FurloughPeriodExtractor with PeriodHelper {

  //noinspection ScalaStyle
  def rows: Option[Seq[SummaryListRow]] =
    for {
      claimPeriodStart <- userAnswers.get(ClaimPeriodStartPage)
      claimPeriodEnd   <- userAnswers.get(ClaimPeriodEndPage)
      payFrequency     <- userAnswers.get(PaymentFrequencyPage)
      payMethod        <- userAnswers.get(PayMethodPage)
      nicCategory      <- userAnswers.get(NicCategoryPage)
      pensionStatus    <- userAnswers.get(PensionStatusPage)
    } yield {
      Seq(
        Some(
          summaryListRow(
            label = messages("claimPeriodStartAndEnd.checkYourAnswersLabel"),
            value = messages("claimPeriodStartAndEnd.checkYourAnswersValue", dateToString(claimPeriodStart), dateToString(claimPeriodEnd)),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.ClaimPeriodStartController.onPageLoad().url,
              content = Text(messages("site.edit")),
              visuallyHiddenText = Some(messages("claimPeriodStartAndEnd.change.hidden"))
            ))))
          )),
        furloughRow(userAnswers),
        Some(
          summaryListRow(
            label = messages("payFrequency.checkYourAnswersLabel"),
            value = messages(s"payFrequency.${payFrequency.toString}"),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.PaymentFrequencyController.onPageLoad().url,
              content = Text(messages("site.edit")),
              visuallyHiddenText = Some(messages("payFrequency.change.hidden"))
            ))))
          )),
        Some(
          summaryListRow(
            label = messages("payMethod.checkYourAnswersLabel"),
            value = messages(s"payMethod.${payMethod.toString}"),
            actions = Some(
              Actions(
                items = Seq(
                  ActionItem(
                    href = routes.PayMethodController.onPageLoad().url,
                    content = Text(messages("site.edit")),
                    visuallyHiddenText = Some(messages("payMethod.change.hidden"))
                  ))))
          )),
        salaryRow(userAnswers, payFrequency),
        payPeriodsRow(userAnswers),
        Some(
          summaryListRow(
            label = messages("nicCategory.checkYourAnswersLabel"),
            value = messages(s"nicCategory.${nicCategory.toString}"),
            actions = Some(
              Actions(
                items = Seq(
                  ActionItem(
                    href = routes.NicCategoryController.onPageLoad().url,
                    content = Text(messages("site.edit")),
                    visuallyHiddenText = Some(messages("nicCategory.change.hidden"))
                  ))))
          )),
        Some(
          summaryListRow(
            label = messages("pensionContribution.checkYourAnswersLabel"),
            value = messages(s"pensionContribution.${pensionStatus.toString}"),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.PensionContributionController.onPageLoad().url,
              content = Text(messages("site.edit")),
              visuallyHiddenText = Some(messages("pensionContribution.change.hidden"))
            ))))
          ))
      ).flatten
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

  private def salaryRow(userAnswers: UserAnswers, payFrequency: PaymentFrequency): Option[SummaryListRow] =
    userAnswers
      .get(SalaryQuestionPage)
      .map(
        salary =>
          summaryListRow(
            label = messages("salaryQuestion.checkYourAnswersLabel"),
            value =
              messages("salaryQuestion.checkYourAnswersValue", salary.amount.formatted("%.2f"), messages(s"payFrequency.$payFrequency")),
            actions = Some(Actions(items = Seq(ActionItem(
              href = routes.SalaryQuestionController.onPageLoad().url,
              content = Text(messages("site.edit")),
              visuallyHiddenText = Some(messages("salaryQuestion.change.hidden"))
            ))))
        ))

  private def payPeriodsRow(userAnswers: UserAnswers): Option[SummaryListRow] =
    for {
      furloughPeriod <- extractFurloughWithinClaim(userAnswers)
      payDates = userAnswers.getList(PayDatePage)
      periods = generatePeriods(payDates, furloughPeriod)
      frequency  <- userAnswers.get(PaymentFrequencyPage)
      lastPayDay <- userAnswers.get(LastPayDatePage)
      periodsWithDate = assignPayDates(frequency, periods, lastPayDay)
    } yield {
      val values = buildPaymentList(periodsWithDate, Seq.empty).mkString("<br/>")

      summaryListRow(
        label = messages("payDate.checkYourAnswersLabel"),
        value = values,
        actions = Some(
          Actions(
            items = Seq(
              ActionItem(
                href = routes.PayDateController.onPageLoad(1).url,
                content = Text(messages("site.edit")),
                visuallyHiddenText = Some(messages("payDate.change.hidden"))
              ))))
      )
    }

  @scala.annotation.tailrec
  private def buildPaymentList(periods: Seq[PeriodWithPaymentDate], result: Seq[String]): Seq[String] =
    periods match {
      case Nil => result
      case h :: Nil =>
        result :+ messages(
          "payDate.checkYourAnswersValue.last",
          dateToStringWithoutYear(h.period.period.start),
          dateToStringWithoutYear(h.period.period.end),
          dateToStringWithoutYear(h.paymentDate.value)
        )
      case h :: t =>
        val value = messages(
          "payDate.checkYourAnswersValue",
          dateToStringWithoutYear(h.period.period.start),
          dateToStringWithoutYear(h.period.period.end)
        )
        buildPaymentList(t, result :+ value)
    }

}
