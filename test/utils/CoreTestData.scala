/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package utils

import models.UserAnswers
import play.api.libs.json.Json

trait CoreTestData {

  val userAnswersId = "id"
  def dummyUserAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  def userAnswersJson(
    furloughQuestion: String = "no",
    furloughStartDate: String = "2020-03-01",
    furloughEndDate: String = "",
    payQuestion: String = "regularly",
    variableGrossPay: String = "",
    employeeStartDate: String = ""): String =
    s"""
       |{
       |    "_id" : "session-3fdd2682-dad1-48e1-80d6-8c1480696811",
       |    "data" : {
       |        "lastPayDate" : "2020-04-20",
       |        "furloughQuestion" : "$furloughQuestion",
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
      |    "lastPayDate" : "2020-04-20",
      |    "furloughQuestion": "no",
      |    "furloughStartDate" : "2020-03-01",
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

  val tempTest: String =
    """{
      |    "_id" : "session-4f8fa4d9-b46c-466f-ae9b-424cfffd841f",
      |    "data" : {
      |        "lastPayDate" : "2020-03-31",
      |        "furloughQuestion" : "no",
      |        "furloughStartDate" : "2020-03-10",
      |        "payQuestion" : "regularly",
      |        "pensionAutoEnrolment" : "optedIn",
      |        "claimPeriodEnd" : "2020-03-31",
      |        "paymentFrequency" : "monthly",
      |        "salary" : {
      |            "amount" : 3500
      |        },
      |        "nicCategory" : "payable",
      |        "claimPeriodStart" : "2020-03-01",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-31"
      |        ]
      |    },
      |  "lastUpdated": {
      |    "$date": 1586873457650
      |  }
      |}""".stripMargin

  val variableMonthlyPartial: String =
    """
      |{
      |    "_id" : "session-08e14c4d-8956-4d3a-b457-1b76e3922dd6",
      |    "data" : {
      |        "furloughQuestion" : "yes",
      |        "variableGrossPay" : {
      |            "amount" : 10000
      |        },
      |        "variableLengthEmployed" : "no",
      |        "employeeStartDate" : "2019-12-01",
      |        "furloughEndDate" : "2020-04-20",
      |        "paymentFrequency" : "monthly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughCalculations" : "no",
      |        "PartialPayAfterFurlough" : {
      |            "value" : 800
      |        },
      |        "lastPayDate" : "2020-04-20",
      |        "PartialPayBeforeFurlough" : {
      |            "value" : 1000
      |        },
      |        "furloughStartDate" : "2020-03-10",
      |        "payQuestion" : "varies",
      |        "pensionAutoEnrolment" : "optedIn",
      |        "claimPeriodEnd" : "2020-04-30",
      |        "nicCategory" : "payable",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-31",
      |            "2020-04-30"
      |        ]
      |    },
      |  "lastUpdated": {
      |    "$date": 1586873457650
      |  }
      |}
      |""".stripMargin

  val variableAveragePartial: String =
    """
      |{
      |    "_id" : "session-a33fd288-bafb-475b-9767-84df4315d230",
      |    "data" : {
      |        "furloughQuestion" : "no",
      |        "variableGrossPay" : {
      |            "amount" : 12960
      |        },
      |        "variableLengthEmployed" : "no",
      |        "employeeStartDate" : "2019-08-01",
      |        "paymentFrequency" : "monthly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughCalculations" : "no",
      |        "lastPayDate" : "2020-03-31",
      |        "PartialPayBeforeFurlough" : {
      |            "value" : 280
      |        },
      |        "furloughStartDate" : "2020-03-05",
      |        "payQuestion" : "varies",
      |        "pensionAutoEnrolment" : "optedIn",
      |        "claimPeriodEnd" : "2020-03-31",
      |        "nicCategory" : "payable",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-31"
      |        ]
      |    },
      |  "lastUpdated": {
      |    "$date": 1586873457650
      |  }
      |}
      |""".stripMargin

  def variableWeekly(lastPayDate: String = "2020-03-21"): String =
    s"""
       |{
       |    "_id" : "session-08e14c4d-8956-4d3a-b457-1b76e3922dd6",
       |    "data" : {
       |        "furloughQuestion" : "yes",
       |        "variableGrossPay" : {
       |            "amount" : 10000
       |        },
       |        "variableLengthEmployed" : "no",
       |        "employeeStartDate" : "2019-12-01",
       |        "furloughEndDate" : "2020-03-21",
       |        "paymentFrequency" : "weekly",
       |        "claimPeriodStart" : "2020-03-01",
       |        "furloughCalculations" : "no",
       |        "lastPayDate" : "$lastPayDate",
       |        "furloughStartDate" : "2020-03-10",
       |        "payQuestion" : "varies",
       |        "pensionAutoEnrolment" : "optedIn",
       |        "claimPeriodEnd" : "2020-03-21",
       |        "nicCategory" : "payable",
       |        "payDate" : [
       |            "2020-02-29",
       |            "2020-03-07",
       |            "2020-03-14",
       |            "2020-03-21"
       |        ]
       |    },
       |  "lastUpdated": {
       |    "$$date": 1586873457650
       |  }
       |}
       |""".stripMargin

  val variableFortnightly: String =
    """
      |{
      |    "_id" : "session-08e14c4d-8956-4d3a-b457-1b76e3922dd6",
      |    "data" : {
      |        "furloughQuestion" : "yes",
      |        "variableGrossPay" : {
      |            "amount" : 10000
      |        },
      |        "variableLengthEmployed" : "no",
      |        "employeeStartDate" : "2019-12-01",
      |        "furloughEndDate" : "2020-03-21",
      |        "paymentFrequency" : "fortnightly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughCalculations" : "no",
      |        "lastPayDate" : "2020-03-28",
      |        "furloughStartDate" : "2020-03-10",
      |        "payQuestion" : "varies",
      |        "pensionAutoEnrolment" : "optedIn",
      |        "claimPeriodEnd" : "2020-03-21",
      |        "nicCategory" : "payable",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-14",
      |            "2020-03-28"
      |        ]
      |    },
      |  "lastUpdated": {
      |    "$date": 1586873457650
      |  }
      |}
      |""".stripMargin

  val variableFourweekly: String =
    """
      |{
      |    "_id" : "session-08e14c4d-8956-4d3a-b457-1b76e3922dd6",
      |    "data" : {
      |        "furloughQuestion" : "yes",
      |        "variableGrossPay" : {
      |            "amount" : 10000
      |        },
      |        "variableLengthEmployed" : "no",
      |        "employeeStartDate" : "2019-12-01",
      |        "furloughEndDate" : "2020-04-26",
      |        "paymentFrequency" : "fourweekly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughCalculations" : "no",
      |        "lastPayDate" : "2020-04-25",
      |        "furloughStartDate" : "2020-03-10",
      |        "payQuestion" : "varies",
      |        "pensionAutoEnrolment" : "optedIn",
      |        "claimPeriodEnd" : "2020-03-21",
      |        "nicCategory" : "payable",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-28",
      |            "2020-04-25"
      |        ]
      |    },
      |  "lastUpdated": {
      |    "$date": 1586873457650
      |  }
      |}
      |""".stripMargin
}
