/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.{CoreTestDataBuilder, SpecBase}
import models.PayMethod.{Regular, Variable}
import models.{Amount, CylbEligibility, FullPeriod, FullPeriodWithPaymentDate, MandatoryData, PaymentDate, Period, UserAnswers}
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import play.api.libs.json.Json
import utils.CoreTestData

class DataExtractorSpec extends SpecBase with CoreTestData with CoreTestDataBuilder {

  "Extract mandatory data in order to do the calculation" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]

    extract(userAnswers) must matchPattern {
      case Some(MandatoryData(_, _, _, _, _, _, _, _, _)) =>
    }
  }

  "extractFurloughPeriod" must {

    "use the user submitted furlough end date if furlough question is yes" in new DataExtractor {
      val userAnswers = Json.parse(userAnswersJson("ended", furloughEndDate = "2020-03-31")).as[UserAnswers]
      val expected = Period(userAnswers.get(FurloughStartDatePage).get, userAnswers.get(FurloughEndDatePage).get)

      extractRelevantFurloughPeriod(extract(userAnswers).get, userAnswers) mustBe expected
    }

    "use the claim period end date if furlough question is no" in new DataExtractor {
      val userAnswers = Json.parse(userAnswersJson("ended")).as[UserAnswers]
      val expected = Period(userAnswers.get(FurloughStartDatePage).get, userAnswers.get(ClaimPeriodEndPage).get)

      extractRelevantFurloughPeriod(extract(userAnswers).get, userAnswers) mustBe expected
    }

    "use the furlough start date if later than claim start date" in new DataExtractor {
      val userAnswers =
        Json.parse(userAnswersJson("ongoing", furloughStartDate = "2020-03-02", claimStartDate = "2020-03-01")).as[UserAnswers]
      val expected = Period(userAnswers.get(FurloughStartDatePage).get, userAnswers.get(ClaimPeriodEndPage).get)

      extractRelevantFurloughPeriod(extract(userAnswers).get, userAnswers) mustBe expected
    }

    "use the claim start date if later than furlough start date" in new DataExtractor {
      val userAnswers =
        Json.parse(userAnswersJson("ongoing", furloughStartDate = "2020-03-01", claimStartDate = "2020-03-02")).as[UserAnswers]
      val expected = Period(userAnswers.get(ClaimPeriodStartPage).get, userAnswers.get(ClaimPeriodEndPage).get)

      extractRelevantFurloughPeriod(extract(userAnswers).get, userAnswers) mustBe expected
    }

  }

  "Extract Salary as an Amount() when payMethod answer is Regular" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
    val expected = Amount(2000.0)

    extractGrossPay(userAnswers) mustBe Some(expected)
  }

  "Extract prior furlough period from user answers" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson(employeeStartDate = "2020-12-01")).as[UserAnswers]
    val expected = period("2020, 12, 1", "2020, 2, 29")

    extractPriorFurloughPeriod(userAnswers) mustBe Some(expected)
  }

  "Extract variable gross pay when payMethod answer is Variable" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson(payMethod = "variable", variableGrossPay = "2400.0")).as[UserAnswers]
    val expected = Amount(2400.0)

    extractGrossPay(userAnswers) mustBe Some(expected)
  }

  "Extract payments for employees that are paid a regular amount each time" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
    val expected = Seq(
      paymentWithFullPeriod(
        2000.0,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 20))),
        Regular),
      paymentWithFullPeriod(
        2000.0,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
          PaymentDate(LocalDate.of(2020, 4, 20))),
        Regular
      )
    )

    val furloughPeriod: Period = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))

    extractPayments(userAnswers, furloughPeriod) mustBe Some(expected)
  }

  "Extract payments for employees that are paid a variable amount each time" in new DataExtractor {
    val userAnswers =
      Json.parse(userAnswersJson(payMethod = "variable", variableGrossPay = "2400.00", employeeStartDate = "2019-12-01")).as[UserAnswers]
    val expected = Seq(
      paymentWithFullPeriod(
        817.47,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
          PaymentDate(LocalDate.of(2020, 3, 20))),
        Variable
      ),
      paymentWithFullPeriod(
        791.10,
        FullPeriodWithPaymentDate(
          FullPeriod(Period(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30))),
          PaymentDate(LocalDate.of(2020, 4, 20))),
        Variable
      )
    )

    val furloughPeriod: Period = Period(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))

    extractPayments(userAnswers, furloughPeriod) mustBe Some(expected)
  }

  "Calculates cylbs when variable length is No but the employee start date is before 6/4/2019" in new DataExtractor {
    val userAnswers = Json.parse(jsonCylb).as[UserAnswers]

    val expected =
      List(
        paymentWithPartialPeriod(
          150,
          988.38,
          partialPeriodWithPaymentDate("2020-03-01", "2020-03-31", "2020-03-15", "2020-03-31", "2020-03-30"),
          Variable),
        paymentWithFullPeriod(1744.20, fullPeriodWithPaymentDate("2020-04-01", "2020-04-30", "2020-04-30"), Variable)
      )
    extractPayments(userAnswers, extractRelevantFurloughPeriod(extract(userAnswers).get, userAnswers)) mustBe Some(expected)
  }

  "Calculates cylbs when variable length is Yes but the employee start date is None" in new DataExtractor {
    val userAnswers = Json.parse(jsonCylbWithoutEmployeeStartDate).as[UserAnswers]

    val expected =
      List(
        paymentWithPartialPeriod(
          100.0,
          3271.43,
          partialPeriodWithPaymentDate("2020-3-1", "2020-3-28", "2020-3-2", "2020-3-28", "2020-03-28"),
          Variable)
      )

    extractPayments(userAnswers, extractRelevantFurloughPeriod(extract(userAnswers).get, userAnswers)) mustBe Some(expected)
  }

  "defines a variable calculation that requires cylb" in new DataExtractor {
    import models.EmployeeStarted._

    cylbCalculationPredicate(OnOrBefore1Feb2019, LocalDate.now) mustBe CylbEligibility(true)
    cylbCalculationPredicate(After1Feb2019, LocalDate.of(2019, 4, 5)) mustBe CylbEligibility(true)
    cylbCalculationPredicate(After1Feb2019, LocalDate.of(2019, 4, 6)) mustBe CylbEligibility(false)
  }

}
