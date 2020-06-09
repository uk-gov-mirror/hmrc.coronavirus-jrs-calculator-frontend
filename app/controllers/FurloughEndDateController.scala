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

package controllers

import java.time.LocalDate

import cats.data.Validated.{Invalid, Valid}
import controllers.actions._
import forms.FurloughEndDateFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.Period
import navigation.Navigator
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughEndDatePage, FurloughStartDatePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.FurloughEndDateView

import scala.concurrent.{ExecutionContext, Future}

class FurloughEndDateController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughEndDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughEndDateView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(FurloughStartDatePage) { furloughStart =>
      getRequiredAnswersV(ClaimPeriodStartPage, ClaimPeriodEndPage) { (claimStart, claimEnd) =>
        val preparedForm = request.userAnswers.getV(FurloughEndDatePage) match {
          case Invalid(_)   => form(Period(claimStart, claimEnd), furloughStart)
          case Valid(value) => form(Period(claimStart, claimEnd), furloughStart).fill(value)
        }

        Future.successful(Ok(view(preparedForm, claimStart)))
      }
    }
  }

  def form(claimPeriod: Period, furloughStart: LocalDate): Form[LocalDate] =
    formProvider(claimPeriod, furloughStart)

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getRequiredAnswerV(FurloughStartDatePage) { furloughStart =>
      getRequiredAnswersV(ClaimPeriodStartPage, ClaimPeriodEndPage) { (claimStart, claimEnd) =>
        form(Period(claimStart, claimEnd), furloughStart)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, claimStart))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(FurloughEndDatePage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(FurloughEndDatePage, updatedAnswers))
          )
      }
    }
  }
}
