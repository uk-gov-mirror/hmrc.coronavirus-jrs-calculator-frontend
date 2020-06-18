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

package handlers

import java.time.LocalDate

import base.SpecBase
import cats.scalatest.ValidatedValues
import models.PayMethod.{Regular, Variable}
import models.{Amount, AnnualPayAmount, BranchingQuestions, EmployeeStarted, LastYearPayment, NonFurloughPay, RegularPay, RegularPayData, Salary, UserAnswers, VariablePay, VariablePayData, VariablePayWithCylb, VariablePayWithCylbData}
import pages._
import utils.CoreTestData

class JourneyBuilderSpec extends SpecBase with CoreTestData with ValidatedValues {

  "return regular journey if pay question is Regularly" in new JourneyBuilder {
    val questions = BranchingQuestions(Regular, None, None)

    define(questions, LocalDate.of(2019, 4, 6)) mustBe RegularPay
  }

  "return variable journey if pay question is Varies and no Cylb eligible" in new JourneyBuilder {
    val questions = BranchingQuestions(Variable, Some(EmployeeStarted.After1Feb2019), Some(LocalDate.of(2019, 4, 6)))

    define(questions, LocalDate.of(2019, 4, 6)) mustBe VariablePay
  }

  "return variable journey if pay question is Variable and Cylb eligible" in new JourneyBuilder {
    val questionsOne = BranchingQuestions(Variable, Some(EmployeeStarted.OnOrBefore1Feb2019), None)
    val questionsTwo = BranchingQuestions(Variable, Some(EmployeeStarted.After1Feb2019), Some(LocalDate.of(2019, 4, 5)))

    define(questionsOne, LocalDate.of(2019, 4, 6)) mustBe VariablePayWithCylb
    define(questionsTwo, LocalDate.of(2019, 4, 6)) mustBe VariablePayWithCylb
  }

  "build a RegularPayData for a RegularPay journey" in new JourneyBuilder {
    val answers: UserAnswers = mandatoryAnswersOnRegularMonthly
      .set(RegularPayAmountPage, Salary(1000.0))
      .get

    val expected: RegularPayData = RegularPayData(defaultReferencePayData, Amount(1000.0))

    journeyDataV(RegularPay, answers).value mustBe expected
  }

  "build a VariablePayData for a VariablePay journey where CYLB is not required" in new JourneyBuilder {
    val answers: UserAnswers = mandatoryAnswersOnRegularMonthly
      .set(AnnualPayAmountPage, AnnualPayAmount(1000.0))
      .get
      .set(PayMethodPage, Variable)
      .get
      .set(EmployeeStartDatePage, LocalDate.of(2019, 12, 1))
      .get

    val expected: VariablePayData = VariablePayData(
      defaultReferencePayData,
      Amount(1000.0),
      NonFurloughPay(None, None),
      period("2019-12-01", "2020-02-29")
    )

    journeyDataV(VariablePay, answers).value mustBe expected
  }

  "build a VariablePayData for a VariablePay journey where CYLB is required" in new JourneyBuilder {
    val answers: UserAnswers = mandatoryAnswersOnRegularMonthly
      .set(AnnualPayAmountPage, AnnualPayAmount(1000.0))
      .get
      .set(PayMethodPage, Variable)
      .get
      .set(EmployeeStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
      .get
      .setListItemWithInvalidation(LastYearPayPage, LastYearPayment(LocalDate.of(2019, 3, 31), Amount(1200.0)), 1)
      .get

    val expected: VariablePayWithCylbData = VariablePayWithCylbData(
      defaultReferencePayData,
      Amount(1000.0),
      NonFurloughPay(None, None),
      period("2019-04-06", "2020-02-29"),
      Seq(LastYearPayment(LocalDate.of(2019, 3, 31), Amount(1200.0)))
    )

    journeyDataV(VariablePayWithCylb, answers).value mustBe expected
  }
}
