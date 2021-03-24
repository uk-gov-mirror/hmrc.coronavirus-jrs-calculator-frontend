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

/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate
import base.{CoreTestDataBuilder, SpecBase}
import cats.data.Validated.Valid
import models.{AdditionalPayment, Amount, FullPeriod, FullPeriodWithPaymentDate, FurloughWithinClaim, PaymentDate, PaymentFrequency, Period, PhaseTwoPeriod, PhaseTwoReferencePayData, StatutoryLeaveData, TopUpPayment, UserAnswers}
import pages.{AdditionalPaymentAmountPage, TopUpAmountPage}
import utils.CoreTestData

import java.time.LocalDate._

class DataExtractorSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "Extract prior furlough period from user answers" when {

    "employee start date is present" in new DataExtractor {
      val userAnswers = dummyUserAnswers.withEmployeeStartDate("2020-12-01")
      val expected    = period("2020, 12, 1", "2020, 2, 29")

      extractPriorFurloughPeriodV(userAnswers) mustBe Valid(expected)
    }

    "First Furlough Date is present" in new DataExtractor {
      val userAnswers = dummyUserAnswersNoLastPayDate
        .withEmployeeStartDate("2020-12-01")
        .withFirstFurloughDate("2020, 4, 1")

      val expected = period("2020, 12, 1", "2020, 3, 31")

      extractPriorFurloughPeriodV(userAnswers) mustBe Valid(expected)
    }

    "employee start date is not present" in new DataExtractor {
      val expected = period("2019, 4, 6", "2020, 2, 29")

      extractPriorFurloughPeriodV(dummyUserAnswers) mustBe Valid(expected)
    }

    "extract top up payments" in new DataExtractor {
      val payments = List(
        TopUpPayment(LocalDate.of(2020, 3, 1), Amount(100.0)),
        TopUpPayment(LocalDate.of(2020, 4, 1), Amount(0.0)),
        TopUpPayment(LocalDate.of(2020, 5, 1), Amount(50.0))
      )
      val userAnswers = UserAnswers("123")
        .setListItemWithInvalidation(TopUpAmountPage, payments.head, 1)
        .get
        .setListItemWithInvalidation(TopUpAmountPage, payments.tail.head, 2)
        .get
        .setListItemWithInvalidation(TopUpAmountPage, payments.drop(2).head, 3)
        .get

      extractTopUpPayment(userAnswers) mustBe payments
    }

    "extract additional payments" in new DataExtractor {
      val payments = List(
        AdditionalPayment(LocalDate.of(2020, 3, 1), Amount(100.0)),
        AdditionalPayment(LocalDate.of(2020, 4, 1), Amount(0.0)),
        AdditionalPayment(LocalDate.of(2020, 5, 1), Amount(50.0))
      )
      val userAnswers = UserAnswers("123")
        .setListItemWithInvalidation(AdditionalPaymentAmountPage, payments.head, 1)
        .get
        .setListItemWithInvalidation(AdditionalPaymentAmountPage, payments.tail.head, 2)
        .get
        .setListItemWithInvalidation(AdditionalPaymentAmountPage, payments.drop(2).head, 3)
        .get

      extractAdditionalPayment(userAnswers) mustBe payments
    }
  }

  "Extracting the Statutory Leave data from user answers" must {

    "return Valid(Some(StatutoryLeaveData)) model when both answers are retrieved" in new DataExtractor {

      val userAnswers =
        dummyUserAnswers
          .withStatutoryLeaveDays(1)
          .withStatutoryLeaveAmount(500)

      val expected = StatutoryLeaveData(days = 1, pay = 500)

      extractStatutoryLeaveData(userAnswers) mustBe Valid(Some(expected))
    }

    "return Valid(None) when days missing" in new DataExtractor {

      val userAnswers = dummyUserAnswers.withStatutoryLeaveAmount(500)

      extractStatutoryLeaveData(userAnswers) mustBe Valid(None)
    }

    "return Valid(None) when amount missing" in new DataExtractor {

      val userAnswers = dummyUserAnswers.withStatutoryLeaveDays(1)

      extractStatutoryLeaveData(userAnswers) mustBe Valid(None)
    }

    "return Valid(None) when both missing" in new DataExtractor {

      val userAnswers = dummyUserAnswers.withStatutoryLeaveDays(1)

      extractStatutoryLeaveData(userAnswers) mustBe Valid(None)
    }
  }

  "Extracting the Phase Two Reference Pay Data" must {

    "Include the Statutory Leave Data when values are held in user answers" in new DataExtractor {

      val userAnswers =
        dummyUserAnswers
          .withStatutoryLeaveDays(1)
          .withStatutoryLeaveAmount(500)

      val expected =
        PhaseTwoReferencePayData(
          furloughPeriod = FurloughWithinClaim(
            start = LocalDate.of(2020, 3, 1),
            end = LocalDate.of(2020, 4, 30)
          ),
          periods = List(
            PhaseTwoPeriod(
              periodWithPaymentDate = FullPeriodWithPaymentDate(
                period = FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
                paymentDate = PaymentDate(LocalDate.of(2020, 3, 20))
              ),
              actualHours = None,
              usualHours = None
            ),
            PhaseTwoPeriod(
              periodWithPaymentDate = FullPeriodWithPaymentDate(
                period = FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
                paymentDate = PaymentDate(LocalDate.of(2020, 4, 20))
              ),
              actualHours = None,
              usualHours = None
            )
          ),
          frequency = PaymentFrequency.Monthly,
          statutoryLeave = Some(StatutoryLeaveData(1, 500))
        )

      extractPhaseTwoReferencePayDataV(userAnswers) mustBe Valid(expected)
    }

    "Not include the Statutory Leave Data when no values are held in user answers" in new DataExtractor {

      val userAnswers = dummyUserAnswers

      val expected =
        PhaseTwoReferencePayData(
          furloughPeriod = FurloughWithinClaim(
            start = LocalDate.of(2020, 3, 1),
            end = LocalDate.of(2020, 4, 30)
          ),
          periods = List(
            PhaseTwoPeriod(
              periodWithPaymentDate = FullPeriodWithPaymentDate(
                period = FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
                paymentDate = PaymentDate(LocalDate.of(2020, 3, 20))
              ),
              actualHours = None,
              usualHours = None
            ),
            PhaseTwoPeriod(
              periodWithPaymentDate = FullPeriodWithPaymentDate(
                period = FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
                paymentDate = PaymentDate(LocalDate.of(2020, 4, 20))
              ),
              actualHours = None,
              usualHours = None
            )
          ),
          frequency = PaymentFrequency.Monthly,
          statutoryLeave = None
        )

      extractPhaseTwoReferencePayDataV(userAnswers) mustBe Valid(expected)
    }
  }
}
