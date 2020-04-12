/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryPayQuestionPage: Arbitrary[PayQuestionPage.type] =
    Arbitrary(PayQuestionPage)

  implicit lazy val arbitraryTestOnlyNICGrantCalculatorPage: Arbitrary[TestOnlyNICGrantCalculatorPage.type] =
    Arbitrary(TestOnlyNICGrantCalculatorPage)

  implicit lazy val arbitraryClaimPeriodPage: Arbitrary[ClaimPeriodPage.type] =
    Arbitrary(ClaimPeriodPage)
}
