/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import models.UserAnswers
import play.api.libs.json.Json

trait CoreTestData {

  val userAnswersId = "id"
  def dummyUserAnswers = Json.parse(userAnswersJson()).as[UserAnswers]
  def emptyUserAnswers = UserAnswers(userAnswersId, Json.obj())

  def userAnswersJson(
    furloughOngoing: String = "ongoing",
    furloughStartDate: String = "2020-03-01",
    furloughEndDate: String = "",
    payMethod: String = "regular",
    variableGrossPay: String = "",
    employeeStartDate: String = "",
    claimStartDate: String = "2020-03-01"): String =
    s"""
       |{
       |    "_id" : "session-3fdd2682-dad1-48e1-80d6-8c1480696811",
       |    "data" : {
       |        "lastPayDate" : "2020-04-20",
       |        "furloughStatus" : "$furloughOngoing",
       |        "furloughStartDate" : "$furloughStartDate",
       |        "furloughEndDate" : "$furloughEndDate",
       |        "payMethod" : "$payMethod",
       |        "variableGrossPay": {
       |            "amount" : "$variableGrossPay"
       |        },
       |        "employeeStartDate": "$employeeStartDate",
       |        "pensionStatus" : "doesContribute",
       |        "claimPeriodEnd" : "2020-04-30",
       |        "paymentFrequency" : "monthly",
       |        "regularPayAmount" : {
       |            "amount" : 2000.0
       |        },
       |        "nicCategory" : "payable",
       |        "claimPeriodStart" : "$claimStartDate",
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
      |    "furloughStatus": "ongoing",
      |    "furloughStartDate" : "2020-03-01",
      |    "payMethod": "regular",
      |    "pensionStatus": "doesNotContribute",
      |    "claimPeriodEnd": "2020-04-30",
      |    "paymentFrequency": "monthly",
      |    "regularPayAmount": {
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
      |        "furloughStatus" : "ongoing",
      |        "furloughStartDate" : "2020-03-10",
      |        "payMethod" : "regular",
      |        "pensionStatus" : "doesContribute",
      |        "claimPeriodEnd" : "2020-03-31",
      |        "paymentFrequency" : "monthly",
      |        "regularPayAmount" : {
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
      |        "furloughStatus" : "ended",
      |        "variableGrossPay" : {
      |            "amount" : 10000
      |        },
      |        "employeeStarted" : "after1Feb2019",
      |        "employeeStartDate" : "2019-12-01",
      |        "furloughEndDate" : "2020-04-20",
      |        "paymentFrequency" : "monthly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughTopUpStatus" : "notToppedUp",
      |        "PartialPayAfterFurlough" : {
      |            "value" : 800
      |        },
      |        "lastPayDate" : "2020-04-20",
      |        "PartialPayBeforeFurlough" : {
      |            "value" : 1000
      |        },
      |        "furloughStartDate" : "2020-03-10",
      |        "payMethod" : "variable",
      |        "pensionStatus" : "doesContribute",
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
      |        "furloughStatus" : "ongoing",
      |        "variableGrossPay" : {
      |            "amount" : 12960
      |        },
      |        "employeeStarted" : "after1Feb2019",
      |        "employeeStartDate" : "2019-08-01",
      |        "paymentFrequency" : "monthly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughTopUpStatus" : "notToppedUp",
      |        "lastPayDate" : "2020-03-31",
      |        "PartialPayBeforeFurlough" : {
      |            "value" : 280
      |        },
      |        "furloughStartDate" : "2020-03-05",
      |        "payMethod" : "variable",
      |        "pensionStatus" : "doesContribute",
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
       |        "furloughStatus" : "ended",
       |        "variableGrossPay" : {
       |            "amount" : 10000
       |        },
       |        "employeeStarted" : "after1Feb2019",
       |        "employeeStartDate" : "2019-12-01",
       |        "furloughEndDate" : "2020-03-21",
       |        "paymentFrequency" : "weekly",
       |        "claimPeriodStart" : "2020-03-01",
       |        "furloughTopUpStatus" : "notToppedUp",
       |        "lastPayDate" : "$lastPayDate",
       |        "furloughStartDate" : "2020-03-10",
       |        "payMethod" : "variable",
       |        "pensionStatus" : "doesContribute",
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
      |        "furloughStatus" : "ended",
      |        "variableGrossPay" : {
      |            "amount" : 10000
      |        },
      |        "employeeStarted" : "after1Feb2019",
      |        "employeeStartDate" : "2019-12-01",
      |        "furloughEndDate" : "2020-03-21",
      |        "paymentFrequency" : "fortnightly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughTopUpStatus" : "notToppedUp",
      |        "lastPayDate" : "2020-03-28",
      |        "furloughStartDate" : "2020-03-10",
      |        "payMethod" : "variable",
      |        "pensionStatus" : "doesContribute",
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
      |        "furloughStatus" : "ended",
      |        "variableGrossPay" : {
      |            "amount" : 10000
      |        },
      |        "employeeStarted" : "after1Feb2019",
      |        "employeeStartDate" : "2019-12-01",
      |        "furloughEndDate" : "2020-04-26",
      |        "paymentFrequency" : "fourweekly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughTopUpStatus" : "notToppedUp",
      |        "lastPayDate" : "2020-04-25",
      |        "furloughStartDate" : "2020-03-10",
      |        "payMethod" : "variable",
      |        "pensionStatus" : "doesContribute",
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

  val jsonCylb =
    """
      |{
      |    "_id" : "session-559ffcf7-de7b-49e6-bf4d-bbc248698ae1",
      |    "data" : {
      |        "furloughStatus" : "ongoing",
      |        "variableGrossPay" : {
      |            "amount" : 20000.0
      |        },
      |        "employeeStarted" : "after1Feb2019",
      |        "employeeStartDate" : "2019-04-03",
      |        "paymentFrequency" : "monthly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughTopUpStatus" : "notToppedUp",
      |        "lastYearPay" : [
      |            {
      |                "date" : "2019-03-30",
      |                "amount" : 1200
      |            },
      |            {
      |                "date" : "2019-04-30",
      |                "amount" : 1200
      |            }
      |        ],
      |        "lastPayDate" : "2020-04-30",
      |        "PartialPayBeforeFurlough" : {
      |            "value" : 150
      |        },
      |        "furloughStartDate" : "2020-03-15",
      |        "payMethod" : "variable",
      |        "pensionStatus" : "doesContribute",
      |        "claimPeriodEnd" : "2020-04-30",
      |        "nicCategory" : "payable",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-31",
      |            "2020-04-30"
      |        ]
      |    },
      |     "lastUpdated": {
      |       "$date": 1586873457650
      |     }
      |}
      |""".stripMargin

  val jsonCylbWithoutEmployeeStartDate =
    """
      |{
      |    "_id" : "session-559ffcf7-de7b-49e6-bf4d-bbc248698ae1",
      |    "data" : {
      |        "furloughStatus" : "ended",
      |        "variableGrossPay" : {
      |            "amount" : 31970
      |        },
      |        "employeeStarted" : "onOrBefore1Feb2019",
      |        "furloughEndDate" : "2020-03-28",
      |        "paymentFrequency" : "fourweekly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughTopUpStatus" : "notToppedUp",
      |        "lastYearPay" : [
      |            {
      |                "date" : "2019-03-02",
      |                "amount" : "3200"
      |            },
      |            {
      |                "date" : "2019-03-30",
      |                "amount" : "3400"
      |            }
      |        ],
      |        "lastPayDate" : "2020-03-28",
      |        "PartialPayBeforeFurlough" : {
      |            "value" : 100
      |        },
      |        "furloughStartDate" : "2020-03-02",
      |        "payMethod" : "variable",
      |        "pensionStatus" : "doesContribute",
      |        "claimPeriodEnd" : "2020-03-28",
      |        "nicCategory" : "payable",
      |        "payDate" : [
      |            "2020-02-29",
      |            "2020-03-28"
      |        ]
      |    },
      |     "lastUpdated": {
      |       "$date": 1586873457650
      |     }
      |}
      |""".stripMargin

  val manyPeriods =
    """
      |{
      |    "_id" : "session-7f38466c-1922-4cf8-9be2-9e9d3de9bdc2",
      |    "data" : {
      |        "furloughStatus" : "ended",
      |        "variableGrossPay" : {
      |            "amount" : 31970
      |        },
      |        "employeeStarted" : "onOrBefore1Feb2019",
      |        "furloughEndDate" : "2020-03-31",
      |        "paymentFrequency" : "weekly",
      |        "claimPeriodStart" : "2020-03-01",
      |        "furloughTopUpStatus" : "notToppedUp",
      |        "lastYearPay" : [
      |            {
      |                "date" : "2019-03-05",
      |                "amount" : "500"
      |            },
      |            {
      |                "date" : "2019-03-12",
      |                "amount" : "450"
      |            },
      |            {
      |                "date" : "2019-03-19",
      |                "amount" : "500"
      |            },
      |            {
      |                "date" : "2019-03-26",
      |                "amount" : "550"
      |            },
      |            {
      |                "date" : "2019-04-02",
      |                "amount" : "600"
      |            }
      |        ],
      |        "lastPayDate" : "2020-03-31",
      |        "PartialPayBeforeFurlough" : {
      |            "value" : 200
      |        },
      |        "furloughStartDate" : "2020-03-01",
      |        "payMethod" : "variable",
      |        "pensionStatus" : "doesContribute",
      |        "claimPeriodEnd" : "2020-03-31",
      |        "nicCategory" : "payable",
      |        "payDate" : [
      |            "2020-02-25",
      |            "2020-03-03",
      |            "2020-03-10",
      |            "2020-03-17",
      |            "2020-03-24",
      |            "2020-03-31"
      |        ]
      |    },
      |     "lastUpdated": {
      |       "$date": 1586873457650
      |     }
      |}
      |""".stripMargin
}
