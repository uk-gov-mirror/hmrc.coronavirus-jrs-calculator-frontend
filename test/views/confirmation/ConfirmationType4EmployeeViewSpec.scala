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

import assets.messages.BeenOnStatutoryLeaveMessages
import base.SpecBase
import cats.scalatest.ValidatedValues
import handlers.ConfirmationControllerRequestHandler
import messages.JRSExtensionConfirmationMessages.Type4._
import models.FurloughStatus.FurloughOngoing
import models.PartTimeQuestion.PartTimeNo
import models.PayMethod.Variable
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{EightyPercent, EmployeeStarted, Period, UserAnswers}
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers._
import utils.ValueFormatter
import viewmodels.{ConfirmationDataResultWithoutNicAndPension, ConfirmationViewBreakdownWithoutNicAndPension}
import views.behaviours.ViewBehaviours
import views.html.JrsExtensionConfirmationView

import java.time.LocalDate

class ConfirmationType4EmployeeViewSpec
    extends SpecBase with ConfirmationControllerRequestHandler with ValidatedValues with ValueFormatter with ViewBehaviours {

  val extConfirmationView: JrsExtensionConfirmationView = injector.instanceOf[JrsExtensionConfirmationView]

  val novClaimPeriod: Period = Period(
    LocalDate.of(2020, 11, 1),
    LocalDate.of(2020, 12, 1)
  )

  def nov2020Type4Journey(): UserAnswers =
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
      .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
      .withEmployeeStartDate("2020, 1, 31")
      .withPreviousFurloughedPeriodsAnswer(true)
      .withFirstFurloughDate("2020, 04, 02")
      .withPayDate(List("2020, 10, 31", "2020, 12, 1"))
      .withAnnualPayAmount(10000.00)
      .withPartTimeQuestion(PartTimeNo)

  "display the correct paragraph text for averaging calc" in {

    val userAnswers: UserAnswers = nov2020Type4Journey()

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

    doc.toString.contains(averageP1) mustBe true
    doc.toString.contains(statLeaveOnly(dateToString(LocalDate.parse("2020-04-01")))) mustBe false
  }

  "display the correct text for the breakdown explanation paragraph" in {

    val userAnswers: UserAnswers = nov2020Type4Journey()

    implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

    val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
      loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
    }

    def applyView(): HtmlFormat.Appendable =
      extConfirmationView(cvb = noNicAndPensionBreakdown,
                          claimPeriod = novClaimPeriod,
                          version = "2",
                          isNewStarterType5 = false,
                          EightyPercent)

    implicit val doc: Document = asDocument(applyView())

    doc.toString.contains(calculationBreakdownSummary(BeenOnStatutoryLeaveMessages.dayEmploymentStarted,
                                                      dateToString(LocalDate.parse("2020-04-01")))) mustBe true
    doc.toString.contains(statLeaveOnly(dateToString(LocalDate.parse("2020-04-01")))) mustBe false
  }

  "show the Stat Leave only section" in {

    val userAnswers: UserAnswers = nov2020Type4Journey()
      .withStatutoryLeaveDays(1)
      .withStatutoryLeaveAmount(100)

    implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

    val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
      loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
    }

    def applyView(): HtmlFormat.Appendable =
      extConfirmationView(cvb = noNicAndPensionBreakdown,
                          claimPeriod = novClaimPeriod,
                          version = "2",
                          isNewStarterType5 = false,
                          EightyPercent)

    implicit val doc: Document = asDocument(applyView())

    doc.toString.contains(statLeaveOnly(dateToString(LocalDate.parse("2020-04-01")))) mustBe true
  }
}
