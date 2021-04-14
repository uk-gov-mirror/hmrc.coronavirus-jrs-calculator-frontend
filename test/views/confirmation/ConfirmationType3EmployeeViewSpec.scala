/*
 * Copyright 2021 HM Revenue & Customs
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

package views.confirmation

import base.SpecBase
import cats.scalatest.ValidatedValues
import handlers.ConfirmationControllerRequestHandler
import messages.JRSExtensionConfirmationMessages.Type3._
import models.FurloughStatus.FurloughOngoing
import models.PartTimeQuestion.PartTimeNo
import models.PayMethod.Variable
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{EightyPercent, EmployeeStarted, Period, UserAnswers}
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers._
import utils.{LocalDateHelpers, ValueFormatter}
import viewmodels.ConfirmationDataResultWithoutNicAndPension
import views.behaviours.ViewBehaviours
import views.html.JrsExtensionConfirmationView

import java.time.LocalDate

class ConfirmationType3EmployeeViewSpec
    extends SpecBase with ConfirmationControllerRequestHandler with ValidatedValues with ValueFormatter with ViewBehaviours
    with LocalDateHelpers {

  val extConfirmationView: JrsExtensionConfirmationView = injector.instanceOf[JrsExtensionConfirmationView]

  val novClaimPeriod: Period = Period(
    LocalDate.of(2020, 11, 1),
    LocalDate.of(2020, 12, 1)
  )

  def nov2020Type3Journey(): UserAnswers =
    emptyUserAnswers
      .withClaimPeriodStart("2020, 11, 1")
      .withClaimPeriodEnd("2020, 11, 30")
      .withFurloughStartDate("2020, 11, 15")
      .withFurloughStatus(FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withNiCategory()
      .withPensionStatus()
      .withPayMethod(Variable)
      .withFurloughInLastTaxYear(false)
      .withVariableLengthEmployed(EmployeeStarted.OnOrBefore1Feb2019)
      .withPreviousFurloughedPeriodsAnswer(true)
      .withFirstFurloughDate("2020, 4, 2")
      .withPayDate(List("2020, 10, 31", "2020, 12, 1"))
      .withLastYear(List("2019-12-01" -> 100))
      .withAnnualPayAmount(10000.00)
      .withPartTimeQuestion(PartTimeNo)

  "display the correct text for method 2 breakdown summary" in {

    val userAnswers: UserAnswers = nov2020Type3Journey()

    implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

    val noNicAndPensionBreakdown = {
      loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
    }

    def applyView(): HtmlFormat.Appendable =
      extConfirmationView(cvb = noNicAndPensionBreakdown,
                          claimPeriod = novClaimPeriod,
                          version = "2",
                          isNewStarterType5 = false,
                          EightyPercent)

    implicit val doc: Document = asDocument(applyView())

    doc.toString.contains(method2BreadownSummary(dateToString(LocalDate.parse("2020-04-01")))) mustBe true
    doc.toString.contains(statLeaveOnly(dateToString(apr6th2019), dateToString(LocalDate.parse("2020-04-01")))) mustBe false
  }

  "show the stat leave only section" in {

    val userAnswers: UserAnswers = nov2020Type3Journey()
      .withStatutoryLeaveDays(1)
      .withStatutoryLeaveAmount(100)

    implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

    val noNicAndPensionBreakdown = {
      loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
    }

    def applyView(): HtmlFormat.Appendable =
      extConfirmationView(cvb = noNicAndPensionBreakdown,
                          claimPeriod = novClaimPeriod,
                          version = "2",
                          isNewStarterType5 = false,
                          EightyPercent)

    implicit val doc: Document = asDocument(applyView())

    doc.toString.contains(method2BreadownSummary(dateToString(LocalDate.parse("2020-04-01")))) mustBe true
    doc.toString.contains(statLeaveOnly(dateToString(apr6th2019), dateToString(LocalDate.parse("2020-04-01")))) mustBe true
  }
}
