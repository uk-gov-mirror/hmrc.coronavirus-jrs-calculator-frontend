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

import controllers.actions._
import forms.OnPayrollBefore30thOct2020FormProvider

import javax.inject.Inject
import cats.data.Validated.{Invalid, Valid}
import models.requests.DataRequest
import navigation.Navigator
import pages.{EmployeeStartDatePage, OnPayrollBefore30thOct2020Page}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.OnPayrollBefore30thOct2020View
import play.api.http.{Status => StatusCode}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class OnPayrollBefore30thOct2020Controller @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: OnPayrollBefore30thOct2020FormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: OnPayrollBefore30thOct2020View
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  val october30th2020 = LocalDate.of(2020, 10, 30)

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    request.userAnswers.getV(EmployeeStartDatePage) match {
      case Valid(startDate) if !startDate.isBefore(october30th2020) =>
        saveAndRedirect(answer = false)
      case _ => {
        renderView(StatusCode.OK)(request.userAnswers.getV(OnPayrollBefore30thOct2020Page) match {
          case Invalid(_)   => form
          case Valid(value) => form.fill(value)
        })
      }
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        renderView(StatusCode.BAD_REQUEST),
        saveAndRedirect
      )
  }

  private def renderView(status: Int)(form: Form[Boolean])(implicit request: DataRequest[_]): Future[Result] =
    Future.successful(Status(status)(view(form, postAction = controllers.routes.OnPayrollBefore30thOct2020Controller.onSubmit())))

  private def saveAndRedirect(answer: Boolean)(implicit request: DataRequest[_]): Future[Result] =
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(OnPayrollBefore30thOct2020Page, answer))
      _              <- sessionRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(OnPayrollBefore30thOct2020Page, updatedAnswers))
}
