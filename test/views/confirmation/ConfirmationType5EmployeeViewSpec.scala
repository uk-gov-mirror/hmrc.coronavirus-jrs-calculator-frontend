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
import messages.JRSExtensionConfirmationMessages.VariableExtensionType5._
import models.FurloughStatus.FurloughOngoing
import models.PartTimeQuestion.PartTimeNo
import models.PayMethod.Variable
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{EmployeeStarted, Period, UserAnswers}
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers.apr6th2020
import utils.ValueFormatter
import viewmodels.{ConfirmationDataResultWithoutNicAndPension, ConfirmationViewBreakdownWithoutNicAndPension}
import views.behaviours.ViewBehaviours
import views.html.JrsExtensionConfirmationView

import java.time.LocalDate
import utils.LocalDateHelpers._

class ConfirmationType5EmployeeViewSpec
    extends SpecBase with ConfirmationControllerRequestHandler with ValidatedValues with ValueFormatter with ViewBehaviours {

  val extConfirmationView: JrsExtensionConfirmationView = injector.instanceOf[JrsExtensionConfirmationView]

  val novClaimPeriod: Period = Period(
    LocalDate.of(2020, 11, 1),
    LocalDate.of(2020, 11, 30)
  )

  val mayClaimPeriod: Period = Period(
    LocalDate.of(2021, 5, 10),
    LocalDate.of(2021, 5, 25)
  )

  def nov2020Type5aJourney(): UserAnswers =
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
      .withEmployeeStartDate("2020, 3, 20")
      .withOnPayrollBefore30thOct2020()
      .withPreviousFurloughedPeriodsAnswer(true)
      .withFirstFurloughDate("2020, 11, 10")
      .withPayDate(List("2020, 10, 31", "2020, 12, 1"))
      .withAnnualPayAmount(10000.00)
      .withPartTimeQuestion(PartTimeNo)

  def may2021Type5bJourney(): UserAnswers =
    emptyUserAnswers
      .withClaimPeriodStart("2021, 5, 10")
      .withClaimPeriodEnd("2021, 5, 25")
      .withFurloughStartDate("2021, 5, 10")
      .withFurloughStatus(FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withNiCategory()
      .withPensionStatus()
      .withPayMethod(Variable)
      .withFurloughInLastTaxYear(false)
      .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
      .withEmployeeStartDate("2021, 1, 1")
      .withOnPayrollBefore30thOct2020(false)
      .withPreviousFurloughedPeriodsAnswer(true)
      .withFirstFurloughDate("2021, 5, 2")
      .withPayDate(List("2020, 10, 31", "2020, 12, 1"))
      .withAnnualPayAmount(10000.00)
      .withPartTimeQuestion(PartTimeNo)

  "for type 5a" when {

    "display the correct text for the breakdown explanation paragraph" in {

      val userAnswers: UserAnswers = nov2020Type5aJourney()

      implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

      val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
      }

      def applyView(): HtmlFormat.Appendable =
        extConfirmationView(cvb = noNicAndPensionBreakdown, claimPeriod = novClaimPeriod, version = "2", isNewStarterType5 = true)

      implicit val doc: Document = asDocument(applyView())

      doc.toString.contains(breakdownP1(dateToString(apr6th2020), dateToString(LocalDate.parse("2020-11-09")))) mustBe true
      doc.toString.contains(statLeaveOnly(Some(dateToString(apr6th2020)), dateToString(LocalDate.parse("2020-11-09")))) mustBe false
    }

    "show the stat leave only section" in {

      val userAnswers: UserAnswers = nov2020Type5aJourney()
        .withStatutoryLeaveDays(1)
        .withStatutoryLeaveAmount(100)

      implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

      val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
      }

      def applyView(): HtmlFormat.Appendable =
        extConfirmationView(cvb = noNicAndPensionBreakdown, claimPeriod = novClaimPeriod, version = "2", isNewStarterType5 = true)

      implicit val doc: Document = asDocument(applyView())

      doc.toString.contains(statLeaveOnly(Some(dateToString(apr6th2020)), dateToString(LocalDate.parse("2020-11-09")))) mustBe true
    }
  }

  "for type 5b" when {

    "display the correct text for the breakdown explanation paragraph" in {

      val userAnswers: UserAnswers = may2021Type5bJourney()

      implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

      val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
      }

      def applyView(): HtmlFormat.Appendable =
        extConfirmationView(cvb = noNicAndPensionBreakdown, claimPeriod = mayClaimPeriod, version = "2", isNewStarterType5 = true)

      implicit val doc: Document = asDocument(applyView())

      doc.toString.contains(breakdownP1(BeenOnStatutoryLeaveMessages.dayEmploymentStarted, dateToString(LocalDate.parse("2021-05-01")))) mustBe true
      doc.toString.contains(statLeaveOnly(None, dateToString(LocalDate.parse("2021-05-01")))) mustBe false
    }

    "display stat leave only section" in {

      val userAnswers: UserAnswers = may2021Type5bJourney()
        .withStatutoryLeaveDays(1)
        .withStatutoryLeaveAmount(100)

      implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

      val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
      }

      def applyView(): HtmlFormat.Appendable =
        extConfirmationView(cvb = noNicAndPensionBreakdown, claimPeriod = mayClaimPeriod, version = "2", isNewStarterType5 = true)

      implicit val doc: Document = asDocument(applyView())
      doc.toString.contains(statLeaveOnly(None, dateToString(LocalDate.parse("2021-05-01")))) mustBe true
    }
  }
}
