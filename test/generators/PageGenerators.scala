/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryLastYearPayPage: Arbitrary[LastYearPayPage.type] =
    Arbitrary(LastYearPayPage)

  implicit lazy val arbitraryLastPayDatePage: Arbitrary[LastPayDatePage.type] =
    Arbitrary(LastPayDatePage)

  implicit lazy val arbitraryFurloughTopUpStatusPage: Arbitrary[FurloughTopUpStatusPage.type] =
    Arbitrary(FurloughTopUpStatusPage)

  implicit lazy val arbitraryPartialPayBeforeFurloughPage: Arbitrary[PartialPayBeforeFurloughPage.type] =
    Arbitrary(PartialPayBeforeFurloughPage)

  implicit lazy val arbitraryPartialPayAfterFurloughPage: Arbitrary[PartialPayAfterFurloughPage.type] =
    Arbitrary(PartialPayAfterFurloughPage)

  implicit lazy val arbitraryVariableGrossPayPage: Arbitrary[VariableGrossPayPage.type] =
    Arbitrary(VariableGrossPayPage)

  implicit lazy val arbitraryEmployeeStartDatePage: Arbitrary[EmployeeStartDatePage.type] =
    Arbitrary(EmployeeStartDatePage)

  implicit lazy val arbitraryEmployeeStartedPage: Arbitrary[EmployedStartedPage.type] =
    Arbitrary(EmployedStartedPage)

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

  implicit lazy val arbitrarySalaryQuestionPage: Arbitrary[SalaryQuestionPage.type] =
    Arbitrary(SalaryQuestionPage)

  implicit lazy val arbitraryPaymentFrequencyPage: Arbitrary[PaymentFrequencyPage.type] =
    Arbitrary(PaymentFrequencyPage)

  implicit lazy val arbitrarypayMethodPage: Arbitrary[PayMethodPage.type] =
    Arbitrary(PayMethodPage)

  implicit lazy val arbitraryPayDatePage: Arbitrary[PayDatePage.type] =
    Arbitrary(PayDatePage)

}
