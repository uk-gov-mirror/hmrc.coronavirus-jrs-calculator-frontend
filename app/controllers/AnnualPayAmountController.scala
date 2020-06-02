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
import controllers.actions._
import forms.AnnualPayAmountFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.AnnualPayAmount
import navigation.Navigator
import pages.{AnnualPayAmountPage, EmployeeStartedPage, FurloughStartDatePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import utils.LocalDateHelpers._
import views.html.AnnualPayAmountView

import scala.concurrent.{ExecutionContext, Future}

class AnnualPayAmountController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AnnualPayAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AnnualPayAmountView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with I18nSupport {

  val form: Form[AnnualPayAmount] = formProvider()

  def onPageLoad(): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      getRequiredAnswersV(FurloughStartDatePage, EmployeeStartedPage) { (furloughStart, employeeStarted) =>
        val preparedForm = request.userAnswers.getV(AnnualPayAmountPage) match {
          case Invalid(e)   => form
          case Valid(value) => form.fill(value)
        }

        val uiDate = earliestOf(apr5th2020, furloughStart.minusDays(1))

        Future.successful(Ok(view(preparedForm, uiDate, employeeStarted)))
      }
    }

  def onSubmit(): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      getRequiredAnswersV(FurloughStartDatePage, EmployeeStartedPage) { (furloughStart, employeeStarted) =>
        val uiDate = earliestOf(apr5th2020, furloughStart.minusDays(1))

        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, uiDate, employeeStarted))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(AnnualPayAmountPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(AnnualPayAmountPage, updatedAnswers))
          )
      }
    }
}
