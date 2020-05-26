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
import forms.PayPeriodQuestionFormProvider
import handlers.FastJourneyUserAnswersHandler
import javax.inject.Inject
import models.PayPeriodQuestion
import models.requests.DataRequest
import navigation.Navigator
import pages.{PayDatePage, PayPeriodQuestionPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.PayPeriodQuestionView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class PayPeriodQuestionController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  feature: FeatureFlagActionProvider,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PayPeriodQuestionFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PayPeriodQuestionView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with FastJourneyUserAnswersHandler {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen feature(FastTrackJourneyFlag) andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.getV(PayPeriodQuestionPage) match {
        case Invalid(e)   => form
        case Valid(value) => form.fill(value)
      }
      Ok(view(preparedForm, generatePeriods(request.userAnswers.getList(PayDatePage))))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen feature(FastTrackJourneyFlag) andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, generatePeriods(request.userAnswers.getList(PayDatePage))))),
          value => processSubmittedAnswer(request, value)
        )
  }

  private def processSubmittedAnswer(request: DataRequest[AnyContent], value: PayPeriodQuestion): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(PayPeriodQuestionPage, value))
      _              <- sessionRepository.set(updatedAnswers)
      updatedJourney <- Future.fromTry(Try(updateJourney(updatedAnswers).get))
    } yield Redirect(navigator.nextPage(PayPeriodQuestionPage, updatedJourney.updated))
}
