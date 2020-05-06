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

package models

import java.time.LocalDate

sealed trait Journey
case object RegularPay extends Journey
case object VariablePay extends Journey
case object VariablePayWithCylb extends Journey

case class BranchingQuestions(payMethod: PayMethod, employeeStarted: Option[EmployeeStarted], employeeStartDate: Option[LocalDate])

case class ReferencePayData(furloughPeriod: FurloughWithinClaim, periods: Seq[PeriodWithPaymentDate], frequency: PaymentFrequency)

sealed trait ReferencePay {
  val referencePayData: ReferencePayData

  def furloughPeriod: FurloughWithinClaim = referencePayData.furloughPeriod
  def periods: Seq[PeriodWithPaymentDate] = referencePayData.periods
  def frequency: PaymentFrequency = referencePayData.frequency
}

case class RegularPayData(referencePayData: ReferencePayData, wage: Amount) extends ReferencePay

case class VariablePayData(referencePayData: ReferencePayData, grossPay: Amount, nonFurloughPay: NonFurloughPay, priorFurlough: Period)
    extends ReferencePay

case class VariablePayWithCylbData(
  referencePayData: ReferencePayData,
  grossPay: Amount,
  nonFurloughPay: NonFurloughPay,
  priorFurlough: Period,
  cylbPayments: Seq[CylbPayment])
    extends ReferencePay
