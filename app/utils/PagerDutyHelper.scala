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

package utils

import play.api.Logger

object PagerDutyHelper {
  val logger: Logger = Logger("PagerDutyLogger")

  object PagerDutyKeys extends Enumeration {
    val CALCULATION_FAILED = Value
  }

  def alert(pagerDutyKey: PagerDutyKeys.Value, otherDetail: Option[String] = None): Unit =
    logger.warn(s"$pagerDutyKey${otherDetail.fold("")(detail => s" $detail")}")
}
