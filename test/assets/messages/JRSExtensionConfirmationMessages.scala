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

package messages

import models.Period
import play.api.i18n.Messages
import utils.ValueFormatter

object JRSExtensionConfirmationMessages extends ValueFormatter {

  val heading = "Claim amount for this employee"

  object ConfirmationBlock {
    def p1(percent: Int) = s"($percent% of their wages)"
    def p2(claimPeriod: Period)(implicit messages: Messages) =
      s"Claim period: ${dateToStringWithoutYear(claimPeriod.start)} to ${dateToString(claimPeriod.end)}"
    def claimAmount(amount: BigDecimal) = currencyFormatter(amount)
  }

  object AdditionalPaymentBlock {
    def p1(topup: BigDecimal) =
      s"You must pay this employee an additional ${currencyFormatter(topup)} to be eligible for this grant. This is because employees must be paid at least 80% of their wages."
    val p2 =
      "To be eligible for the grant you must pay employees at least 80% of their wages for the time they are furloughed. You can choose to pay more than this but do not have to."
    val stillPayNICandPension =
      "You cannot claim for employer National Insurance and pension contributions, but the employer must still pay these"
  }

  def dateAndCalculatorVersion(todaysDate: String, version: String) = s"Calculated on: $todaysDate (Calculator Version v$version)"

  val disclaimerTopPage = {
    "The results of the calculation rely on the accuracy of the information you entered, for which you are responsible. " +
      "You cannot claim for more money than you are going to pay out under the scheme."
  }

  val h2NextSteps = "Next steps"

  def nextStepsListMessages(messageNumber: Int, period: Period)(implicit messages: Messages): String =
    messageNumber match {
      case 1 => "Print or save a copy of this page for your records"
      case 2 => "Make a note of the amount you can claim for this employee and the claim period."
      case 3 =>
        s"Use the calculator again for any other employees furloughed within this claim period ${dateToStringWithoutYear(period.start)} " +
          s"to ${dateToString(period.`end`)} and make a note of the results."
      case 4 => "Add all the results for each employee furloughed in this claim period together to get the total amount you can claim."
      case 5 => "Make a claim through the Coronavirus Job Retention Scheme (opens in a new window or tab)."
      case _ => s"This number $messageNumber is not valid. Are you sure there are that many bullets?"
    }

  object RegularType1 {

    val h2BreakdownOfCalculations = "Breakdown of calculations"

    val breakDownParagraphOne
      : String = "You told us this employee gets paid a regular amount each time. We’ve worked out their daily earnings" +
      " and multiplied by the number of furlough days and furlough hours in each pay period. The furlough grant is 80% of this."

    val breakDownParagraphTwo
      : String = "There’s a maximum amount you can claim. If this affects your claim, we’ve adjusted the calculations. " +
      "Work out the maximum wage amount you can claim (opens in new tab)."

    val breakDownParagraphThree = "Calculations are rounded to the nearest penny unless otherwise stated."

    def h3PayPeriod(claimPeriod: Period)(implicit messages: Messages): String =
      s"For pay period ${dateToStringWithoutYear(claimPeriod.start)} to ${dateToString(claimPeriod.`end`)}"

    val h4CalculatePay = "Calculate the employee’s pay based on their furlough days"

    val h4ParagraphOne = "Take the pay in pay period:"

    def calculatePayListMessages(messageNumber: Int, pay: BigDecimal, daysInPeriod: Int, numberOfDaysFurloughed: Int): String =
      messageNumber match {
        case 1 => s"Start with ${currencyFormatter(pay)} (from pay period)."
        case 2 => s"Divide by $daysInPeriod (number of days in the pay period)."
        case 3 => s"Multiply by $numberOfDaysFurloughed (furlough days)."
        case _ => s"This number $messageNumber is not valid. Are you sure there are that many bullets?"
      }

    val h4ParagraphTwo: BigDecimal => String = (pay: BigDecimal) => s"Total pay based on furlough days = ${currencyFormatter(pay)}"

    val h4FurloughGrant = "Furlough grant"

    def furloughGrantListMessages(messageNumber: Int, pay: BigDecimal, generosityPercentage: BigDecimal): String =
      messageNumber match {
        case 1 => s"Take ${currencyFormatter(pay)} (pay based on furlough days)."
        case 2 => s"Multiply by $generosityPercentage%"
        case _ => s"This number $messageNumber is not valid. Are you sure there are that many bullets?"
      }

    val furloughGrantParagraphOne = (calculatedAmount: BigDecimal) => s"Calculated furlough grant = ${currencyFormatter(calculatedAmount)}"
    val furloughGrantParagraphTwo = (calculatedAmount: BigDecimal) =>
      s"The calculated furlough grant is more than the maximum furlough grant for this pay period. " +
        s"Because of this, you must use the maximum furlough grant, which is ${currencyFormatter(calculatedAmount)}"
    val maxFurloughGrantBullet: BigDecimal => String = (maxAmount: BigDecimal) => currencyFormatter(maxAmount)
    val furloughGrantParagraphThree                  = (calculatedAmount: BigDecimal) => s"Actual furlough grant = ${currencyFormatter(calculatedAmount)}"

    val furloughGrantIndent = (furloughPay: BigDecimal) => s"Total furlough grant for pay period = ${currencyFormatter(furloughPay)}"

    val disclaimerBottomPage = "The results of the calculation rely on the accuracy of the information you entered, for which you are responsible." +
      " You cannot claim for more money than you are going to pay out under the scheme."

    val printOrSave  = "Print or save a copy of this page"
    val webchatLink  = "Webchat help (opens in a new tab)."
    val feedbackLink = "What do you think of this service?"

