/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import cats.data.Validated.Valid
import models.EmployeeStarted.OnOrBefore1Feb2019
import models.UserAnswers
import models.UserAnswers.AnswerV
import pages.{AnnualPayAmountPage, ClaimPeriodEndPage, ClaimPeriodStartPage, EmployeeRTISubmissionPage,
  EmployeeStartDatePage, EmployeeStartedPage, FurloughEndDatePage, FurloughInLastTaxYearPage, FurloughStartDatePage,
  FurloughStatusPage, LastYearPayPage, PartTimeHoursPage, PartTimeNormalHoursPage, PartTimePeriodsPage, PartTimeQuestionPage,
  PayDatePage, PayMethodPage, PayPeriodsListPage, PaymentFrequencyPage, RegularLengthEmployedPage, RegularPayAmountPage}
import viewmodels.{ConfirmationDataResult, ConfirmationDataResultWithoutNicAndPension}

import scala.util.matching.Regex

/**
 * For integration testing purposes only.
 */

//scalastyle:off
object ConfirmationTestCasesUtil {

  var testCases: Seq[String] = Seq()

  def printOutConfirmationTestCases(userAnswers: UserAnswers, result: AnswerV[ConfirmationDataResult], numberOfCases: Int = 10): String = {

    val date: Regex = """(\d{4})-(\d{2})-(\d{2})""".r

    val periodDate: Regex = """Period\("(\d{4})-(\d{2})-(\d{2})"""".r
    val periodDateSecond: Regex = """.toLocalDate,"(\d{4})-(\d{2})-(\d{2})"""".r
    val usualHoursRegex: Regex = """UsualHours\("(\d{4})-(\d{2})-(\d{2})"""".r
    val partTimeHoursRegex: Regex = """PartTimeHours\("(\d{4})-(\d{2})-(\d{2})"""".r

    val text =s"""emptyUserAnswers
                 |      ${userAnswers.getO(EmployeeRTISubmissionPage).flatMap(x => x.toOption.map(x => ".withRtiSubmission(EmployeeRTISubmission." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(FurloughStatusPage).flatMap(x => x.toOption.map(x => ".withFurloughStatus(FurloughStatus." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(EmployeeStartDatePage).flatMap(x => x.toOption.map(x => ".withEmployeeStartDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(FurloughEndDatePage).flatMap(x => x.toOption.map(x => ".withFurloughEndDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(PaymentFrequencyPage).flatMap(x => x.toOption.map(x => ".withPaymentFrequency(" + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(EmployeeStartedPage).map { x =>
      if (x.exists(_.equals(OnOrBefore1Feb2019))) {
        ".withEmployeeStartedOnOrBefore1Feb2019(" + x.getClass.getSimpleName
          .replace("Valid(", "")
          .replace("Valid", "")
          .replace("$", "") + ")"
      } else {
        ".withEmployeeStartedAfter1Feb2019(" + x.getClass.getSimpleName
          .replace("Valid(", "")
          .replace("Valid", "")
          .replace("$", "") + ")"
      }
    }.getOrElse("")}
                 |      ${userAnswers.getO(ClaimPeriodStartPage).flatMap(x => x.toOption.map(x => ".withClaimPeriodStart(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${".withLastYear(" + userAnswers.getList(LastYearPayPage).toString.replace("Valid(", "").replaceAll("\\)\\), LastYearPayment\\(",",").replaceAll("LastYearPayment\\(","").replaceAll(",Amount\\("," -> ") + ")"}
                 |      ${userAnswers.getO(FurloughInLastTaxYearPage).flatMap(x => x.toOption.map(x => ".withFurloughInLastTaxYear(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(PayPeriodsListPage).flatMap(x => x.toOption.map(x => ".withPayPeriodsList(PayPeriodsList." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(PartTimePeriodsPage).flatMap(x => x.toOption.map(x => ".withPartTimePeriods(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(PayMethodPage).flatMap(x => x.toOption.map(x => ".withPayMethod(PayMethod." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(PartTimeQuestionPage).flatMap(x => x.toOption.map(x => ".withPartTimeQuestion(PartTimeQuestion." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(AnnualPayAmountPage).flatMap(x => x.toOption.map(x => ".withAnnualPayAmount(" + x.amount.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(RegularPayAmountPage).flatMap(x => x.toOption.map(x => ".withRegularPayAmount(" + x.amount.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(FurloughStartDatePage).flatMap(x => x.toOption.map(x => ".withFurloughStartDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(ClaimPeriodEndPage).flatMap(x => x.toOption.map(x => ".withClaimPeriodEnd(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${userAnswers.getO(RegularLengthEmployedPage).flatMap(x => x.toOption.map(x => ".withRegularLengthEmployed(RegularLengthEmployed." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
                 |      ${".withPayDate(" + userAnswers.getList(PayDatePage).toString.replace("Valid(", "") + ")"}
                 |      ${userAnswers.getO(LastYearPayPage).flatMap(x => x.toOption.map(x => ".withPayDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
                 |      ${".withUsualHours(" + userAnswers.getList(PartTimeNormalHoursPage).toString.replace("Valid(", "") + ")"}
                 |      ${".withPartTimeHours(" + userAnswers.getList(PartTimeHoursPage).toString.replace("Valid(", "") + ")"}
                 |""".stripMargin.replaceAll("\n\n", "\n")

    val textWithOutcome = text + "\n -> " + result.asInstanceOf[Valid[ConfirmationDataResultWithoutNicAndPension]].toOption.get.confirmationViewBreakdown.furlough.total.formatted("%.2f")

    def addLocalDate(s: Regex.Match) = s"$s.toLocalDate"

    val finalResult = partTimeHoursRegex.replaceAllIn(
      usualHoursRegex.replaceAllIn(
        periodDateSecond.replaceAllIn(
          periodDate.replaceAllIn(
            date.replaceAllIn(textWithOutcome, _ match {
              case date(y, m, d) => f""""$y-$m-$d""""
            }), _ match {
              case periodDate => addLocalDate(periodDate)
            }), _ match {
            case secondPeriodDate => addLocalDate(secondPeriodDate)
          }), _ match {
          case usualHours => addLocalDate(usualHours)
        }), _ match {
        case partTimeHours => addLocalDate(partTimeHours)
      })

    testCases = testCases ++ Seq(finalResult)

    if(testCases.length >= numberOfCases){
      println("Test cases done. See Outcome.")
      println("#############################")
      println(testCases.mkString(","))
      Thread.sleep(30000)
      println("#############################")
      testCases = Seq()
    }

    finalResult
  }
}
