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

case class BranchingQuestion(payMethod: PayMethod, variableLengthEmployed: Option[EmployeeStarted], employeeStartDate: Option[LocalDate])

case class JourneyCoreData(
  furloughPeriod: Period,
  periods: Seq[PeriodWithPaymentDate],
  frequency: PaymentFrequency,
  nic: NicCategory,
  pension: PensionStatus)

sealed trait JourneyData
case class RegularPayData(data: JourneyCoreData, wage: Amount) extends JourneyData
case class VariablePayData(data: JourneyCoreData, grossPay: Amount, nonFurloughPay: NonFurloughPay, priorFurlough: Period)
    extends JourneyData
