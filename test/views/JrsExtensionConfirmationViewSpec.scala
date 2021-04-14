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

package views

import assets.messages.PhaseTwoReferencePayBreakdownHelperMessages
import cats.scalatest.ValidatedValues
import handlers.ConfirmationControllerRequestHandler
import messages.JRSExtensionConfirmationMessages._
import models.FurloughStatus.FurloughOngoing
import models.PartTimeQuestion.PartTimeNo
import models.PayMethod.Variable
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{EightyPercent, EmployeeStarted, Period, UserAnswers}
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import utils.LocalDateHelpers.apr6th2020
import utils.{LocalDateHelpers, ValueFormatter}
import viewmodels.{ConfirmationDataResultWithoutNicAndPension, ConfirmationViewBreakdownWithoutNicAndPension}
import views.behaviours.ViewBehaviours
import views.html.JrsExtensionConfirmationView

import java.time.LocalDate

class JrsExtensionConfirmationViewSpec
    extends ViewBehaviours with ConfirmationControllerRequestHandler with ValidatedValues with ValueFormatter with LocalDateHelpers {

  val messageKeyPrefix                   = "confirmation"
  val view: JrsExtensionConfirmationView = injector.instanceOf[JrsExtensionConfirmationView]

  object RegularEmployeeTypeOneSelectors extends BaseSelectors {
    val nonGreenContentParagraphChild: Int => String = (i: Int) => s"#main-content > div > div > div > p:nth-child($i)"
    val dateAndCalculatorVersion: String             = nonGreenContentParagraphChild(3)
    val disclaimer: String                           = nonGreenContentParagraphChild(4)
    val nextStepsNumberedList: Int => String =
      i => s"#main-content > div > div > div > ol.govuk-list.govuk-list--number > li:nth-child($i)"
    val calculatePayList: Int => String       = i => s"#main-content > div > div > div > ol:nth-child(15) > li:nth-child($i)"
    val furloughGrantList: Int => String      = i => s"#main-content > div > div > div > ol:nth-child(18) > li:nth-child($i)"
    val breakdownParagraphOne: String         = nonGreenContentParagraphChild(9)
    val breakdownParagraphTwo: String         = nonGreenContentParagraphChild(10)
    val breakdownParagraphThree: String       = nonGreenContentParagraphChild(11)
    val h4CalculatePayParagraphOne: String    = nonGreenContentParagraphChild(14)
    val h4CalculatePayParagraphTwo: String    = nonGreenContentParagraphChild(16)
    val h4FurloughGrantParagraphOne: String   = nonGreenContentParagraphChild(19)
    val h4FurloughGrantParagraphTwo: String   = nonGreenContentParagraphChild(20)
    val h4FurloughGrantParagraphThree: String = nonGreenContentParagraphChild(21)
    val furloughGrantInset: String            = "#total-furlough-grant"
    val bottomDisclaimer: String              = nonGreenContentParagraphChild(25)
    val printLink: String                     = "#main-content div > p:nth-child(26) > a"
    val webChatLink: String                   = "#main-content > div > div > div > p:nth-child(27) > a"
    val feedbackLink: String                  = "#main-content > div > div > div > p:nth-child(28) > a"
  }

  val decClaimPeriod: Period = Period(
    LocalDate.of(2020, 12, 1),
    LocalDate.of(2020, 12, 31)
  )

  def dec2020Journey(): UserAnswers =
    emptyUserAnswers
      .withClaimPeriodStart("2020, 12, 1")
      .withClaimPeriodEnd("2021, 12, 31")
      .withFurloughStartDate("2020, 3, 20")
      .withFurloughStatus(FurloughOngoing)
      .withPaymentFrequency(Monthly)
      .withNiCategory()
      .withPensionStatus()
      .withPayMethod()
      .withFurloughInLastTaxYear(false)
      .withVariableLengthEmployed(EmployeeStarted.After1Feb2019)
      .withEmployeeStartDate("2020, 3, 20")
      .withOnPayrollBefore30thOct2020()
      .withPayDate(List("2020, 11, 30", "2020, 12, 31"))
      .withLastPayDate("2020, 11, 30")
      .withPartTimeQuestion(PartTimeNo)
      .withRegularPayAmount(10000.00)

  val userAnswers: UserAnswers = dec2020Journey()
  val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
    loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
  }

  val nextStepsListMessage: Int => String = (bullet: Int) => nextStepsListMessages(bullet, decClaimPeriod)
  val calculatePayListMessage: Int => String = { (bullet: Int) =>
    RegularType1.calculatePayListMessages(bullet, 10000, 31, 31)
  }
  val furloughGrantListMessage: Int => String = { (bullet: Int) =>
    RegularType1.furloughGrantListMessages(bullet, 10000, 80)
  }

  val expectedContent = Seq(
    RegularEmployeeTypeOneSelectors.h1                            -> heading,
    RegularEmployeeTypeOneSelectors.dateAndCalculatorVersion      -> dateAndCalculatorVersion(dateToString(LocalDate.now()), "2"),
    RegularEmployeeTypeOneSelectors.indent                        -> AdditionalPaymentBlock.stillPayNICandPension,
    RegularEmployeeTypeOneSelectors.disclaimer                    -> disclaimerTopPage,
    RegularEmployeeTypeOneSelectors.h2(1)                         -> h2NextSteps,
    RegularEmployeeTypeOneSelectors.nextStepsNumberedList(1)      -> nextStepsListMessage(1),
    RegularEmployeeTypeOneSelectors.nextStepsNumberedList(2)      -> nextStepsListMessage(2),
    RegularEmployeeTypeOneSelectors.nextStepsNumberedList(3)      -> nextStepsListMessage(3),
    RegularEmployeeTypeOneSelectors.nextStepsNumberedList(4)      -> nextStepsListMessage(4),
    RegularEmployeeTypeOneSelectors.nextStepsNumberedList(5)      -> nextStepsListMessage(5),
    RegularEmployeeTypeOneSelectors.h2(2)                         -> RegularType1.h2BreakdownOfCalculations,
    RegularEmployeeTypeOneSelectors.breakdownParagraphOne         -> RegularType1.breakDownParagraphOne,
    RegularEmployeeTypeOneSelectors.breakdownParagraphTwo         -> RegularType1.breakDownParagraphTwo,
    RegularEmployeeTypeOneSelectors.breakdownParagraphThree       -> RegularType1.breakDownParagraphThree,
    RegularEmployeeTypeOneSelectors.h3(1)                         -> RegularType1.h3PayPeriod(decClaimPeriod),
    RegularEmployeeTypeOneSelectors.h4(1)                         -> RegularType1.h4CalculatePay,
    RegularEmployeeTypeOneSelectors.h4CalculatePayParagraphOne    -> RegularType1.h4ParagraphOne,
    RegularEmployeeTypeOneSelectors.calculatePayList(1)           -> calculatePayListMessage(1),
    RegularEmployeeTypeOneSelectors.calculatePayList(2)           -> calculatePayListMessage(2),
    RegularEmployeeTypeOneSelectors.calculatePayList(3)           -> calculatePayListMessage(3),
    RegularEmployeeTypeOneSelectors.h4CalculatePayParagraphTwo    -> RegularType1.h4ParagraphTwo(10000),
    RegularEmployeeTypeOneSelectors.h4(2)                         -> RegularType1.h4FurloughGrant,
    RegularEmployeeTypeOneSelectors.furloughGrantList(1)          -> furloughGrantListMessage(1),
    RegularEmployeeTypeOneSelectors.furloughGrantList(2)          -> furloughGrantListMessage(2),
    RegularEmployeeTypeOneSelectors.h4FurloughGrantParagraphOne   -> RegularType1.furloughGrantParagraphOne(8000),
    RegularEmployeeTypeOneSelectors.h4FurloughGrantParagraphTwo   -> RegularType1.furloughGrantParagraphTwo(2500),
    RegularEmployeeTypeOneSelectors.h4FurloughGrantParagraphThree -> RegularType1.furloughGrantParagraphThree(2500),
    RegularEmployeeTypeOneSelectors.furloughGrantInset            -> RegularType1.furloughGrantIndent(2500),
    RegularEmployeeTypeOneSelectors.bottomDisclaimer              -> RegularType1.disclaimerBottomPage,
    RegularEmployeeTypeOneSelectors.printLink                     -> RegularType1.printOrSave,
    RegularEmployeeTypeOneSelectors.webChatLink                   -> RegularType1.webchatLink,
    RegularEmployeeTypeOneSelectors.feedbackLink                  -> RegularType1.feedbackLink
  )

  implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

  def applyView(): HtmlFormat.Appendable =
    view(cvb = noNicAndPensionBreakdown, claimPeriod = decClaimPeriod, version = "2", isNewStarterType5 = false, EightyPercent)

  implicit val doc: Document = asDocument(applyView())

  "JRSExtensionConfirmationViewSpec" when {

    "for a Regular Pay Pre-Covid Employee (Employee Type 1)" should {

      behave like normalPage(messageKeyPrefix)
      behave like pageWithHeading(heading = heading)
      behave like pageWithExpectedMessages(expectedContent)

      "behave like a page with a StartAnotherCalculation button" must {

        s"have a button with message '${VariableExtensionType5.startAnotherCalculation}'" in {
          assertEqualsMessage(doc, "#main-content > div > div > div > a", VariableExtensionType5.startAnotherCalculation)
        }
      }

      "behave like a page with correct links" must {

        "have a PrintOrSave link - brings up window.print() when clicked" in {
          doc.select(RegularEmployeeTypeOneSelectors.printLink).attr("onClick") mustBe "window.print();"
        }

        "have a Webchat link - opens a webchat/contact details page" in {
          doc.select(RegularEmployeeTypeOneSelectors.webChatLink).attr("href") mustBe frontendAppConfig.webchatHelpUrl
        }

        "have a Feedback link" in {
          doc.select(RegularEmployeeTypeOneSelectors.feedbackLink).attr("href") mustBe "/job-retention-scheme-calculator/start-survey"
        }
      }
    }
  }

}

