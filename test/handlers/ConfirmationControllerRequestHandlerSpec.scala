/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import base.SpecBase
import models.Calculation.{FurloughCalculationResult, NicCalculationResult, PensionCalculationResult}
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
    val furlough = CalculationResult(FurloughCalculationResult, 3200.00, List(periodBreakdownOne(1600.00), periodBreakdownTwo(1600.00)))
    val nic = CalculationResult(NicCalculationResult, 241.36, List(periodBreakdownOne(121.58), periodBreakdownTwo(119.78)))
    val pension = CalculationResult(PensionCalculationResult, 65.07, List(periodBreakdownOne(32.67), periodBreakdownTwo(32.40)))

    val expected = ConfirmationViewBreakdown(furlough, nic, pension)

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe expected //TODO metadata to be tested
  }

  "for a given user answer calculate furlough and empty results for ni and pension if do not apply" in new ConfirmationControllerRequestHandler {
    val userAnswers = Json.parse(jsStringWithNoNiNoPension).as[UserAnswers]
    val withPayDay: PayPeriodWithPayDay =
      PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 3, 31)), PaymentDate(LocalDate.of(2020, 3, 31)))
    val withPayDayTwo: PayPeriodWithPayDay =
      PayPeriodWithPayDay(PayPeriod(LocalDate.of(2020, 4, 1), LocalDate.of(2020, 4, 30)), PaymentDate(LocalDate.of(2020, 4, 20)))

    val payPeriodBreakdowns = List(PayPeriodBreakdown(1600.0, withPayDay), PayPeriodBreakdown(1600.0, withPayDayTwo))
    val nicPayPeriodBreakdowns = List(PayPeriodBreakdown(0.0, withPayDay), PayPeriodBreakdown(0.0, withPayDayTwo))
    val pensionPayPeriodBreakdowns = List(PayPeriodBreakdown(0.0, withPayDay), PayPeriodBreakdown(0.0, withPayDayTwo))

    loadResultData(userAnswers).get.confirmationViewBreakdown mustBe ConfirmationViewBreakdown(
      CalculationResult(FurloughCalculationResult, 3200.0, payPeriodBreakdowns),
      CalculationResult(NicCalculationResult, 0.0, nicPayPeriodBreakdowns),
      CalculationResult(PensionCalculationResult, 0.0, pensionPayPeriodBreakdowns)
    ) //TODO metadata to be tested
  }

  private val userAnswersJson: String =
    """
      |{
      |    "_id" : "session-3fdd2682-dad1-48e1-80d6-8c1480696811",
      |    "data" : {
      |        "taxYearPayDate" : "2020-04-20",
      |        "furloughQuestion" : "yes",
      |        "payQuestion" : "regularly",
      |        "pensionAutoEnrolment" : "optedIn",
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

  private val jsStringWithNoNiNoPension: String =
    """{
      |  "_id": "session-9dee1ca2-1378-479b-92f8-748de7f363d5",
      |  "data": {
      |    "taxYearPayDate" : "2020-04-20",
      |    "furloughQuestion": "yes",
      |    "payQuestion": "regularly",
      |    "pensionAutoEnrolment": "optedOut",
      |    "claimPeriodEnd": "2020-04-30",
      |    "paymentFrequency": "monthly",
      |    "salary": {
      |      "amount": 2000
      |    },
      |    "nicCategory": "nonPayable",
      |    "claimPeriodStart": "2020-03-01",
      |    "payDate": [
      |      "2020-02-29",
      |      "2020-03-31",
      |      "2020-04-30"
      |    ]
      |  },
      |  "lastUpdated": {
      |    "$date": 1586873457650
      |  }
      |}""".stripMargin

}
