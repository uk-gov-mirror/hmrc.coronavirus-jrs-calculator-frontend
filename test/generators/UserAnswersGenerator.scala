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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(FurloughInLastTaxYearPage.type, JsValue)] ::
      arbitrary[(EmployeeRTISubmissionPage.type, JsValue)] ::
      arbitrary[(RegularLengthEmployedPage.type, JsValue)] ::
      arbitrary[(PayPeriodsListPage.type, JsValue)] ::
      arbitrary[(PartTimeHoursPage.type, JsValue)] ::
      arbitrary[(PartTimeQuestionPage.type, JsValue)] ::
      arbitrary[(PayPeriodQuestionPage.type, JsValue)] ::
      arbitrary[(FurloughPeriodQuestionPage.type, JsValue)] ::
      arbitrary[(ClaimPeriodQuestionPage.type, JsValue)] ::
      arbitrary[(TopUpStatusPage.type, JsValue)] ::
      arbitrary[(TopUpAmountPage.type, JsValue)] ::
      arbitrary[(TopUpPeriodsPage.type, JsValue)] ::
      arbitrary[(LastYearPayPage.type, JsValue)] ::
      arbitrary[(LastPayDatePage.type, JsValue)] ::
      arbitrary[(PartialPayBeforeFurloughPage.type, JsValue)] ::
      arbitrary[(PartialPayBeforeFurloughPage.type, JsValue)] ::
      arbitrary[(PartialPayAfterFurloughPage.type, JsValue)] ::
      arbitrary[(AnnualPayAmountPage.type, JsValue)] ::
      arbitrary[(EmployeeStartDatePage.type, JsValue)] ::
      arbitrary[(EmployeeStartedPage.type, JsValue)] ::
      arbitrary[(FurloughStartDatePage.type, JsValue)] ::
      arbitrary[(FurloughEndDatePage.type, JsValue)] ::
      arbitrary[(FurloughStatusPage.type, JsValue)] ::
      arbitrary[(ClaimPeriodEndPage.type, JsValue)] ::
      arbitrary[(ClaimPeriodStartPage.type, JsValue)] ::
      arbitrary[(PensionStatusPage.type, JsValue)] ::
      arbitrary[(NicCategoryPage.type, JsValue)] ::
      arbitrary[(RegularPayAmountPage.type, JsValue)] ::
      arbitrary[(PaymentFrequencyPage.type, JsValue)] ::
      arbitrary[(PayDatePage.type, JsValue)] ::
      arbitrary[(PayMethodPage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id <- nonEmptyString
        data <- generators match {
                 case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
                 case _   => Gen.mapOf(oneOf(generators))
               }
      } yield
        UserAnswers(
          id = id,
          data = data.foldLeft(Json.obj()) {
            case (obj, (path, value)) =>
              obj.setObject(path.path, value).get
          }
        )
    }
  }
}
