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
import config.SchemeConfiguration
import controllers.actions._
import forms.AnnualPayAmountFormProvider
import handlers.ErrorHandler
import javax.inject.Inject
import models.{AnnualPayAmount, EmployeeRTISubmission, EmployeeStarted, UserAnswers}
import models.EmployeeStarted._
import navigation.Navigator
import pages.{AnnualPayAmountPage, ClaimPeriodStartPage, EmployeeRTISubmissionPage, EmployeeStartDatePage, EmployeeStartedPage, FurloughStartDatePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.UserAnswerPersistence
import utils.LocalDateHelpers._
import views.html.AnnualPayAmountView
import views.ViewUtils._

import scala.concurrent.{ExecutionContext, Future}

class AnnualPayAmountController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: AnnualPayAmountFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AnnualPayAmountView
)(implicit ec: ExecutionContext, errorHandler: ErrorHandler)
    extends BaseController with SchemeConfiguration with I18nSupport {

  val form: Form[AnnualPayAmount] = formProvider()
  protected val userAnswerPersistence = new UserAnswerPersistence(sessionRepository.set)

  def onPageLoad(): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      getRequiredAnswersV(FurloughStartDatePage, EmployeeStartedPage) { (furloughStart, employeeStarted) =>
        getRequiredAnswerV(ClaimPeriodStartPage) { claimStart =>
          val preparedForm = request.userAnswers.getV(AnnualPayAmountPage) match {
            case Invalid(e)   => form
            case Valid(value) => form.fill(value)
          }

          val (keySwitch, args) = titleHeading(claimStart, employeeStarted, furloughStart, request.userAnswers)

          Future.successful(Ok(view(preparedForm, keySwitch, args)))
        }
      }
    }

  def onSubmit(): Action[AnyContent] =
    (identify andThen getData andThen requireData).async { implicit request =>
      getRequiredAnswersV(FurloughStartDatePage, EmployeeStartedPage) { (furloughStart, employeeStarted) =>
        getRequiredAnswerV(ClaimPeriodStartPage) { claimStart =>
          val (keySwitch, args) = titleHeading(claimStart, employeeStarted, furloughStart, request.userAnswers)

          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, keySwitch, args))),
              value =>
                userAnswerPersistence
                  .persistAnswer(request.userAnswers, AnnualPayAmountPage, value, None)
                  .map { updatedAnswers =>
                    Redirect(navigator.nextPage(AnnualPayAmountPage, updatedAnswers, None))
                }
            )
        }
      }
    }

  private def titleHeading(claimStart: LocalDate, employeeStarted: EmployeeStarted, furloughStart: LocalDate, userAnswers: UserAnswers)(
    implicit message: Messages): (String, Seq[String]) = {
    val isExt: Boolean = claimStart.isEqualOrAfter(extensionStartDate)

    val employeeStartDate = userAnswers.getV(EmployeeStartDatePage)
    val isRTISubmissionRequired = rtiSubmissionRequired(userAnswers)
    val rtiSubmission = userAnswers.getV(EmployeeRTISubmissionPage)

    if (isExt) {

      if (isRTISubmissionRequired && rtiSubmission.exists(_ == EmployeeRTISubmission.No)) {
        ("from", Seq(dateToString(apr6th2020), dateToString(furloughStart.minusDays(1))))
      } else {
        (employeeStarted, employeeStartDate) match {
          case (OnOrBefore1Feb2019, _) =>
            ("from", Seq(dateToString(apr6th2019), dateToString(earliestOf(apr5th2020, furloughStart.minusDays(1)))))
          case (After1Feb2019, Valid(esd)) if esd.isBefore(apr6th2019) =>
            ("from", Seq(dateToString(apr6th2019), dateToString(earliestOf(apr5th2020, furloughStart.minusDays(1)))))
          case (After1Feb2019, Valid(esd)) if esd.isEqualOrAfter(apr6th2019) && esd.isBefore(mar20th2020) =>
            ("since", Seq(dateToString(earliestOf(apr5th2020, furloughStart.minusDays(1)))))
          case (After1Feb2019, Valid(esd)) if esd.isAfter(apr5th2020) =>
            ("since", Seq(dateToString(furloughStart.minusDays(1))))
          case (After1Feb2019, Valid(esd)) if esd.isBefore(apr6th2020) =>
            ("from", Seq(dateToString(apr6th2020), dateToString(furloughStart.minusDays(1))))
        }
      }
    } else {
      (employeeStarted, employeeStartDate) match {
        case (OnOrBefore1Feb2019, _) =>
          ("from", Seq(dateToString(apr6th2019), dateToString(earliestOf(apr5th2020, furloughStart.minusDays(1)))))
        case (After1Feb2019, Valid(esd)) if esd.isEqualOrAfter(apr6th2019) =>
          ("since", Seq(dateToString(earliestOf(apr5th2020, furloughStart.minusDays(1)))))
        case (After1Feb2019, Valid(esd)) if esd.isBefore(apr6th2019) =>
          ("from", Seq(dateToString(apr6th2019), dateToString(earliestOf(apr5th2020, furloughStart.minusDays(1)))))
      }
    }
  }

  private def rtiSubmissionRequired(userAnswers: UserAnswers) =
    (userAnswers.getV(ClaimPeriodStartPage), userAnswers.getV(EmployeeStartDatePage)) match {
      case (Valid(claimPeriodStart), Valid(empStartDate))
          if (claimPeriodStart.isEqualOrAfter(nov1st2020) && empStartDate.isEqualOrAfter(feb1st2020) && empStartDate.isEqualOrBefore(
            mar19th2020)) =>
        true

      case _ => false
    }

}
