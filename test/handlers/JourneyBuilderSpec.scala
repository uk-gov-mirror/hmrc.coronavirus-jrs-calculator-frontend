/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.NicCategory.Payable
import models.PayMethod.{Regular, Variable}
import models.PaymentFrequency.Monthly
import models.{Amount, BranchingQuestions, CylbPayment, EmployeeStarted, FurloughWithinClaim, JourneyCoreData, NonFurloughPay, PensionStatus, PeriodWithPaymentDate, RegularPay, RegularPayData, Salary, UserAnswers, VariableGrossPay, VariablePay, VariablePayData, VariablePayWithCylb, VariablePayWithCylbData}
import pages.{EmployedStartedPage, EmployeeStartDatePage, LastYearPayPage, PayMethodPage, SalaryQuestionPage, VariableGrossPayPage}

class JourneyBuilderSpec extends SpecBase with CoreTestDataBuilder {

  "return regular journey if pay question is Regularly" in new JourneyBuilder {
    val questions = BranchingQuestions(Regular, None, None)

    define(questions) mustBe RegularPay
  }

  "return variable journey if pay question is Varies and no Cylb eligible" in new JourneyBuilder {
    val questions = BranchingQuestions(Variable, Some(EmployeeStarted.After1Feb2019), Some(LocalDate.of(2019, 4, 6)))

    define(questions) mustBe VariablePay
  }

  "return variable journey if pay question is Variable and Cylb eligible" in new JourneyBuilder {
    val questionsOne = BranchingQuestions(Variable, Some(EmployeeStarted.OnOrBefore1Feb2019), None)
    val questionsTwo = BranchingQuestions(Variable, Some(EmployeeStarted.After1Feb2019), Some(LocalDate.of(2019, 4, 5)))

    define(questionsOne) mustBe VariablePayWithCylb
    define(questionsTwo) mustBe VariablePayWithCylb
  }

  "build a RegularPayData for a RegularPay journey" in new JourneyBuilder {
    val answers: UserAnswers = mandatoryAnswers
      .set(SalaryQuestionPage, Salary(1000.0))
      .get
    val periods: Seq[PeriodWithPaymentDate] = defaultJourneyCoreData.periods

    val expected =
      JourneyCoreData(FurloughWithinClaim(period("2020-03-01", "2020-03-31")), periods, Monthly, Payable, PensionStatus.DoesContribute)

    val actual = journeyData(RegularPay, answers)

    actual mustBe Some(RegularPayData(expected, Amount(1000.0)))
  }

  "build a VariablePayData for a VariablePay journey where CYLB is not required" in new JourneyBuilder {
    val answers: UserAnswers = mandatoryAnswers
      .set(VariableGrossPayPage, VariableGrossPay(1000.0))
      .get
      .set(PayMethodPage, Variable)
      .get
      .set(EmployeeStartDatePage, LocalDate.of(2019, 12, 1))
      .get

    val periods: Seq[PeriodWithPaymentDate] = defaultJourneyCoreData.periods

    val expected =
      JourneyCoreData(FurloughWithinClaim(period("2020-03-01", "2020-03-31")), periods, Monthly, Payable, PensionStatus.DoesContribute)

    val actual = journeyData(VariablePay, answers)

    actual mustBe Some(VariablePayData(expected, Amount(1000.0), NonFurloughPay(None, None), period("2019-12-01", "2020-02-29")))
  }

  "build a VariablePayData for a VariablePay journey where CYLB is required" in new JourneyBuilder {
    val answers: UserAnswers = mandatoryAnswers
      .set(VariableGrossPayPage, VariableGrossPay(1000.0))
      .get
      .set(PayMethodPage, Variable)
      .get
      .set(EmployedStartedPage, EmployeeStarted.OnOrBefore1Feb2019)
      .get
      .setListWithInvalidation(LastYearPayPage, CylbPayment(LocalDate.of(2019, 3, 31), Amount(1200.0)), 1)
      .get

    val periods: Seq[PeriodWithPaymentDate] = defaultJourneyCoreData.periods

    val expected =
      JourneyCoreData(FurloughWithinClaim(period("2020-03-01", "2020-03-31")), periods, Monthly, Payable, PensionStatus.DoesContribute)

    val actual = journeyData(VariablePayWithCylb, answers)

    actual mustBe Some(
      VariablePayWithCylbData(
        expected,
        Amount(1000.0),
        NonFurloughPay(None, None),
        period("2019-04-06", "2020-02-29"),
        Seq(CylbPayment(LocalDate.of(2019, 3, 31), Amount(1200.0)))))
  }
}
