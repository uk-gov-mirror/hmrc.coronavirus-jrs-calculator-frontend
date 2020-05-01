/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package services

import base.{CoreTestDataBuilder, SpecBase}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PartialPayHelperSpec extends SpecBase with ScalaCheckPropertyChecks with CoreTestDataBuilder {

  "PartialPayHelper" when {

    "getPeriodRemainder" must {

      "return remainders with multiple days before the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5"  -> "2020,4,25",
          "2020,4,10" -> "2020,4,25"
        )
        val expected = period(
          "2020,4,5",
          "2020,4,9"
        )

        getPeriodRemainder(partial) mustBe expected
      }

      "return remainders with multiple days after the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5" -> "2020,4,25",
          "2020,4,5" -> "2020,4,15"
        )
        val expected = period(
          "2020,4,16",
          "2020,4,25"
        )

        getPeriodRemainder(partial) mustBe expected
      }

      "return remainders with a single day before the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5" -> "2020,4,25",
          "2020,4,6" -> "2020,4,25"
        )
        val expected = period(
          "2020,4,5",
          "2020,4,5"
        )

        getPeriodRemainder(partial) mustBe expected
      }

      "return remainders with a single day after the partial" in new PartialPayExtractor {

        val partial = partialPeriod(
          "2020,4,5" -> "2020,4,25",
          "2020,4,5" -> "2020,4,24"
        )
        val expected = period(
          "2020,4,25",
          "2020,4,25"
        )

        getPeriodRemainder(partial) mustBe expected
      }

    }

  }

}
