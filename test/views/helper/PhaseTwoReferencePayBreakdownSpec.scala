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

package views.helper

import java.time.LocalDate

import cats.scalatest.ValidatedValues
import handlers.ConfirmationControllerRequestHandler
import models.PartTimeQuestion.PartTimeNo
import models.PaymentFrequency.Monthly
import models.{Period, PhaseTwoFurloughBreakdown, UserAnswers}
import play.twirl.api.HtmlFormat
import viewmodels.ConfirmationDataResultWithoutNicAndPension
import views.behaviours.ViewBehaviours
import views.html.helper.phaseTwoReferencePayBreakdown

class PhaseTwoReferencePayBreakdownSpec extends ViewBehaviours with ConfirmationControllerRequestHandler with ValidatedValues {

  val decClaimPeriod = Period(LocalDate.of(2020, 12, 1), LocalDate.of(2020, 12, 31))

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

  val noNicAndPensionBreakdown: Seq[PhaseTwoFurloughBreakdown] = {
    loadResultData(dec2020Journey()).value
      .asInstanceOf[ConfirmationDataResultWithoutNicAndPension]
      .confirmationViewBreakdown
      .furlough
      .periodBreakdowns
  }

  val messageKeyPrefix = "confirmation"
  val breakdownComponent: phaseTwoReferencePayBreakdown = injector.instanceOf[phaseTwoReferencePayBreakdown]

  def applyComponent(): Seq[HtmlFormat.Appendable] = {
    noNicAndPensionBreakdown.map(phaseTwoFurloughBreakdown => breakdownComponent(payment = phaseTwoFurloughBreakdown.paymentWithPeriod))
  }


//  "PhaseTwoReferencePayBreakdown" when {
//
//    "the component produces the correct messages" in {
//
//      implicit val documents: Seq[Document] = applyComponent().map(html => asDocument(html))
//
//      documents mustBe "Mikey"
//    }
//
//  }

}
