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
import config.CalculatorVersionConfiguration
import controllers.actions._
import handlers.{ConfirmationControllerRequestHandler, ErrorHandler}
import javax.inject.Inject
import models.EmployeeStarted.OnOrBefore1Feb2019
import models.UserAnswers.AnswerV
import models.UserAnswers
import navigation.Navigator
import pages.{AnnualPayAmountPage, ClaimPeriodEndPage, ClaimPeriodStartPage, EmployeeRTISubmissionPage, EmployeeStartDatePage, EmployeeStartedPage,
  FurloughEndDatePage, FurloughInLastTaxYearPage, FurloughStartDatePage, FurloughStatusPage, LastYearPayPage, PartTimeHoursPage, PartTimeNormalHoursPage,
  PartTimePeriodsPage, PartTimeQuestionPage, PayDatePage, PayMethodPage, PayPeriodsListPage, PaymentFrequencyPage, RegularLengthEmployedPage,
  RegularPayAmountPage
}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import utils.PagerDutyHelper
import utils.PagerDutyHelper.PagerDutyKeys._
import viewmodels.{
  ConfirmationDataResult,
  ConfirmationDataResultWithoutNicAndPension,
  PhaseOneConfirmationDataResult,
  PhaseTwoConfirmationDataResult
}
import views.html.{
  ConfirmationViewWithDetailedBreakdowns,
  JrsExtensionConfirmationView,
  NoNicAndPensionConfirmationView,
  OctoberConfirmationView,
  PhaseTwoConfirmationView,
  SeptemberConfirmationView
}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

