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
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import models.ClaimPeriodQuestion.{ClaimOnDifferentPeriod, ClaimOnSamePeriod}
import models.FurloughPeriodQuestion.{FurloughedOnDifferentPeriod, FurloughedOnSamePeriod}
import models.PayPeriodQuestion.{UseDifferentPayPeriod, UseSamePayPeriod}
import models.UserAnswers
import pages._
import play.api.libs.json.{JsError, Json}
import utils.UserAnswersHelper
import com.softwaremill.quicklens._
import models.UserAnswers.AnswerV

trait FastJourneyUserAnswersHandler extends DataExtractor with UserAnswersHelper {

  def claimQuestion(userAnswer: UserAnswers): AnswerV[UserAnswersState] =
    userAnswer.getV(ClaimPeriodQuestionPage) map {
      case ClaimOnSamePeriod      => UserAnswersState(userAnswer, userAnswer)
      case ClaimOnDifferentPeriod => UserAnswersState(userAnswer.copy(data = Json.obj()), userAnswer)
    }

  def updateJourney(userAnswer: UserAnswers): AnswerV[UserAnswersState] =
    userAnswer.getV(ClaimPeriodQuestionPage) match {
      case Valid(ClaimOnSamePeriod)      => processFurloughQuestion(UserAnswersState(userAnswer, userAnswer))
      case Valid(ClaimOnDifferentPeriod) => UserAnswersState(userAnswer.copy(data = Json.obj()), userAnswer).validNec
      case inv @ Invalid(_)              => inv
    }

  def furloughQuestionV(answer: UserAnswers): AnswerV[UserAnswersState] =
    answer.getV(FurloughPeriodQuestionPage) match {
      case Valid(FurloughedOnSamePeriod) => Valid(UserAnswersState(answer, answer))
      case Valid(FurloughedOnDifferentPeriod) =>
        (clearAllAnswers andThen keepClaimPeriod)
          .run(UserAnswersState(answer, answer))
          .toValidNec(JsError(s"Unable to clear answers while keeping pay period for $answer"))

      case inv @ Invalid(_) => inv
    }

  private[this] def processFurloughQuestion(answer: UserAnswersState): AnswerV[UserAnswersState] =
    answer.original.getV(FurloughPeriodQuestionPage) match {
      case Valid(FurloughedOnSamePeriod) => processPayQuestionV(answer)
      case Valid(FurloughedOnDifferentPeriod) =>
        (clearAllAnswers andThen keepClaimPeriod)
          .run(answer)
          .toValidNec(JsError(s"Unable to clear answers while keeping pay period for ${answer.original}"))
      case Invalid(_) => Valid(answer)
    }

  def payQuestion(answer: UserAnswers): AnswerV[UserAnswersState] =
    answer.getV(PayPeriodQuestionPage) match {
      case Valid(UseSamePayPeriod) =>
        (clearAllAnswers andThen keepClaimPeriod andThen keepFurloughPeriod andThen keepPayPeriodData)
          .run(UserAnswersState(answer, answer))
          .toValidNec(JsError("Failed to run Kleisi composition for UseSamePayPeriod"))
      case Valid(UseDifferentPayPeriod) =>
        (clearAllAnswers andThen keepClaimPeriod andThen keepFurloughPeriod)
          .run(UserAnswersState(answer, answer))
          .toValidNec(JsError("Failed to run Kleisi composition for UseDifferentPayPeriod"))
      case inv @ Invalid(_) => inv
    }

  private[this] def processPayQuestionV(answer: UserAnswersState): AnswerV[UserAnswersState] =
    answer.original.getV(PayPeriodQuestionPage) match {
      case Valid(UseSamePayPeriod) =>
        (clearAllAnswers andThen keepClaimPeriod andThen keepFurloughPeriod andThen keepPayPeriodData)
          .run(answer)
          .toValidNec(JsError(s"Unable to clear answers using same pay period: ${answer.original}"))

      case Valid(UseDifferentPayPeriod) =>
        (clearAllAnswers andThen keepClaimPeriod andThen keepFurloughPeriod)
          .run(answer)
          .toValidNec(JsError(s"Unable to clear answers using different pay period: ${answer.original}"))
      case Invalid(_) => Valid(answer)
    }

  private val keepClaimPeriod: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      start     <- extractClaimPeriodStartV(answersState.original).toOption
      end       <- extractClaimPeriodEndV(answersState.original).toOption
      withStart <- answersState.updated.set(ClaimPeriodStartPage, start).toOption
      withEnd   <- withStart.set(ClaimPeriodEndPage, end).toOption
    } yield UserAnswersState(withEnd, answersState.original))

  private val keepFurloughPeriod: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      furlough  <- extractFurloughWithinClaimV(answersState.original).toOption
      withStart <- answersState.updated.set(FurloughStartDatePage, furlough.start).toOption
      withEnd   <- withStart.set(FurloughEndDatePage, furlough.end).toOption
    } yield UserAnswersState(withEnd, answersState.original))

  private val keepPayPeriod: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(
    answersState =>
      addPayDates(answersState.updated, answersState.original.getList(PayDatePage).toList).toOption
        .map(payPeriods => UserAnswersState(payPeriods, answersState.original)))

  private val keepPayMethod: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      method     <- extractPayMethodV(answersState.original).toOption
      withMethod <- answersState.updated.set(PayMethodPage, method).toOption
    } yield UserAnswersState(withMethod, answersState.original))

  private val keepPaymentFrequency: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      frequency     <- extractPaymentFrequencyV(answersState.original).toOption
      withFrequency <- answersState.updated.set(PaymentFrequencyPage, frequency).toOption
    } yield UserAnswersState(withFrequency, answersState.original))

  private val keepLastPayDate: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      frequency       <- extractLastPayDateV(answersState.original).toOption
      withLastPayDate <- answersState.updated.set(LastPayDatePage, frequency).toOption
    } yield UserAnswersState(withLastPayDate, answersState.original))

  private val keepFurloughStatus: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(answersState =>
    for {
      frequency  <- extractFurloughStatusV(answersState.original).toOption
      withStatus <- answersState.updated.set(FurloughStatusPage, frequency).toOption
    } yield UserAnswersState(withStatus, answersState.original))

  private val keepPayPeriodData = keepPayPeriod andThen keepPaymentFrequency andThen keepLastPayDate andThen keepFurloughStatus

  private val clearAllAnswers: Kleisli[Option, UserAnswersState, UserAnswersState] = Kleisli(
    answersState => Option(answersState.modify(_.updated.data).setTo(Json.obj())))
}

final case class UserAnswersState(updated: UserAnswers, original: UserAnswers)
