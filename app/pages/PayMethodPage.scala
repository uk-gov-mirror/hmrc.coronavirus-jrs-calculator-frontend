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

package pages

import models.PayMethod.{Regular, Variable}
import models.{PayMethod, UserAnswers}
import play.api.libs.json.JsPath

import scala.util.Try

case object PayMethodPage extends QuestionPage[PayMethod] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "payMethod"

  override def cleanup(value: Option[PayMethod], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(Regular) =>
        removeVariableAnswers(userAnswers)
      case Some(Variable) =>
        userAnswers
          .remove(RegularPayAmountPage)
      case _ =>
        super.cleanup(value, userAnswers)
    }

  private def removeVariableAnswers(userAnswers: UserAnswers) =
    userAnswers
      .remove(EmployeeStartedPage)
      .get
      .remove(EmployeeStartDatePage)
      .get
      .remove(LastYearPayPage)
      .get
      .remove(AnnualPayAmountPage)
      .get
      .remove(PartialPayBeforeFurloughPage)
      .get
      .remove(PartialPayAfterFurloughPage)
}