class ConfirmationController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        viewWithDetailedBreakdowns: ConfirmationViewWithDetailedBreakdowns,
                                        phaseTwoView: PhaseTwoConfirmationView,
                                        noNicAndPensionView: NoNicAndPensionConfirmationView,
                                        septemberConfirmationView: SeptemberConfirmationView,
                                        octoberConfirmationView: OctoberConfirmationView,
                                        extensionView: JrsExtensionConfirmationView,
                                        auditService: AuditService,
                                        val navigator: Navigator
                                      )(implicit val errorHandler: ErrorHandler, ec: ExecutionContext) extends BaseController
  with ConfirmationControllerRequestHandler with CalculatorVersionConfiguration {

  var testCases: Seq[String] = Seq()

  //scalastyle:off
  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireData).async { implicit request =>

    //stringTestCase(request.userAnswers, loadResultData(request.userAnswers))

    loadResultData(request.userAnswers) match {
      case Valid(data: PhaseOneConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        Future.successful(Ok(viewWithDetailedBreakdowns(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
      case Valid(data: PhaseTwoConfirmationDataResult) =>
        auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
        Future.successful(Ok(phaseTwoView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
      case Valid(data: ConfirmationDataResultWithoutNicAndPension) =>
        data.metaData.claimPeriod.start.getMonthValue match {
          case 8 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(Ok(noNicAndPensionView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
          case 9 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(
              Ok(septemberConfirmationView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
          case 10 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(Ok(octoberConfirmationView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
          case 11 | 12 | 1 =>
            auditService.sendCalculationPerformed(request.userAnswers, data.confirmationViewBreakdown)
            Future.successful(Ok(extensionView(data.confirmationViewBreakdown, data.metaData.claimPeriod, calculatorVersionConf)))
          case _ => Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
        }
      case Invalid(e) =>
        auditService.sendCalculationFailed(request.userAnswers)
        PagerDutyHelper.alert(CALCULATION_FAILED)
        UserAnswers.logErrors(e)(logger)
        Future.successful(Redirect(routes.ErrorController.somethingWentWrong()))
    }
  }

  def stringTestCase(userAnswers: UserAnswers, result: AnswerV[ConfirmationDataResult]): String = {

    val date: Regex = """(\d{4})-(\d{2})-(\d{2})""".r

    val periodDate: Regex = """Period\("(\d{4})-(\d{2})-(\d{2})"""".r
    val periodDateSecond: Regex = """.toLocalDate,"(\d{4})-(\d{2})-(\d{2})"""".r
    val usualHoursRegex: Regex = """UsualHours\("(\d{4})-(\d{2})-(\d{2})"""".r
    val partTimeHoursRegex: Regex = """PartTimeHours\("(\d{4})-(\d{2})-(\d{2})"""".r

    val text =s"""emptyUserAnswers
         |      ${userAnswers.getO(EmployeeRTISubmissionPage).flatMap(x => x.toOption.map(x => ".withRtiSubmission(" + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(FurloughStatusPage).flatMap(x => x.toOption.map(x => ".withFurloughStatus(FurloughStatus." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(EmployeeStartDatePage).flatMap(x => x.toOption.map(x => ".withEmployeeStartDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(FurloughEndDatePage).flatMap(x => x.toOption.map(x => ".withFurloughEndDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(PaymentFrequencyPage).flatMap(x => x.toOption.map(x => ".withPaymentFrequency(" + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(EmployeeStartedPage).map { x =>
                  if (x.exists(_.equals(OnOrBefore1Feb2019))) {
                    ".withEmployeeStartedOnOrBefore1Feb2019(" + x.getClass.getSimpleName
                      .replace("Valid(", "")
                      .replace("$", "") + ")"
                  } else {
                    ".withEmployeeStartedAfter1Feb2019(" + x.getClass.getSimpleName
                      .replace("Valid(", "")
                      .replace("$", "") + ")"
                  }
                }.getOrElse("")}
         |      ${userAnswers.getO(ClaimPeriodStartPage).flatMap(x => x.toOption.map(x => ".withClaimPeriodStart(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(FurloughInLastTaxYearPage).flatMap(x => x.toOption.map(x => ".withFurloughInLastTaxYear(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(PayPeriodsListPage).flatMap(x => x.toOption.map(x => ".withPayPeriodsList(PayPeriodsList." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(PartTimePeriodsPage).flatMap(x => x.toOption.map(x => ".withPartTimePeriods(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(PayMethodPage).flatMap(x => x.toOption.map(x => ".withPayMethod(PayMethod." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(PartTimeQuestionPage).flatMap(x => x.toOption.map(x => ".withPartTimeQuestion(PartTimeQuestion." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(AnnualPayAmountPage).flatMap(x => x.toOption.map(x => ".withAnnualPayAmount(" + x.amount.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(RegularPayAmountPage).flatMap(x => x.toOption.map(x => ".withRegularPayAmount(" + x.amount.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(FurloughStartDatePage).flatMap(x => x.toOption.map(x => ".withFurloughStartDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(ClaimPeriodEndPage).flatMap(x => x.toOption.map(x => ".withClaimPeriodEnd(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${userAnswers.getO(RegularLengthEmployedPage).flatMap(x => x.toOption.map(x => ".withRegularLengthEmployed(RegularLengthEmployed." + x.getClass.getSimpleName.replace("Valid(", "").replace("$", "") + ")")).getOrElse("")}
         |      ${".withPayDate(" + userAnswers.getList(PayDatePage).toString.replace("Valid(", "") + ")"}
         |      ${userAnswers.getO(LastYearPayPage).flatMap(x => x.toOption.map(x => ".withPayDate(" + x.toString.replace("Valid(", "") + ")")).getOrElse("")}
         |      ${".withUsualHours(" + userAnswers.getList(PartTimeNormalHoursPage).toString.replace("Valid(", "") + ")"}
         |      ${".withPartTimeHours(" + userAnswers.getList(PartTimeHoursPage).toString.replace("Valid(", "") + ")"}
         |""".stripMargin.replaceAll("\n\n", "\n")

    val textWithOutcome = text + "\n -> " + result.asInstanceOf[Valid[ConfirmationDataResultWithoutNicAndPension]].toOption.get.confirmationViewBreakdown.furlough.total.formatted("%.2f")

    def addLocalDate(s: Regex.Match) = s"$s.toLocalDate"

    val finalResult = partTimeHoursRegex.replaceAllIn(
      usualHoursRegex.replaceAllIn(
        periodDateSecond.replaceAllIn(
          periodDate.replaceAllIn(
            date.replaceAllIn(textWithOutcome, _ match {
              case date(y, m, d) => f""""$y-$m-$d""""
            }), _ match {
              case periodDate => addLocalDate(periodDate)
            }), _ match {
            case secondPeriodDate => addLocalDate(secondPeriodDate)
          }), _ match {
          case usualHours => addLocalDate(usualHours)
        }), _ match {
        case partTimeHours => addLocalDate(partTimeHours)
      })

    testCases = testCases ++ Seq(finalResult)

    if(testCases.length > 8000){
      println("Test cases done. See Outcome.")
      println("#############################")
      println(testCases.mkString(",\n"))
      Thread.sleep(30000)
      println("#############################")
    }

    finalResult
  }
}
