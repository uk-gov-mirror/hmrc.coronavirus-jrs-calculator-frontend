/*
 * Copyright 2020 HM Revenue & Customs
 *
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
