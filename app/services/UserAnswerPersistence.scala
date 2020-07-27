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

import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.Writes

import scala.concurrent.{ExecutionContext, Future}

class UserAnswerPersistence(persist: UserAnswers => Future[Boolean])(implicit ec: ExecutionContext) {

  def persistAnswer[A](userAnswers: UserAnswers, questionPage: QuestionPage[A], answer: A, idx: Option[Int])(
    implicit writes: Writes[A]): Future[UserAnswers] =
    for {
      updatedAnswers <- Future.fromTry(userAnswers.set(questionPage, answer, idx))
      _              <- persist(updatedAnswers)
    } yield updatedAnswers
}
