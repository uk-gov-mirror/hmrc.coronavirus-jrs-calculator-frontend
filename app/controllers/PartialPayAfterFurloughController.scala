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
import forms.FurloughPartialPayFormProvider
import javax.inject.Inject
import models.UserAnswers
import navigation.Navigator
import org.slf4j.{Logger, LoggerFactory}
import pages._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.PartialPayExtractor
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.VariableLengthPartialPayView

import scala.concurrent.{ExecutionContext, Future}

class PartialPayAfterFurloughController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  identify: IdentifierAction,
  feature: FeatureFlagActionProvider,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: FurloughPartialPayFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: VariableLengthPartialPayView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with PartialPayExtractor {

  val form = formProvider()

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getPartialPeriods(request.userAnswers)
      .find(isFurloughEnd)
      .map(getAfterFurloughPeriodRemainder)
      .fold(
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      ) { afterFurlough =>
        val preparedForm = request.userAnswers.getV(PartialPayAfterFurloughPage) match {
          case Invalid(errors) =>
            UserAnswers.logWarnings(errors)(logger)
            form
          case Valid(value) => form.fill(value)
        }

        Future.successful(
          Ok(
            view(
              preparedForm,
              afterFurlough.start,
              afterFurlough.end,
              routes.PartialPayAfterFurloughController.onSubmit()
            )))
      }
  }

  def onSubmit: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>
    getPartialPeriods(request.userAnswers)
      .find(isFurloughEnd)
      .map(getAfterFurloughPeriodRemainder)
      .fold(
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
      ) { afterFurlough =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors =>
              Future.successful(
                BadRequest(
                  view(
                    formWithErrors,
                    afterFurlough.start,
                    afterFurlough.end,
                    routes.PartialPayAfterFurloughController.onSubmit()
                  ))), { value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(PartialPayAfterFurloughPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield {
                Redirect(navigator.nextPage(PartialPayAfterFurloughPage, updatedAnswers))
              }
            }
          )
      }
  }

  override val logger: Logger = LoggerFactory.getLogger(getClass)
}
