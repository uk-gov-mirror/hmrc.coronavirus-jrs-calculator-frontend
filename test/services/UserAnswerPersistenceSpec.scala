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

package services

import java.time.LocalDate

import base.SpecBaseControllerSpecs
import models.{AdditionalPayment, Amount, UserAnswers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import pages.AdditionalPaymentAmountPage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserAnswerPersistenceSpec extends SpecBaseControllerSpecs with ScalaFutures {

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(scaled(Span(5, Seconds)), scaled(Span(1, Seconds)))

  "persist a user answer" in {
    val additionalPaymentPeriod = LocalDate.of(2020, 3, 31)
    val amount = Amount(100)
    val additionalPayment = AdditionalPayment(additionalPaymentPeriod, amount) //TODO maybe use generator for different types of answers
    val stubbedPersistence: UserAnswers => Future[Boolean] = _ => Future.successful(true)

    val userAnswers = emptyUserAnswers
    val expectedAnswers = userAnswers.withAdditionalPaymentAmount(additionalPayment, Some(1))
    val eventualAnswers = new UserAnswerPersistence(stubbedPersistence)
      .persistAnswer(userAnswers, AdditionalPaymentAmountPage, additionalPayment, Some(1))

    whenReady(eventualAnswers) { res =>
      res mustBe expectedAnswers
    }
  }
}
