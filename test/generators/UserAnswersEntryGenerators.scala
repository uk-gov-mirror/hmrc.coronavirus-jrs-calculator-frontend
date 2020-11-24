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

import java.time.LocalDate

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryEmployeeRTISubmissionUserAnswersEntry: Arbitrary[(EmployeeRTISubmissionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmployeeRTISubmissionPage.type]
        value <- arbitrary[EmployeeRTISubmission].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRegularLengthEmployedUserAnswersEntry: Arbitrary[(RegularLengthEmployedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RegularLengthEmployedPage.type]
        value <- arbitrary[RegularLengthEmployed].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayPeriodsListUserAnswersEntry: Arbitrary[(PayPeriodsListPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayPeriodsListPage.type]
        value <- arbitrary[PayPeriodsList].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPartTimeHoursUserAnswersEntry: Arbitrary[(PartTimeHoursPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PartTimeHoursPage.type]
        value <- arbitrary[PartTimeHours].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPartTimeQuestionUserAnswersEntry: Arbitrary[(PartTimeQuestionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PartTimeQuestionPage.type]
        value <- arbitrary[PartTimeQuestion].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayPeriodQuestionUserAnswersEntry: Arbitrary[(PayPeriodQuestionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayPeriodQuestionPage.type]
        value <- arbitrary[PayPeriodQuestion].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFurloughPeriodQuestionUserAnswersEntry: Arbitrary[(FurloughPeriodQuestionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughPeriodQuestionPage.type]
        value <- arbitrary[FurloughPeriodQuestion].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimPeriodQuestionUserAnswersEntry: Arbitrary[(ClaimPeriodQuestionPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimPeriodQuestionPage.type]
        value <- arbitrary[ClaimPeriodQuestion].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTopUpStatusUserAnswersEntry: Arbitrary[(TopUpStatusPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TopUpStatusPage.type]
        value <- arbitrary[TopUpStatus].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTopUpAmountUserAnswersEntry: Arbitrary[(TopUpAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TopUpAmountPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryTopupPeriodsUserAnswersEntry: Arbitrary[(TopUpPeriodsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[TopUpPeriodsPage.type]
        value <- arbitrary[List[LocalDate]].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLastYearPayUserAnswersEntry: Arbitrary[(LastYearPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LastYearPayPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLastPayDateUserAnswersEntry: Arbitrary[(LastPayDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LastPayDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPartialPayBeforeFurloughUserAnswersEntry: Arbitrary[(PartialPayBeforeFurloughPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PartialPayBeforeFurloughPage.type]
        value <- arbitrary[FurloughPartialPay].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPartialPayAfterFurloughUserAnswersEntry: Arbitrary[(PartialPayAfterFurloughPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PartialPayAfterFurloughPage.type]
        value <- arbitrary[FurloughPartialPay].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAnnualPayAmountUserAnswersEntry: Arbitrary[(AnnualPayAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AnnualPayAmountPage.type]
        value <- arbitrary[AnnualPayAmount].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmployeeStartDateUserAnswersEntry: Arbitrary[(EmployeeStartDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmployeeStartDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEmployeeStartedUserAnswersEntry: Arbitrary[(EmployeeStartedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EmployeeStartedPage.type]
        value <- arbitrary[EmployeeStarted].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFurloughStartDateUserAnswersEntry: Arbitrary[(FurloughStartDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughStartDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryFurloughEndDateUserAnswersEntry: Arbitrary[(FurloughEndDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughEndDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryfurloughOngoingUserAnswersEntry: Arbitrary[(FurloughStatusPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[FurloughStatusPage.type]
        value <- arbitrary[FurloughStatus].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimPeriodEndUserAnswersEntry: Arbitrary[(ClaimPeriodEndPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimPeriodEndPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimPeriodStartUserAnswersEntry: Arbitrary[(ClaimPeriodStartPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimPeriodStartPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionStatusUserAnswersEntry: Arbitrary[(PensionStatusPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionStatusPage.type]
        value <- arbitrary[PensionStatus].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNicCategoryUserAnswersEntry: Arbitrary[(NicCategoryPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NicCategoryPage.type]
        value <- arbitrary[NicCategory].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRegularPayAmountUserAnswersEntry: Arbitrary[(RegularPayAmountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RegularPayAmountPage.type]
        value <- arbitrary[Salary].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPaymentFrequencyUserAnswersEntry: Arbitrary[(PaymentFrequencyPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PaymentFrequencyPage.type]
        value <- arbitrary[PaymentFrequency].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarypayMethodUserAnswersEntry: Arbitrary[(PayMethodPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayMethodPage.type]
        value <- arbitrary[PayMethod].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayDateUserAnswersEntry: Arbitrary[(PayDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

}
