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

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryPartTimeQuestionPage: Arbitrary[PartTimeQuestionPage.type] =
    Arbitrary(PartTimeQuestionPage)

  implicit lazy val arbitraryPayPeriodQuestionPage: Arbitrary[PayPeriodQuestionPage.type] =
    Arbitrary(PayPeriodQuestionPage)

  implicit lazy val arbitraryFurloughPeriodQuestionPage: Arbitrary[FurloughPeriodQuestionPage.type] =
    Arbitrary(FurloughPeriodQuestionPage)

  implicit lazy val arbitraryClaimPeriodQuestionPage: Arbitrary[ClaimPeriodQuestionPage.type] =
    Arbitrary(ClaimPeriodQuestionPage)

  implicit lazy val arbitraryAdditionalPaymentAmountPage: Arbitrary[AdditionalPaymentAmountPage.type] =
    Arbitrary(AdditionalPaymentAmountPage)

  implicit lazy val arbitraryTopUpStatusPage: Arbitrary[TopUpStatusPage.type] =
    Arbitrary(TopUpStatusPage)

  implicit lazy val arbitraryTopUpAmountPage: Arbitrary[TopUpAmountPage.type] =
    Arbitrary(TopUpAmountPage)

  implicit lazy val arbitraryTopupPeriodsPage: Arbitrary[TopUpPeriodsPage.type] =
    Arbitrary(TopUpPeriodsPage)

  implicit lazy val arbitraryLastYearPayPage: Arbitrary[LastYearPayPage.type] =
    Arbitrary(LastYearPayPage)

  implicit lazy val arbitraryLastPayDatePage: Arbitrary[LastPayDatePage.type] =
    Arbitrary(LastPayDatePage)

  implicit lazy val arbitraryPartialPayBeforeFurloughPage: Arbitrary[PartialPayBeforeFurloughPage.type] =
    Arbitrary(PartialPayBeforeFurloughPage)

  implicit lazy val arbitraryPartialPayAfterFurloughPage: Arbitrary[PartialPayAfterFurloughPage.type] =
    Arbitrary(PartialPayAfterFurloughPage)

  implicit lazy val arbitraryAnnualPayAmountPage: Arbitrary[AnnualPayAmountPage.type] =
    Arbitrary(AnnualPayAmountPage)

  implicit lazy val arbitraryEmployeeStartDatePage: Arbitrary[EmployeeStartDatePage.type] =
    Arbitrary(EmployeeStartDatePage)

  implicit lazy val arbitraryEmployeeStartedPage: Arbitrary[EmployeeStartedPage.type] =
    Arbitrary(EmployeeStartedPage)

  implicit lazy val arbitraryFurloughStartDatePage: Arbitrary[FurloughStartDatePage.type] =
    Arbitrary(FurloughStartDatePage)

  implicit lazy val arbitraryFurloughEndDatePage: Arbitrary[FurloughEndDatePage.type] =
    Arbitrary(FurloughEndDatePage)

  implicit lazy val arbitraryClaimPeriodStartPage: Arbitrary[ClaimPeriodStartPage.type] =
    Arbitrary(ClaimPeriodStartPage)

  implicit lazy val arbitraryClaimPeriodEndPage: Arbitrary[ClaimPeriodEndPage.type] =
    Arbitrary(ClaimPeriodEndPage)

  implicit lazy val arbitraryFurloughOngoingPage: Arbitrary[FurloughStatusPage.type] =
    Arbitrary(FurloughStatusPage)

  implicit lazy val arbitraryPensionStatusPage: Arbitrary[PensionStatusPage.type] =
    Arbitrary(PensionStatusPage)

  implicit lazy val arbitraryNicCategoryPage: Arbitrary[NicCategoryPage.type] =
    Arbitrary(NicCategoryPage)

  implicit lazy val arbitraryRegularPayAmountPage: Arbitrary[RegularPayAmountPage.type] =
    Arbitrary(RegularPayAmountPage)

  implicit lazy val arbitraryPaymentFrequencyPage: Arbitrary[PaymentFrequencyPage.type] =
    Arbitrary(PaymentFrequencyPage)

  implicit lazy val arbitrarypayMethodPage: Arbitrary[PayMethodPage.type] =
    Arbitrary(PayMethodPage)

  implicit lazy val arbitraryPayDatePage: Arbitrary[PayDatePage.type] =
    Arbitrary(PayDatePage)

}
