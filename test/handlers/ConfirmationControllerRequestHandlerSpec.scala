/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.SpecBase
import models.{CalculationResult, PayPeriod, PayPeriodBreakdown, PayPeriodWithPayDay, PaymentDate, UserAnswers}
import play.api.libs.json.Json
import viewmodels.ConfirmationViewBreakdown

class ConfirmationControllerRequestHandlerSpec extends SpecBase {

  "do all calculations given a set of userAnswers" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(userAnswersJson).as[UserAnswers]

    def periodBreakdownOne(amount: Double) =
      PayPeriodBreakdown(
        amount,
        PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 31))))
    def periodBreakdownTwo(amount: Double) =
      PayPeriodBreakdown(
        amount,
        PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), PaymentDate(LocalDate.of(2020, 4, 20))))
    val furlough = CalculationResult(3200.00, List(periodBreakdownOne(1600.00), periodBreakdownTwo(1600.00)))
    val nic = CalculationResult(241.36, List(periodBreakdownOne(121.58), periodBreakdownTwo(119.78)))
    val pension = CalculationResult(65.07, List(periodBreakdownOne(32.67), periodBreakdownTwo(32.40)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    breakdown(userAnswers) mustBe Option(expected)
  }

  private val userAnswersJson: String =
    """
      |{
      |    "_id" : "session-3fdd2682-dad1-48e1-80d6-8c1480696811",
      |    "data" : {
      |        "taxYearPayDate" : "2020-04-20",
      |        "furloughQuestion" : "yes",
      |        "payQuestion" : "regularly",
      |        "pensionAutoEnrolment" : false,
      |        "claimPeriodEnd" : "2020-04-30",
      |        "paymentFrequency" : "monthly",
      |        "salary" : {
      |            "amount" : 2000.0
      |        },
      |        "nicCategory" : "payable",
      |        "claimPeriodStart" : "2020-03-01",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-31",
      |            "2020-04-30"
      |        ]
      |    },
      |    "lastUpdated" : {
      |        "$date": 1586873457650
      |    }
      |}
      |""".stripMargin

}