class EmployeeType5JrsExtensionConfirmationViewSpec
    extends ViewBehaviours with ConfirmationControllerRequestHandler with ValidatedValues with ValueFormatter {

  val messageKeyPrefix                   = "confirmation"
  val view: JrsExtensionConfirmationView = injector.instanceOf[JrsExtensionConfirmationView]

  object VariableEmployeeTypeFiveSelectors extends BaseSelectors {
    val nonGreenContentParagraphChild: Int => String = (i: Int) => s"#main-content > div > div > div > p:nth-child($i)"
    val dateAndCalculatorVersion: String             = nonGreenContentParagraphChild(3)
    val disclaimer: String                           = nonGreenContentParagraphChild(4)
    val nextStepsNumberedList: Int => String =
      i => s"#main-content > div > div > div > ol.govuk-list.govuk-list--number > li:nth-child($i)"
    val calculatePayList: Int => String       = i => s"#main-content > div > div > div > ol:nth-child(15) > li:nth-child($i)"
    val furloughGrantList: Int => String      = i => s"#main-content > div > div > div > ol:nth-child(18) > li:nth-child($i)"
    val breakdownParagraphOne: String         = nonGreenContentParagraphChild(9)
    val breakdownParagraphTwo: String         = nonGreenContentParagraphChild(10)
    val breakdownParagraphThree: String       = nonGreenContentParagraphChild(11)
    val h4CalculatePayParagraphOne: String    = nonGreenContentParagraphChild(14)
    val h4CalculatePayParagraphTwo: String    = nonGreenContentParagraphChild(16)
    val h4FurloughGrantParagraphThree: String = nonGreenContentParagraphChild(21)
    val furloughGrantInset: String            = "#total-furlough-grant"
    val bottomDisclaimer: String              = nonGreenContentParagraphChild(22)
    val printLink: String                     = "#main-content div > p:nth-child(23) > a"
    val webChatLink: String                   = "#main-content > div > div > div > p:nth-child(24) > a"
    val feedbackLink: String                  = "#main-content > div > div > div > p:nth-child(25) > a"
  }

  val novClaimPeriod: Period = Period(
    LocalDate.of(2020, 11, 1),
    LocalDate.of(2020, 12, 1)
  )

  def nov2020Type5Journey(): UserAnswers =
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

  val userAnswers: UserAnswers = nov2020Type5Journey()

  val nextStepsListMessage: Int => String = (bullet: Int) => nextStepsListMessages(bullet, novClaimPeriod)

  val furloughGrantListMessage: Int => String = { (bullet: Int) =>
    VariableExtensionType5.furloughGrantListMessages(bullet, 733.92, 80)
  }

  val expectedContent = Seq(
    VariableEmployeeTypeFiveSelectors.h1                       -> heading,
    VariableEmployeeTypeFiveSelectors.dateAndCalculatorVersion -> dateAndCalculatorVersion(dateToString(LocalDate.now()), "2"),
    VariableEmployeeTypeFiveSelectors.indent                   -> AdditionalPaymentBlock.stillPayNICandPension,
    VariableEmployeeTypeFiveSelectors.disclaimer               -> disclaimerTopPage,
    VariableEmployeeTypeFiveSelectors.h2(1)                    -> h2NextSteps,
    VariableEmployeeTypeFiveSelectors.nextStepsNumberedList(1) -> nextStepsListMessage(1),
    VariableEmployeeTypeFiveSelectors.nextStepsNumberedList(2) -> nextStepsListMessage(2),
    VariableEmployeeTypeFiveSelectors.nextStepsNumberedList(3) -> nextStepsListMessage(3),
    VariableEmployeeTypeFiveSelectors.nextStepsNumberedList(4) -> nextStepsListMessage(4),
    VariableEmployeeTypeFiveSelectors.nextStepsNumberedList(5) -> nextStepsListMessage(5),
    VariableEmployeeTypeFiveSelectors.h2(2)                    -> VariableExtensionType5.h2BreakdownOfCalculations,
    VariableEmployeeTypeFiveSelectors.breakdownParagraphOne -> VariableExtensionType5
      .breakdownP1(dateToString(apr6th2020), dateToString(LocalDate.parse("2020-11-09"))),
    VariableEmployeeTypeFiveSelectors.breakdownParagraphTwo   -> VariableExtensionType5.breakDownParagraphTwo,
    VariableEmployeeTypeFiveSelectors.breakdownParagraphThree -> VariableExtensionType5.breakDownParagraphThree,
    VariableEmployeeTypeFiveSelectors.h3(1)                   -> VariableExtensionType5.h3PayPeriod(novClaimPeriod),
    VariableEmployeeTypeFiveSelectors.h4(1)                   -> VariableExtensionType5.h4CalculatePay,
    VariableEmployeeTypeFiveSelectors.h4CalculatePayParagraphOne ->
      PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p1(isType5 = true),
    VariableEmployeeTypeFiveSelectors.calculatePayList(1) ->
      PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered1(10000, hasStatLeave = false),
    VariableEmployeeTypeFiveSelectors.calculatePayList(2) ->
      PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered2(218, hasStatLeave = false),
    VariableEmployeeTypeFiveSelectors.calculatePayList(3)        -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered3(16),
    VariableEmployeeTypeFiveSelectors.h4CalculatePayParagraphTwo -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p2(733.92),
    VariableEmployeeTypeFiveSelectors.h4(2)                      -> VariableExtensionType5.h4FurloughGrant,
    VariableEmployeeTypeFiveSelectors.furloughGrantList(1)       -> furloughGrantListMessage(1),
    VariableEmployeeTypeFiveSelectors.furloughGrantList(2)       -> furloughGrantListMessage(2),
    VariableEmployeeTypeFiveSelectors.furloughGrantInset         -> VariableExtensionType5.furloughGrantIndent(587.14),
    VariableEmployeeTypeFiveSelectors.bottomDisclaimer           -> VariableExtensionType5.disclaimerBottomPage,
    VariableEmployeeTypeFiveSelectors.printLink                  -> VariableExtensionType5.printOrSave,
    VariableEmployeeTypeFiveSelectors.webChatLink                -> VariableExtensionType5.webchatLink,
    VariableEmployeeTypeFiveSelectors.feedbackLink               -> VariableExtensionType5.feedbackLink
  )

  val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
    loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
  }

  implicit val request: DataRequest[_] = fakeDataRequest(userAnswers)

  def applyView(): HtmlFormat.Appendable =
    view(cvb = noNicAndPensionBreakdown, claimPeriod = novClaimPeriod, version = "2", isNewStarterType5 = true, EightyPercent)

  implicit val doc: Document = asDocument(applyView())

  "for a Variable Pay New Starter Employee (Employee Type 5)" should {

    behave like normalPage(messageKeyPrefix)
    behave like pageWithHeading(heading = heading)
    behave like pageWithExpectedMessages(expectedContent)

    "behave like a page with a StartAnotherCalculation button" must {

      s"have a button with message '${VariableExtensionType5.startAnotherCalculation}'" in {
        assertEqualsMessage(doc, "#main-content > div > div > div > a", VariableExtensionType5.startAnotherCalculation)
      }
    }

    "behave like a page with correct links" must {

      "have a PrintOrSave link - brings up window.print() when clicked" in {
        doc.select(VariableEmployeeTypeFiveSelectors.printLink).attr("onClick") mustBe "window.print();"
      }

      "have a Webchat link - opens a webchat/contact details page" in {
        doc.select(VariableEmployeeTypeFiveSelectors.webChatLink).attr("href") mustBe frontendAppConfig.webchatHelpUrl
      }

      "have a Feedback link" in {
        doc.select(VariableEmployeeTypeFiveSelectors.feedbackLink).attr("href") mustBe "/job-retention-scheme-calculator/start-survey"
      }
    }
  }
}
