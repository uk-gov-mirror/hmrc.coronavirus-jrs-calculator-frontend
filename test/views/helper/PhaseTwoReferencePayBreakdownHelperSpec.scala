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

import assets.messages.PhaseTwoReferencePayBreakdownHelperMessages
import models.{Amount, AveragePaymentWithPhaseTwoPeriod, FullPeriod, FullPeriodWithPaymentDate, Hours, PaymentDate, Period, PhaseTwoPeriod, StatutoryLeaveData}
import org.jsoup.nodes.Document
import views.BaseSelectors
import views.behaviours.ViewBehaviours
import views.html.helper.phaseTwoReferencePayBreakdown

import java.time.LocalDate

class PhaseTwoReferencePayBreakdownHelperSpec extends ViewBehaviours {

  val breakdownHelper = app.injector.instanceOf[phaseTwoReferencePayBreakdown]

  object Selectors extends BaseSelectors

  "phaseTwoReferencePayBreakdown" must {

    "for averagePaymentWithPhaseTwoPeriod" when {

      "not part time (no actual or usual hours)" when {

        "not statutory leave" must {

          val averagePaymentWithPhaseTwoPeriod =
            AveragePaymentWithPhaseTwoPeriod(
              referencePay = Amount(350),
              annualPay = Amount(30000),
              priorFurloughPeriod = Period(LocalDate.of(2019, 4, 6), LocalDate.of(2020, 4, 5)),
              phaseTwoPeriod = PhaseTwoPeriod(
                FullPeriodWithPaymentDate(
                  period = FullPeriod(
                    period = Period(
                      start = LocalDate.of(2021, 5, 1),
                      end = LocalDate.of(2021, 5, 31)
                    )
                  ),
                  paymentDate = PaymentDate(LocalDate.of(2021, 5, 1))
                ),
                actualHours = None,
                usualHours = None
              ),
              statutoryLeaveData = None
            )

          val renderedHtml =
            breakdownHelper(
              payment = averagePaymentWithPhaseTwoPeriod,
              period = FullPeriod(
                period = Period(
                  start = LocalDate.of(2021, 5, 1),
                  end = LocalDate.of(2021, 5, 31)
                )
              ),
              isNewStarterType5 = false
            )

          implicit val document: Document = asDocument(dummyView(renderedHtml))

          pageWithExpectedMessages(
            Seq(
              Selectors.h4(1)       -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.h4,
              Selectors.p(1)        -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p1(isType5 = false),
              Selectors.numbered(1) -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered1(30000, hasStatLeave = false),
              Selectors.numbered(2) -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered2(366, hasStatLeave = false),
              Selectors.numbered(3) -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered3(31),
              Selectors.p(2)        -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p2(2541.07)
            )
          )
        }

        "is statutory leave" must {

          val averagePaymentWithPhaseTwoPeriodWithStatLeave =
            AveragePaymentWithPhaseTwoPeriod(
              referencePay = Amount(350),
              annualPay = Amount(30000),
              priorFurloughPeriod = Period(LocalDate.of(2019, 4, 6), LocalDate.of(2020, 4, 5)),
              phaseTwoPeriod = PhaseTwoPeriod(
                FullPeriodWithPaymentDate(
                  period = FullPeriod(
                    period = Period(
                      start = LocalDate.of(2021, 5, 1),
                      end = LocalDate.of(2021, 5, 31)
                    )
                  ),
                  paymentDate = PaymentDate(LocalDate.of(2021, 5, 1))
                ),
                actualHours = None,
                usualHours = None
              ),
              statutoryLeaveData = Some(StatutoryLeaveData(5, 200))
            )

          val renderedHtml =
            breakdownHelper(
              payment = averagePaymentWithPhaseTwoPeriodWithStatLeave,
              period = FullPeriod(
                period = Period(
                  start = LocalDate.of(2021, 5, 1),
                  end = LocalDate.of(2021, 5, 31)
                )
              ),
              isNewStarterType5 = false
            )

          implicit val document: Document = asDocument(dummyView(renderedHtml))

          pageWithExpectedMessages(
            Seq(
              Selectors.h4(1)       -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.h4,
              Selectors.p(1)        -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p1(isType5 = false),
              Selectors.numbered(1) -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered1(29800, hasStatLeave = true),
              Selectors.numbered(2) -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered2(361, hasStatLeave = true),
              Selectors.numbered(3) -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered3(31),
              Selectors.p(2)        -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p2(2559.05)
            )
          )
        }
      }

      "IS part time" when {

        "not statutory leave" must {

          val averagePaymentWithPhaseTwoPeriodPartTime =
            AveragePaymentWithPhaseTwoPeriod(
              referencePay = Amount(350),
              annualPay = Amount(30000),
              priorFurloughPeriod = Period(LocalDate.of(2019, 4, 6), LocalDate.of(2020, 4, 5)),
              phaseTwoPeriod = PhaseTwoPeriod(
                FullPeriodWithPaymentDate(
                  period = FullPeriod(
                    period = Period(
                      start = LocalDate.of(2021, 5, 1),
                      end = LocalDate.of(2021, 5, 31)
                    )
                  ),
                  paymentDate = PaymentDate(LocalDate.of(2021, 5, 1))
                ),
                actualHours = Some(Hours(10)),
                usualHours = Some(Hours(30))
              ),
              statutoryLeaveData = None
            )

          val renderedHtml =
            breakdownHelper(
              payment = averagePaymentWithPhaseTwoPeriodPartTime,
              period = FullPeriod(
                period = Period(
                  start = LocalDate.of(2021, 5, 1),
                  end = LocalDate.of(2021, 5, 31)
                )
              ),
              isNewStarterType5 = false
            )

          implicit val document: Document = asDocument(dummyView(renderedHtml))

          pageWithExpectedMessages(
            Seq(
              Selectors.h4(1)                         -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.h4,
              Selectors.p(1)                          -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p1(isType5 = false),
              Selectors.numbered(1)                   -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered1(30000, hasStatLeave = false),
              Selectors.numbered(2)                   -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered2(366, hasStatLeave = false),
              Selectors.numbered(3)                   -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered3(31),
              Selectors.p(2)                          -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p2(2541.07),
              Selectors.p("#partTimeHours", 1)        -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.p1,
              Selectors.numbered("#partTimeHours", 1) -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.numbered1(2541.07),
              Selectors.numbered("#partTimeHours", 2) -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.numbered2(30),
              Selectors.numbered("#partTimeHours", 3) -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.numbered3(20),
              Selectors.p("#partTimeHours", 2)        -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.p2(350)
            )
          )
        }

        "is statutory leave" must {

          val averagePaymentWithPhaseTwoPeriodPartTimeWithStatLeave =
            AveragePaymentWithPhaseTwoPeriod(
              referencePay = Amount(350),
              annualPay = Amount(30000),
              priorFurloughPeriod = Period(LocalDate.of(2019, 4, 6), LocalDate.of(2020, 4, 5)),
              phaseTwoPeriod = PhaseTwoPeriod(
                FullPeriodWithPaymentDate(
                  period = FullPeriod(
                    period = Period(
                      start = LocalDate.of(2021, 5, 1),
                      end = LocalDate.of(2021, 5, 31)
                    )
                  ),
                  paymentDate = PaymentDate(LocalDate.of(2021, 5, 1))
                ),
                actualHours = Some(Hours(10)),
                usualHours = Some(Hours(30))
              ),
              statutoryLeaveData = Some(StatutoryLeaveData(5, 200))
            )

          val renderedHtml =
            breakdownHelper(
              payment = averagePaymentWithPhaseTwoPeriodPartTimeWithStatLeave,
              period = FullPeriod(
                period = Period(
                  start = LocalDate.of(2021, 5, 1),
                  end = LocalDate.of(2021, 5, 31)
                )
              ),
              isNewStarterType5 = false
            )

          implicit val document: Document = asDocument(dummyView(renderedHtml))

          pageWithExpectedMessages(
            Seq(
              Selectors.h4(1)                         -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.h4,
              Selectors.p(1)                          -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p1(isType5 = false),
              Selectors.numbered(1)                   -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered1(29800, hasStatLeave = true),
              Selectors.numbered(2)                   -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered2(361, hasStatLeave = true),
              Selectors.numbered(3)                   -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.numbered3(31),
              Selectors.p(2)                          -> PhaseTwoReferencePayBreakdownHelperMessages.AveragingMethod.p2(2559.05),
              Selectors.p("#partTimeHours", 1)        -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.p1,
              Selectors.numbered("#partTimeHours", 1) -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.numbered1(2559.05),
              Selectors.numbered("#partTimeHours", 2) -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.numbered2(30),
              Selectors.numbered("#partTimeHours", 3) -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.numbered3(20),
              Selectors.p("#partTimeHours", 2)        -> PhaseTwoReferencePayBreakdownHelperMessages.PartTimeHours.p2(350)
            )
          )
        }
      }
    }
  }
}
