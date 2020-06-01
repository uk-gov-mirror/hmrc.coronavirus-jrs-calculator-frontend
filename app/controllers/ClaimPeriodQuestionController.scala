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

import cats.data.Validated.{Invalid, Valid}
import controllers.actions.FeatureFlag.FastTrackJourneyFlag
import controllers.actions._
import forms.ClaimPeriodQuestionFormProvider
import handlers.{ErrorHandler, FastJourneyUserAnswersHandler}
import javax.inject.Inject
import models.ClaimPeriodQuestion
import models.requests.DataRequest
import navigation.Navigator
import pages.{ClaimPeriodEndPage, ClaimPeriodQuestionPage, ClaimPeriodStartPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import repositories.SessionRepository
import views.html.ClaimPeriodQuestionView

import scala.concurrent.{ExecutionContext, Future}

class ClaimPeriodQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  feature: FeatureFlagActionProvider,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: ClaimPeriodQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ClaimPeriodQuestionView,
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with FastJourneyUserAnswersHandler {

  val form: Form[ClaimPeriodQuestion] = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen feature(FastTrackJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswersV(ClaimPeriodStartPage, ClaimPeriodEndPage) { (claimStart, claimEnd) =>
        val filledForm: Form[ClaimPeriodQuestion] =
          request.userAnswers.getV(ClaimPeriodQuestionPage).fold(_ => form, form.fill)

        Future.successful(Ok(view(filledForm, claimStart, claimEnd)))
      }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen feature(FastTrackJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      getRequiredAnswersV(ClaimPeriodStartPage, ClaimPeriodEndPage) { (claimStart, claimEnd) =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, claimStart, claimEnd))),
            value => processSubmittedAnswer(request, value)
          )
      }
  }

  private def processSubmittedAnswer(request: DataRequest[AnyContent], value: ClaimPeriodQuestion): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(ClaimPeriodQuestionPage, value))
      call = navigator.nextPage(ClaimPeriodQuestionPage, updatedAnswers)
    } yield {

      updateJourney(updatedAnswers) match {
        case Valid(updatedJourney) =>
          sessionRepository.set(updatedJourney.updated)
          Redirect(call)
        case Invalid(errors) =>
          InternalServerError(errorHandler.internalServerErrorTemplate(request))
      }

    }
}
