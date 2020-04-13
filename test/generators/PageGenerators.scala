/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryPaymentFrequencyPage: Arbitrary[PaymentFrequencyPage.type] =
    Arbitrary(PaymentFrequencyPage)

  implicit lazy val arbitraryPayQuestionPage: Arbitrary[PayQuestionPage.type] =
    Arbitrary(PayQuestionPage)

  implicit lazy val arbitraryPayDatePage: Arbitrary[PayDatePage.type] =
    Arbitrary(PayDatePage)

  implicit lazy val arbitraryTestOnlyNICGrantCalculatorPage: Arbitrary[TestOnlyNICGrantCalculatorPage.type] =
    Arbitrary(TestOnlyNICGrantCalculatorPage)

  implicit lazy val arbitraryClaimPeriodPage: Arbitrary[ClaimPeriodPage.type] =
    Arbitrary(ClaimPeriodPage)
}
