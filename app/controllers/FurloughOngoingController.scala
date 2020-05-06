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

import controllers.actions._
import forms.FurloughOngoingFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.{ClaimPeriodEndPage, ClaimPeriodStartPage, FurloughStatusPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import views.html.FurloughOngoingView

import scala.concurrent.{ExecutionContext, Future}

class FurloughOngoingController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughOngoingFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: FurloughOngoingView
)(implicit ec: ExecutionContext)
    extends BaseController {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val maybeClaimStart = request.userAnswers.get(ClaimPeriodStartPage)
    val maybeClaimEnd = request.userAnswers.get(ClaimPeriodEndPage)
    val maybeFurlough = request.userAnswers.get(FurloughStatusPage)

    (maybeClaimStart, maybeClaimEnd) match {
      case (Some(start), Some(end)) => Ok(view(maybeFurlough.map(fr => form.fill(fr)).getOrElse(form), start, end))
      case (None, _)                => Redirect(routes.ClaimPeriodStartController.onPageLoad())
      case (_, None)                => Redirect(routes.ClaimPeriodEndController.onPageLoad())
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val maybeClaimStart = request.userAnswers.get(ClaimPeriodStartPage)
          val maybeClaimEnd = request.userAnswers.get(ClaimPeriodEndPage)

          val result = (maybeClaimStart, maybeClaimEnd) match {
            case (Some(start), Some(end)) => BadRequest(view(formWithErrors, start, end))
            case (None, _)                => Redirect(routes.ClaimPeriodStartController.onPageLoad())
            case (_, None)                => Redirect(routes.ClaimPeriodEndController.onPageLoad())
          }
          Future.successful(result)
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(
                               request.userAnswers
                                 .set(FurloughStatusPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(FurloughStatusPage, updatedAnswers))
      )
  }
}
