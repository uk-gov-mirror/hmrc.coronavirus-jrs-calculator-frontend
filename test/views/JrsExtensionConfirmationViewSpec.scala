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

import java.time.LocalDate

import cats.scalatest.ValidatedValues
import handlers.ConfirmationControllerRequestHandler
import messages.JRSExtensionConfirmationMessages
import models.PartTimeQuestion.PartTimeNo
import models.PaymentFrequency.Monthly
import models.requests.DataRequest
import models.{Period, UserAnswers}
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import utils.ValueFormatter
import viewmodels.{ConfirmationDataResultWithoutNicAndPension, ConfirmationViewBreakdownWithoutNicAndPension}
import views.behaviours.ViewBehaviours
import views.html.JrsExtensionConfirmationView

class JrsExtensionConfirmationViewSpec
    extends ViewBehaviours with ConfirmationControllerRequestHandler with ValidatedValues with ValueFormatter {

  object Selectors extends BaseSelectors {
    val nonGreenContentParagraphChild: Int => String = (i: Int) => s"#main-content > div > div > div > p:nth-child($i)"
    val dateAndCalculatorVersion: String = nonGreenContentParagraphChild(2)
    val disclaimer: String = nonGreenContentParagraphChild(4)
    val nextStepsNumberedList: Int => String = i => s"#main-content > div > div > div > ul.govuk-list.govuk-list--number > li:nth-child($i)"
    val calculatePayList: Int => String = i => s"#main-content > div > div > div > ol:nth-child(15) > li:nth-child($i)"
    val furloughGrantList: Int => String = i => s"#main-content > div > div > div > ol:nth-child(18) > li:nth-child($i)"
    val breakdownParagraphOne: String = nonGreenContentParagraphChild(9)
    val breakdownParagraphTwo: String = nonGreenContentParagraphChild(10)
    val breakdownParagraphThree: String = nonGreenContentParagraphChild(11)
    val h4CalculatePayParagraphOne: String = nonGreenContentParagraphChild(14)
    val h4CalculatePayParagraphTwo: String = nonGreenContentParagraphChild(16)
    val h4FurloughGrantParagraphOne: String = nonGreenContentParagraphChild(19)
    val h4FurloughGrantParagraphTwo: String = nonGreenContentParagraphChild(20)
    val h4FurloughGrantParagraphThree: String = nonGreenContentParagraphChild(21)
    val furloughGrantInset: String = "#total-furlough-grant"
    val bottomDisclaimer: String = nonGreenContentParagraphChild(25)
    val printLink: String = "#main-content div > p:nth-child(26) > a"
    val webChatLink: String = "#main-content > div > div > div > p:nth-child(27) > a"
    val feedbackLink: String = "#main-content > div > div > div > p:nth-child(28) > a"
  }

  val messageKeyPrefix = "confirmation"
  val view: JrsExtensionConfirmationView = injector.instanceOf[JrsExtensionConfirmationView]

  "JRSExtensionConfirmationViewSpec" when {

    "for a Regular Pay Pre-Covid Employee (Employee Type 1)" should {

      val decClaimPeriod: Period = Period(
        LocalDate.of(2020, 12, 1),
        LocalDate.of(2020, 12, 31)
      )

      def dec2020Journey(): UserAnswers =
        emptyUserAnswers
          .withClaimPeriodStart("2020, 12, 1")
          .withClaimPeriodEnd("2021, 12, 31")
          .withFurloughStartDate("2020, 3, 20")
          .withFurloughStatus()
          .withPaymentFrequency(Monthly)
          .withNiCategory()
          .withPensionStatus()
          .withPayMethod()
          .withPayDate(List("2020, 11, 30", "2020, 12, 31"))
          .withLastPayDate("2020, 11, 30")
          .withPartTimeQuestion(PartTimeNo)
          .withRegularPayAmount(10000.00)

      val userAnswers: UserAnswers = dec2020Journey()
      val noNicAndPensionBreakdown: ConfirmationViewBreakdownWithoutNicAndPension = {
        loadResultData(userAnswers).value.asInstanceOf[ConfirmationDataResultWithoutNicAndPension].confirmationViewBreakdown
      }

      val nextStepsListMessage: Int => String =
        (bullet: Int) => JRSExtensionConfirmationMessages.nextStepsListMessages(bullet, decClaimPeriod)
      val calculatePayListMessage: Int => String = { (bullet: Int) =>
        JRSExtensionConfirmationMessages.calculatePayListMessages(bullet, 10000, 31, 31)
      }
      val furloughGrantListMessage: Int => String = { (bullet: Int) =>
        JRSExtensionConfirmationMessages.furloughGrantListMessages(bullet, 10000, 80)
      }

      val expectedContent = Seq(
        Selectors.h1                            -> JRSExtensionConfirmationMessages.heading,
        Selectors.dateAndCalculatorVersion      -> JRSExtensionConfirmationMessages.dateAndCalculatorVersion(dateToString(LocalDate.now())),
        Selectors.indent                        -> JRSExtensionConfirmationMessages.indent,
        Selectors.disclaimer                    -> JRSExtensionConfirmationMessages.disclaimerTopPage,
        Selectors.h2(1)                         -> JRSExtensionConfirmationMessages.h2NextSteps,
        Selectors.nextStepsNumberedList(1)      -> nextStepsListMessage(1),
        Selectors.nextStepsNumberedList(2)      -> nextStepsListMessage(2),
        Selectors.nextStepsNumberedList(3)      -> nextStepsListMessage(3),
        Selectors.nextStepsNumberedList(4)      -> nextStepsListMessage(4),
        Selectors.nextStepsNumberedList(5)      -> nextStepsListMessage(5),
        Selectors.h2(2)                         -> JRSExtensionConfirmationMessages.h2BreakdownOfCalculations,
        Selectors.breakdownParagraphOne         -> JRSExtensionConfirmationMessages.breakDownParagraphOne,
        Selectors.breakdownParagraphTwo         -> JRSExtensionConfirmationMessages.breakDownParagraphTwo,
        Selectors.breakdownParagraphThree       -> JRSExtensionConfirmationMessages.breakDownParagraphThree,
        Selectors.h3(1)                         -> JRSExtensionConfirmationMessages.h3PayPeriod(decClaimPeriod),
        Selectors.h4(1)                         -> JRSExtensionConfirmationMessages.h4CalculatePay,
        Selectors.h4CalculatePayParagraphOne    -> JRSExtensionConfirmationMessages.h4ParagraphOne,
        Selectors.calculatePayList(1)           -> calculatePayListMessage(1),
        Selectors.calculatePayList(2)           -> calculatePayListMessage(2),
        Selectors.calculatePayList(3)           -> calculatePayListMessage(3),
        Selectors.h4CalculatePayParagraphTwo    -> JRSExtensionConfirmationMessages.h4ParagraphTwo(10000),
        Selectors.h4(2)                         -> JRSExtensionConfirmationMessages.h4FurloughGrant,
        Selectors.furloughGrantList(1)          -> furloughGrantListMessage(1),
        Selectors.furloughGrantList(2)          -> furloughGrantListMessage(2),
        Selectors.h4FurloughGrantParagraphOne   -> JRSExtensionConfirmationMessages.furloughGrantParagraphOne(8000),
        Selectors.h4FurloughGrantParagraphTwo   -> JRSExtensionConfirmationMessages.furloughGrantParagraphTwo(2500),
        Selectors.h4FurloughGrantParagraphThree -> JRSExtensionConfirmationMessages.furloughGrantParagraphThree(2500),
        Selectors.furloughGrantInset            -> JRSExtensionConfirmationMessages.furloughGrantIndent(2500),
        Selectors.bottomDisclaimer              -> JRSExtensionConfirmationMessages.disclaimerBottomPage,
        Selectors.printLink                     -> JRSExtensionConfirmationMessages.printOrSave,
        Selectors.webChatLink                   -> JRSExtensionConfirmationMessages.webchatLink,
        Selectors.feedbackLink                  -> JRSExtensionConfirmationMessages.feedbackLink
      )

      implicit val request: DataRequest[_] = fakeDataRequest()

      def applyView(): HtmlFormat.Appendable =
        view(cvb = noNicAndPensionBreakdown, claimPeriod = decClaimPeriod, version = "2", false)

      implicit val doc: Document = asDocument(applyView())

      behave like normalPage(messageKeyPrefix)
      behave like pageWithHeading(heading = JRSExtensionConfirmationMessages.heading)
      behave like pageWithExpectedMessages(expectedContent)

      "behave like a page with a StartAnotherCalculation button" must {

        s"have a button with message '${JRSExtensionConfirmationMessages.startAnotherCalculation}'" in {
          assertEqualsMessage(doc, "#main-content > div > div > div > a", JRSExtensionConfirmationMessages.startAnotherCalculation)
        }
      }

      "behave like a page with correct links" must {

        "have a PrintOrSave link - brings up window.print() when clicked" in {
          doc.select(Selectors.printLink).attr("onClick") mustBe "window.print()"
        }

        "have a Webchat link - opens a webchat/contact details page" in {
          doc.select(Selectors.webChatLink).attr("href") mustBe appConf.webchatHelpUrl
        }

        "have a Feedback link" in {
          doc.select(Selectors.feedbackLink).attr("href") mustBe "/job-retention-scheme-calculator/start-survey"
        }
      }
    }
  }

}