    val startAnotherCalculation = "Start another calculation"
  }

  object Type3 {

    def method2BreadownSummary(boundaryEnd: String) =
      s"Method 2: we’ve worked out their average daily earnings in the last tax year, by dividing their total pay by the number of calendar days between 6 April 2019 and $boundaryEnd. Then we’ve multiplied that by the number of furlough days in each pay period."

    def statLeaveOnly(date1: String, date2: String) =
      s"You told us this employee was on statutory leave between $date1 and $date2. Because of this, for Method 2 we have to remove the number of days they were on statutory leave, and the amount they were paid for these periods from the calculation."

  }

  object Type4 {

    val oldCalculationBreakdownSummary =
      "You told us your employee gets paid a variable amount each time and has worked for you for less than 12 months. We’ve worked out their average daily earnings by dividing their total pay by the number of calendar days between 6 April 2019 and the day before furlough started (or 5 April 2020, whichever is earlier) Then we’ve multiplied that by the number of furlough days and furlough hours in each pay period. The furlough grant is 80% of this."

    def calculationBreakdownSummary(boundaryStart: String, boundaryEnd: String) =
      s"You told us your employee gets paid a variable amount each time and has worked for you for less than 12 months. We’ve worked out their average daily earnings by dividing their total pay by the number of calendar days between $boundaryStart and $boundaryEnd. Then we’ve multiplied that by the number of furlough days and furlough hours in each pay period. The furlough grant is 80% of this."

    val averageP1 =
      "Take the total pay from the employee’s start date (or 6 April 2019, if they started earlier than this date) to the day before the employee’s furlough start date (or 5 April 2020, whichever is earlier)."

    def statLeaveOnly(date: String) =
      s"You told us this employee was on statutory leave between the day their employment started and $date. Because of this, we have to remove the number of days they were on statutory leave, and the amount they were paid for these periods from the calculation."
  }

  object VariableExtensionType5 {

    val h2BreakdownOfCalculations = "Breakdown of calculations"

    def breakdownP1(boundaryStart: String, boundaryEnd: String) =
      "You told us your employee gets paid a variable amount each time and was not on your payroll " +
        "before 19 March 2020. We’ve worked out their average daily earnings by dividing their total pay by the number of calendar" +
        s" days between $boundaryStart and $boundaryEnd. Then, we’ve multiplied that by the number of furlough days and furlough hours in each pay period." +
        " The furlough grant is 80% of this."

    def statLeaveOnly(date1: Option[String], date2: String) =
      s"You told us this employee was on statutory leave between ${date1.getOrElse("the day their employment started")} and $date2. Because of this, we have to remove the number of days they were on statutory leave, and the amount they were paid for these periods from the calculation."

    val breakDownParagraphOne: String = "You told us your employee gets paid a variable amount each time and was not on your payroll " +
      "before 19 March 2020. We’ve worked out their average daily earnings by dividing their total pay by the number of calendar" +
      " days between 6 April 2020 (or the date their employment started, whichever is later) and the day before they were first" +
      " furloughed on or after 1 November 2020. Then, we’ve multiplied that by the number of furlough days and furlough hours in each pay period." +
      " The furlough grant is 80% of this."

    val breakDownParagraphTwo
      : String = "There’s a maximum amount you can claim. If this affects your claim, we’ve adjusted the calculations. " +
      "Work out the maximum wage amount you can claim (opens in new tab)."

    val breakDownParagraphThree = "Calculations are rounded to the nearest penny unless otherwise stated."

    def h3PayPeriod(claimPeriod: Period)(implicit messages: Messages): String =
      s"For pay period ${dateToStringWithoutYear(claimPeriod.start)} to ${dateToString(claimPeriod.`end`)}"

    val h4CalculatePay = "Calculate the employee’s pay based on their furlough days"

    val h4ParagraphTwo: BigDecimal => String = (pay: BigDecimal) => s"Total pay based on furlough days = ${currencyFormatter(pay)}"

    val h4FurloughGrant = "Furlough grant"

    def furloughGrantListMessages(messageNumber: Int, pay: BigDecimal, generosityPercentage: BigDecimal): String =
      messageNumber match {
        case 1 => s"Take ${currencyFormatter(pay)} (pay based on furlough days)."
        case 2 => s"Multiply by $generosityPercentage%"
        case _ => s"This number $messageNumber is not valid. Are you sure there are that many bullets?"
      }

    val furloughGrantParagraphOne = (calculatedAmount: BigDecimal) => s"Calculated furlough grant = ${currencyFormatter(calculatedAmount)}"
    val furloughGrantParagraphTwo = (calculatedAmount: BigDecimal) =>
      s"The calculated furlough grant is more than the maximum furlough grant for this pay period. " +
        s"Because of this, you must use the maximum furlough grant, which is ${currencyFormatter(calculatedAmount)}"
    val maxFurloughGrantBullet: BigDecimal => String = (maxAmount: BigDecimal) => currencyFormatter(maxAmount)
    val furloughGrantParagraphThree                  = (calculatedAmount: BigDecimal) => s"Actual furlough grant = ${currencyFormatter(calculatedAmount)}"

    val furloughGrantIndent = (furloughPay: BigDecimal) => s"Total furlough grant for pay period = ${currencyFormatter(furloughPay)}"

    val disclaimerBottomPage = "The results of the calculation rely on the accuracy of the information you entered, for which you are responsible." +
      " You cannot claim for more money than you are going to pay out under the scheme."

    val printOrSave  = "Print or save a copy of this page"
    val webchatLink  = "Webchat help (opens in a new tab)."
    val feedbackLink = "What do you think of this service?"

    val startAnotherCalculation = "Start another calculation"
  }

}
