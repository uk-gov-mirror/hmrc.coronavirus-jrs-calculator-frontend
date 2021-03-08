/*
 * Copyright 2021 HM Revenue & Customs
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

import java.time.{LocalDate, Month}

import cats.data.Validated.{Invalid, Valid}
import controllers.actions._
import forms.PreviousFurloughPeriodsFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.DataRequest
import navigation.Navigator
import pages.{FurloughStartDatePage, PreviousFurloughPeriodsPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.LocalDateHelpers._
import views.html.PreviousFurloughPeriodsView

import scala.concurrent.{ExecutionContext, Future}

class PreviousFurloughPeriodsController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: PreviousFurloughPeriodsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PreviousFurloughPeriodsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    getFurloughStartDateFromUserAnswers.fold(
      Redirect(routes.ErrorController.somethingWentWrong())
    )(
      furloughStartDate => {
        val preparedForm = request.userAnswers.getV(PreviousFurloughPeriodsPage) match {
          case Invalid(_)   => form
          case Valid(value) => form.fill(value)
        }
        Ok(view(preparedForm, getDateToShowInHeadingFromFurloughStartDate(furloughStartDate)))
      }
    )
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getFurloughStartDateFromUserAnswers.fold(
      Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    )(
      furloughStartDate => {
        form
          .bindFromRequest()
          .fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, getDateToShowInHeadingFromFurloughStartDate(furloughStartDate)))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(PreviousFurloughPeriodsPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(PreviousFurloughPeriodsPage, updatedAnswers))
          )
      }
    )
  }

  private def getFurloughStartDateFromUserAnswers()(implicit request: DataRequest[_]): Option[LocalDate] =
    request.userAnswers.getV(FurloughStartDatePage) match {
      case Valid(furloughStartDate) => {
        Some(furloughStartDate)
      }
      case Invalid(_) => {
        None
      }
    }

  private def getDateToShowInHeadingFromFurloughStartDate(furloughStartDate: LocalDate): LocalDate =
    furloughStartDate match {
      case date if (date.isAfter(mar8th2020) && date.isBefore(nov8th2020)) => mar1st2020
      case date if (date.isAfter(nov8th2020) && date.isBefore(may8th2021)) => nov1st2020
      case _                                                               => may1st2021
    }
}
