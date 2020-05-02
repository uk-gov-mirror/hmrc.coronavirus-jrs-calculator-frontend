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

case class BranchingQuestions(payMethod: PayMethod, variableLengthEmployed: Option[EmployeeStarted], employeeStartDate: Option[LocalDate])

case class JourneyCoreData(
  furloughPeriod: FurloughWithinClaim,
  periods: Seq[PeriodWithPaymentDate],
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)

sealed trait JourneyData
case class RegularPayData(data: JourneyCoreData, wage: Amount) extends JourneyData

case class VariablePayData(data: JourneyCoreData, grossPay: Amount, nonFurloughPay: NonFurloughPay, priorFurlough: Period)
    extends JourneyData

case class VariablePayWithCylbData(
  data: JourneyCoreData,
  grossPay: Amount,
  nonFurloughPay: NonFurloughPay,
  priorFurlough: Period,
  cylbPayments: Seq[CylbPayment])
    extends JourneyData
