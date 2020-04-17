/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import models.{FurloughDates, UserAnswers}
import play.api.libs.json.Json

trait CoreTestData {

  val userAnswersId = "id"
  def dummyUserAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  def userAnswersJson(
    furloughQuestion: String = "yes",
    furloughDates: String = "",
    furloughStartDate: String = "",
    furloughEndDate: String = "",
    payQuestion: String = "regularly",
    variableGrossPay: String = "",
    employeeStartDate: String = ""): String =
    s"""
       |{
       |    "_id" : "session-3fdd2682-dad1-48e1-80d6-8c1480696811",
       |    "data" : {
       |        "taxYearPayDate" : "2020-04-20",
       |        "furloughQuestion" : "$furloughQuestion",
       |        "furloughDates" : "$furloughDates",
       |        "furloughStartDate" : "$furloughStartDate",
       |        "furloughEndDate" : "$furloughEndDate",
       |        "payQuestion" : "$payQuestion",
       |        "variableGrossPay": {
       |            "amount" : "$variableGrossPay"
       |        },
       |        "employeeStartDate": "$employeeStartDate",
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
       |        "$$date": 1586873457650
       |    }
       |}
       |""".stripMargin

  val jsStringWithNoNiNoPension: String =
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
