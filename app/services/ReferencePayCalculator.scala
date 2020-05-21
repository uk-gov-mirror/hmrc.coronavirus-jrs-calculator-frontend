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

package services

import models.{AveragePayment, CylbPayment, PaymentWithPeriod, ReferencePay, RegularPayData, VariablePayData, VariablePayWithCylbData}

trait ReferencePayCalculator extends RegularPayCalculator with AveragePayCalculator with CylbCalculator with Calculators {

  def calculateReferencePay(data: ReferencePay): Seq[PaymentWithPeriod] = data match {
    case rpd: RegularPayData  => calculateRegularPay(rpd.wage, rpd.referencePayData.periods)
    case vpd: VariablePayData => calculateAveragePay(vpd.nonFurloughPay, vpd.priorFurlough, vpd.periods, vpd.grossPay)
    case lbd: VariablePayWithCylbData => {
      val avg = calculateAveragePay(lbd.nonFurloughPay, lbd.priorFurlough, lbd.periods, lbd.grossPay)

      withCylb(avg, lbd)
    }
  }

  private def withCylb(avg: Seq[AveragePayment], data: VariablePayWithCylbData): Seq[CylbPayment] =
    avg.map(a => calculateCylb(a, data.nonFurloughPay, data.frequency, data.cylbPayments, a.periodWithPaymentDate))

}
