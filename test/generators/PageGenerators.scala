/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryClaimPeriodStartPage: Arbitrary[ClaimPeriodStartPage.type] =
    Arbitrary(ClaimPeriodStartPage)

  implicit lazy val arbitraryClaimPeriodEndPage: Arbitrary[ClaimPeriodEndPage.type] =
    Arbitrary(ClaimPeriodEndPage)

  implicit lazy val arbitraryFurloughQuestionPage: Arbitrary[FurloughQuestionPage.type] =
    Arbitrary(FurloughQuestionPage)

  implicit lazy val arbitraryPensionAutoEnrolmentPage: Arbitrary[PensionAutoEnrolmentPage.type] =
    Arbitrary(PensionAutoEnrolmentPage)

  implicit lazy val arbitraryNicCategoryPage: Arbitrary[NicCategoryPage.type] =
    Arbitrary(NicCategoryPage)

  implicit lazy val arbitrarySalaryQuestionPage: Arbitrary[SalaryQuestionPage.type] =
    Arbitrary(SalaryQuestionPage)

  implicit lazy val arbitraryPaymentFrequencyPage: Arbitrary[PaymentFrequencyPage.type] =
    Arbitrary(PaymentFrequencyPage)

  implicit lazy val arbitraryPayQuestionPage: Arbitrary[PayQuestionPage.type] =
    Arbitrary(PayQuestionPage)

  implicit lazy val arbitraryPayDatePage: Arbitrary[PayDatePage.type] =
    Arbitrary(PayDatePage)

  implicit lazy val arbitraryTestOnlyNICGrantCalculatorPage: Arbitrary[TestOnlyNICGrantCalculatorPage.type] =
    Arbitrary(TestOnlyNICGrantCalculatorPage)
}
