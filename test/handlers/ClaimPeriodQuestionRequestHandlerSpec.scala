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

import base.SpecBase
import models.ClaimPeriodQuestion.{ClaimOnDifferentPeriod, ClaimOnSamePeriod}
import models.UserAnswers
import org.scalatest.concurrent.ScalaFutures
import utils.CoreTestData

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ClaimPeriodQuestionRequestHandlerSpec extends SpecBase with CoreTestData with ScalaFutures {

  "persist claim period question user answer" in new ClaimPeriodQuestionRequestHandler {
    val userAnswers: UserAnswers = dummyUserAnswers
    val stubPersist: UserAnswers => Future[Boolean] = _ => Future.successful(true)

    whenReady(persistAnswer(userAnswers, ClaimOnSamePeriod, stubPersist)) { result =>
      result mustBe userAnswers.withClaimPeriodQuestion(ClaimOnSamePeriod)
    }

    whenReady(persistAnswer(userAnswers, ClaimOnDifferentPeriod, stubPersist)) { result =>
      result mustBe userAnswers.withClaimPeriodQuestion(ClaimOnDifferentPeriod)
    }
  }

  "return a failure if cannot persist" in new ClaimPeriodQuestionRequestHandler {
    val userAnswers: UserAnswers = dummyUserAnswers
    val stubPersist: UserAnswers => Future[Boolean] = _ => Future.failed(new Exception("can't talk to DB"))

    persistAnswer(userAnswers, ClaimOnSamePeriod, stubPersist).value mustBe None
  }
}
