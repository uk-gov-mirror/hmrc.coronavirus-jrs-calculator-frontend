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

package assets.messages

object StartPageMessages {

  val heading = "Job Retention Scheme calculator"

  object WhatYouCanClaim {
    val h2 = "What you can claim"
    val p1 =
      "The Coronavirus Job Retention Scheme has been extended until 30 September 2021. For claim periods between 1 November 2020 and 30 April 2021, you can claim 80% of an employee’s usual salary for hours not worked, up to a maximum of £2,500 per month."
    val p2 = "From 1 July 2021, the amount you can claim for is changing:"

    object Table {
      val column1Heading = "Claim month"
      val column2Heading = "Government contribution: wages for hours not worked"
      val column3Heading = "Employer contribution: wages for hours not worked"
      val naughtPercent  = "0%"
      val mayJunGovt     = "80% up to £2,500"
      val julGovt        = "70% up to £2,187.50"
      val augSepGovt     = "60% up to £1,875"
      val julyEmp        = "10% up to £312.50"
      val augSepEmp      = "20% up to £625"
    }

    val p3 =
      "To be eligible for the grant you must continue to pay your furloughed employees at least 80% of their wages for the time they are on furlough. You can choose to pay them more than this, but you do not have to."
    val p4 = "You cannot claim for employer National Insurance and pension contributions, but the employer must still pay these."
  }

  object AboutCalculator {
    val h2 = "About the calculator"
    val p1 = "Use this calculator to work out the figures you will need when you make a Coronavirus Job Retention Scheme claim."
    val p2 =
      "You will need to use the calculator for each employee on full or flexible furlough and add up the results of each claim amount for the claim period. For your records, the calculator will also break down the calculations for each pay period."

    object UseItFor {
      val h3      = "You can use this calculator for:"
      val bullet1 = "employees who are fully furloughed and therefore not working any hours"
      val bullet2 = "employees brought back to work for some of their normal hours from 1 July"
      val bullet3 = "most employees who are paid weekly, two weekly, four weekly or monthly in fixed pay periods"
      val bullet4 = "employees who have returned from statutory leave from 1 August 2020 onwards, for example, maternity leave"
    }

    object CannotBeUsedFor {
      val h3      = "The calculator cannot be used for employees if they:"
      val bullet1 = "started a notice period or went back off a notice period in the same claim period on or after 1 December 2020"
      val bullet2 = "have an annual pay period"
      val bullet3 = "have been transferred under The Transfer of Undertakings Protection of Employment (TUPE)"
      val bullet4 = "were not employed continuously before their furlough started"
      val bullet5 =
        "returned from statutory leave such as maternity leave in the last 3 months (if the claim period is in July 2020 or earlier)"
      val bullet6 = "receive employer pension contributions outside of an auto-enrolment pension scheme"
      val bullet7 = "ended furlough then began again during the same claim period"
      val bullet8 =
        "were variably paid, and have been on more than one period of furlough where any part of any of the periods of furlough was in the 2019/20 tax year"
      val bullet9 =
        "have variable pay, started employment before 6 April 2020 and were not on their employer’s payroll on or before 19 March 2020"
      val bullet10 =
        "started employment with their employer during a calendar period in the 2019-20 tax year which corresponds with part or all of the period being claimed for"
      val bullet11 = "are on fixed pay and have had a change in payment frequency, for example from monthly pay to weekly pay"
      val p1       = "In these cases, you must work out what you can claim manually using the calculation guidance or seek professional advice."
      val p2       = "It is your responsibility to check that the amount you’re claiming for is correct."
    }

  }

  object BeforeYouStart {
    val h2      = "Before you start"
    val p1      = "You will need:"
    val bullet1 = "claim start date (for your first claim, this is when the first employee started furlough)"
    val bullet2 = "claim end date"
    val bullet3 = "pay dates (when the employee gets their pay)"
    val bullet4 = "dates of pay periods (the time periods that their pay covers)"
    val bullet5 = "regular payment amounts"
    val bullet6 = "additional payments (such as tips, discretionary bonuses, non-cash payments)"
    val bullet7 = "date furlough ended, if it is not ongoing"
    val p2      = "From 1 July, if the employee is flexibly furloughed, you will also need:"
    object JulyOnwardBullets {
      val bullet1 = "employee’s usual hours"
      val bullet2 = "actual hours worked"
      val bullet3 = "hours furloughed"
    }
    val readMore = "Read more about steps to take before calculating your claim"
  }

  val startButton = "Start now"
}
