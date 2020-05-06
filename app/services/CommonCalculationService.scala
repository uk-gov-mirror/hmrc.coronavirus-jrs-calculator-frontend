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

import models.{Amount, PaymentDate, PaymentFrequency}
import services.Calculators._
import utils.TaxYearFinder

trait CommonCalculationService extends TaxYearFinder {

  protected def greaterThanAllowance(amount: Amount, threshold: BigDecimal, rate: Rate): Amount =
    if (amount.value < threshold) Amount(0.0)
    else Amount((amount.value - threshold) * rate.value).halfUp

  protected def thresholdFinder(frequency: PaymentFrequency, paymentDate: PaymentDate, rate: Rate): BigDecimal =
    FrequencyTaxYearThresholdMapping.findThreshold(frequency, taxYearAt(paymentDate), rate)

}
