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

  val heading = "What you can claim for this employee"

  val dateAndCalculatorVersion = "Calculated on: 18 February 2021 (Calculator Version v2)"
  val indent = "You cannot claim for employer National Insurance and pension contributions, but the employer must still pay these"

  val disclaimerTopPage = {
    "The results of the calculation rely on the accuracy of the information you entered, for which you are responsible. " +
      "You cannot claim for more money than you are going to pay out under the scheme."
  }

  def nextStepsListMessages(messageNumber: Int, period: Period)(implicit messages: Messages): String =
    messageNumber match {
      case 1 => "Print or save a copy of this page for your records"
      case 2 => "Make a note of the amount you can claim or this employee and the claim period."
      case 3 =>
        s"Use the calculator again for any other employees furloughed within this claim period ${dateToStringWithoutYear(period.start)} " +
          s"to ${dateToString(period.`end`)} and make a note of the results."
      case 4 => "Add all the results for each employee furloughed in this claim period together to get the total amount you can claim."
      case 5 => "Make a claim through the Job Retention Scheme online claim service (opens in a new window or tab)."
      case _ => s"This number $messageNumber is not valid. Are you sure there are that many bullets?"
    }

  val h2NextSteps = "Next steps"
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

  val h4CalculatePay = "Calculate pay based on furlough days"

  val h4ParagraphOne = "Take the pay in pay period:"

  def calculatePayListMessages(messageNumber: Int, pay: BigDecimal, daysInPeriod: Int, numberOfDaysFurloughed: Int)(
    implicit messages: Messages): String =
    messageNumber match {
      case 1 => s"Start with ${currencyFormatter(pay)} (from pay period)."
      case 2 => s"Divide by $daysInPeriod (days in pay period)."
      case 3 => s"Multiply by $numberOfDaysFurloughed (furlough days)."
      case _ => s"This number $messageNumber is not valid. Are you sure there are that many bullets?"
    }

  val h4ParagraphTwo: BigDecimal => String = (pay: BigDecimal) => s"Total pay based on furlough days = ${currencyFormatter(pay)}"

  val h4FurloughGrant = "Furlough grant"

  def furloughGrantListMessages(messageNumber: Int, pay: BigDecimal, generosityPercentage: BigDecimal)(
    implicit messages: Messages): String =
    messageNumber match {
      case 1 => s"Take ${currencyFormatter(pay)} (pay based on furlough days)."
      case 2 => s"Multiply by $generosityPercentage%"
      case _ => s"This number $messageNumber is not valid. Are you sure there are that many bullets?"
    }

  val furloughGrantParagraphOne = (calculatedAmount: BigDecimal) => s"Calculated furlough grant = ${currencyFormatter(calculatedAmount)}"
  val furloughGrantParagraphTwo = "This exceeds the maximum furlough grant for this pay period, which is:"
  val maxFurloughGrantBullet: BigDecimal => String = (maxAmount: BigDecimal) => currencyFormatter(maxAmount)
  val furloughGrantParagraphThree = "Therefore we use the maximum furlough grant amount."

  val furloughGrantIndent = (furloughPay: BigDecimal) => s"Total furlough grant for pay period = ${currencyFormatter(furloughPay)}"

  val disclaimerBottomPage = "The results of the calculation rely on the accuracy of the information you entered, for which you are responsible." +
    " You cannot claim for more money than you are going to pay out under the scheme."

  val printOrSave = "Print or save a copy of this page"
  val webchatLink = "Webchat help (opens in a new tab)."
  val feedbackLink = "What do you think of this service?"

  val startAnotherCalculation = "Start another calculation"

}
