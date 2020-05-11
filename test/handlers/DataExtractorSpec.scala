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

/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.{AdditionalPayment, Amount, TopUpPayment, UserAnswers}
import pages.{AdditionalPaymentAmountPage, TopUpAmountPage}
import play.api.libs.json.Json
import utils.CoreTestData

class DataExtractorSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "Extract prior furlough period from user answers" when {

    "employee start date is present" in new DataExtractor {
      val userAnswers = Json.parse(userAnswersJson(employeeStartDate = "2020-12-01")).as[UserAnswers]
      val expected = period("2020, 12, 1", "2020, 2, 29")

      extractPriorFurloughPeriod(userAnswers) mustBe Some(expected)
    }

    "employee start date is not present" in new DataExtractor {
      val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
      val expected = period("2019, 4, 6", "2020, 2, 29")

      extractPriorFurloughPeriod(userAnswers) mustBe Some(expected)
    }

    "extract top up payments" in new DataExtractor {
      val payments = List(
        TopUpPayment(LocalDate.of(2020, 3, 1), Amount(100.0)),
        TopUpPayment(LocalDate.of(2020, 4, 1), Amount(0.0)),
        TopUpPayment(LocalDate.of(2020, 5, 1), Amount(50.0))
      )
      val userAnswers = UserAnswers("123")
        .setListWithInvalidation(TopUpAmountPage, payments.head, 1)
        .get
        .setListWithInvalidation(TopUpAmountPage, payments.tail.head, 2)
        .get
        .setListWithInvalidation(TopUpAmountPage, payments.drop(2).head, 3)
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
        .setListWithInvalidation(AdditionalPaymentAmountPage, payments.head, 1)
        .get
        .setListWithInvalidation(AdditionalPaymentAmountPage, payments.tail.head, 2)
        .get
        .setListWithInvalidation(AdditionalPaymentAmountPage, payments.drop(2).head, 3)
        .get

      extractAdditionalPayment(userAnswers) mustBe payments
    }
  }
}
