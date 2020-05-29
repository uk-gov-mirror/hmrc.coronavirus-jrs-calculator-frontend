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

import cats.data.Kleisli
import cats.implicits._
import models.ClaimPeriodQuestion.{ClaimOnDifferentPeriod, ClaimOnSamePeriod}
import models.FurloughPeriodQuestion.{FurloughedOnDifferentPeriod, FurloughedOnSamePeriod}
import models.PayPeriodQuestion.{UseDifferentPayPeriod, UseSamePayPeriod}
import models.UserAnswers
import pages._
import play.api.libs.json.Json
import utils.UserAnswersHelper
import com.softwaremill.quicklens._

trait FastJourneyUserAnswersHandler extends DataExtractor with UserAnswersHelper {

  def claimQuestion(userAnswer: UserAnswers): Option[UserAnswersState] =
    userAnswer.get(ClaimPeriodQuestionPage) map {
      case ClaimOnSamePeriod      => UserAnswersState(userAnswer, userAnswer)
      case ClaimOnDifferentPeriod => UserAnswersState(userAnswer.copy(data = Json.obj()), userAnswer)
    }

  def furloughQuestion(answer: UserAnswers): Option[UserAnswersState] =
    answer.get(FurloughPeriodQuestionPage) flatMap {
      case FurloughedOnSamePeriod      => Some(UserAnswersState(answer, answer))
      case FurloughedOnDifferentPeriod => (clearAllAnswers andThen keepClaimPeriod).run(UserAnswersState(answer, answer))
    }

  def payQuestion(answer: UserAnswers): Option[UserAnswersState] =
    answer.get(PayPeriodQuestionPage) flatMap {
      case UseSamePayPeriod =>
        (clearAllAnswers andThen keepClaimPeriod andThen keepFurloughPeriod andThen keepPayPeriodData).run(UserAnswersState(answer, answer))
      case UseDifferentPayPeriod =>
        (clearAllAnswers andThen keepClaimPeriod andThen keepFurloughPeriod).run(UserAnswersState(answer, answer))
    }

  private val keepClaimPeriod: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      start     <- extractClaimPeriodStart(answersState.original)
      end       <- extractClaimPeriodEnd(answersState.original)
      withStart <- answersState.updated.set(ClaimPeriodStartPage, start).toOption
      withEnd   <- withStart.set(ClaimPeriodEndPage, end).toOption
    } yield UserAnswersState(withEnd, answersState.original))

  private val keepFurloughPeriod: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      furlough  <- extractFurloughWithinClaim(answersState.original)
      withStart <- answersState.updated.set(FurloughStartDatePage, furlough.start).toOption
      withEnd   <- withStart.set(FurloughEndDatePage, furlough.end).toOption
    } yield UserAnswersState(withEnd, answersState.original))

  private val keepPayPeriod: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(
    answersState =>
      addPayDates(answersState.updated, answersState.original.getList(PayDatePage).toList).toOption
        .map(payPeriods => UserAnswersState(payPeriods, answersState.original)))

  private val keepPaymentFrequency: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      frequency     <- extractPaymentFrequency(answersState.original)
      withFrequency <- answersState.updated.set(PaymentFrequencyPage, frequency).toOption
    } yield UserAnswersState(withFrequency, answersState.original))

  private val keepLastPayDate: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      frequency       <- extractLastPayDate(answersState.original)
      withLastPayDate <- answersState.updated.set(LastPayDatePage, frequency).toOption
    } yield UserAnswersState(withLastPayDate, answersState.original))

  private val keepFurloughStatus: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      frequency  <- extractFurloughStatus(answersState.original)
      withStatus <- answersState.updated.set(FurloughStatusPage, frequency).toOption
    } yield UserAnswersState(withStatus, answersState.original))

  private val keepPayPeriodData = keepPayPeriod andThen keepPaymentFrequency andThen keepLastPayDate andThen keepFurloughStatus

  private val clearAllAnswers: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(
    answersState => Option(answersState.modify(_.updated.data).setTo(Json.obj())))
}

final case class UserAnswersState(updated: UserAnswers, original: UserAnswers)
