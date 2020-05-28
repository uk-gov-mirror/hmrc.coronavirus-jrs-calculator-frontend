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

package navigation

import controllers.routes
import models.FurloughStatus.FurloughEnded
import models.requests.DataRequest
import pages._
import play.api.mvc.{Call, Request}

object BackLinker {

  def getFor(currentPage: Option[Page], idx: Option[Int] = None)(implicit request: Request[_]): String =
    currentPage match {
      case Some(page) if request.isInstanceOf[DataRequest[_]] =>
        implicit val dataRequest: DataRequest[_] = request.asInstanceOf[DataRequest[_]]
        getBackLink(page, idx).url

      case _ => "#"
    }

  private def getBackLink(page: Page, idx: Option[Int] = None)(implicit dataRequest: DataRequest[_]): Call =
    page match {
      case ClaimPeriodEndPage    => routes.ClaimPeriodStartController.onPageLoad()
      case FurloughStartDatePage => routes.ClaimPeriodEndController.onPageLoad()
      case FurloughStatusPage    => routes.FurloughStartDateController.onPageLoad()
      case FurloughEndDatePage   => routes.FurloughOngoingController.onPageLoad()
      case PaymentFrequencyPage  => paymentFrequencyBackLink()
      case PayMethodPage         => routes.PaymentFrequencyController.onPageLoad()
      case EmployeeStartedPage   => routes.PayMethodController.onPageLoad()
      case EmployeeStartDatePage => routes.VariableLengthEmployedController.onPageLoad()
    }

  private def paymentFrequencyBackLink()(implicit request: DataRequest[_]): Call =
    request.userAnswers.get(FurloughStatusPage) match {
      case Some(FurloughEnded) => routes.FurloughEndDateController.onPageLoad()
      case _                   => routes.FurloughOngoingController.onPageLoad()
    }
}
