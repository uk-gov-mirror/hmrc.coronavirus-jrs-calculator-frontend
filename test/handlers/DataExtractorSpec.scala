/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.SpecBase
import models.{FurloughPeriod, PayPeriod, RegularPayment, Salary, UserAnswers}
import play.api.libs.json.Json
import utils.CoreTestData

class DataExtractorSpec extends SpecBase with CoreTestData {

  "Extract mandatory data in order to do the calculation" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]

    extract(userAnswers) must matchPattern {
      case Some(MandatoryData(_, _, _, _, _, _, _)) =>
    }
  }

  "Extract furlough period matching claim period if furlough question entered is yes" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson("yes")).as[UserAnswers]
    val claimPeriod = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))
    val expected = FurloughPeriod(claimPeriod.start, claimPeriod.end)

    extractFurloughPeriod(userAnswers) mustBe Some(expected)
  }

  "Extract furlough period with end date matching the claim end date with user submitted start date" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson("no", "startedInClaim", "2020-03-15")).as[UserAnswers]
    val claimPeriod = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))
    val expected = FurloughPeriod(LocalDate.of(2020, 3, 15), claimPeriod.end)

    extractFurloughPeriod(userAnswers) mustBe Some(expected)
  }

  "Extract furlough period with start date matching the claim start date with user submitted end date" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson("no", "endedInClaim", furloughEndDate = "2020-04-15")).as[UserAnswers]
    val claimPeriod = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))
    val expected = FurloughPeriod(claimPeriod.start, LocalDate.of(2020, 4, 15))

    extractFurloughPeriod(userAnswers) mustBe Some(expected)
  }

  "Extract furlough period with user submitted start and end date" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson("no", "startedAndEndedInClaim", "2020-03-15", "2020-04-15")).as[UserAnswers]
    val claimPeriod = PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 4, 30))
    val expected = FurloughPeriod(LocalDate.of(2020, 3, 15), LocalDate.of(2020, 4, 15))

    extractFurloughPeriod(userAnswers) mustBe Some(expected)
  }

  "Extract salary when payQuestion answer is Regularly" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
    val expected = Salary(2000.0)

    extractGrossPay(userAnswers) mustBe Some(expected)
  }

  "Extract prior furlough period from user answers" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson(employeeStartDate = "2020-12-1")).as[UserAnswers]
    val expected = PayPeriod(LocalDate.of(2020, 12, 1), LocalDate.of(2020, 2, 29))
  }

  "Extract variable gross pay when payQuestion answer is Varies" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson(payQuestion = "varies", variableGrossPay = "2400.0")).as[UserAnswers]
    val expected = Salary(2400.0)

    extractGrossPay(userAnswers) mustBe Some(expected)
  }

  "Extract regular payments for employees that are paid a regular amount each time" in new DataExtractor {
    val userAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
    val expected = Seq(
      RegularPayment(Salary(2000.0), PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      RegularPayment(Salary(2000.0), PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)))
    )

    extractRegularPayments(userAnswers) mustBe Some(expected)
  }

  "Extract regular payments for employees that are paid a variable amount each time" in new DataExtractor {
    val userAnswers =
      Json.parse(userAnswersJson(payQuestion = "varies", variableGrossPay = "2400.00", employeeStartDate = "2019-12-01")).as[UserAnswers]
    val expected = Seq(
      RegularPayment(Salary(817.47), PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31))),
      RegularPayment(Salary(791.10), PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)))
    )

    extractRegularPayments(userAnswers) mustBe Some(expected)
  }

}
