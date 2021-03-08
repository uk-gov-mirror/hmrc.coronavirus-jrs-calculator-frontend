package utils

import cats.data.Validated.Valid
import models.PayMethod.{Regular, Variable}
import models.{EmployeeRTISubmission, EmployeeStarted, RegularLengthEmployed}
import models.requests.DataRequest
import pages.{EmployeeRTISubmissionPage, EmployeeStartDatePage, EmployeeStartedPage, PayMethodPage, PreviousFurloughPeriodsPage, RegularLengthEmployedPage}
import play.api.Logger.logger
import uk.gov.hmrc.http.InternalServerException
import utils.LocalDateHelpers.feb1st2020

trait EmployeeTypeUtil {

  def regularPayResolver[T](type1EmployeeResult: Option[T], type2aEmployeeResult: Option[T], type2bEmployeeResult: Option[T])(
    implicit request: DataRequest[_]): Option[T] =
    request.userAnswers.getV(RegularLengthEmployedPage) match {
      case Valid(RegularLengthEmployed.Yes) => type1EmployeeResult
      case Valid(RegularLengthEmployed.No)  =>
        //OnPayrollBefore30thOct2020Page
        request.userAnswers.getV(PreviousFurloughPeriodsPage) match {
          case Valid(true)  => type2aEmployeeResult
          case Valid(false) => type2bEmployeeResult
        }
      case _ =>
        val logMsg = "[EmployeeTypeService][regularPayResolver] no valid answer for EmployeeStartDatePage"
        logger.warn(logMsg)

        //handle in flight users 2a by default

        throw new InternalServerException(logMsg)
    }

  def variablePayResolver[T](type3EmployeeResult: Option[T],
                             type4EmployeeResult: Option[T],
                             type5aEmployeeResult: Option[T],
                             type5bEmployeeResult: Option[T])(implicit request: DataRequest[_]): Option[T] =
    request.userAnswers.getV(EmployeeStartedPage) match {
      case Valid(EmployeeStarted.OnOrBefore1Feb2019) => type3EmployeeResult
      case Valid(EmployeeStarted.After1Feb2019) =>
        (request.userAnswers.getV(EmployeeStartDatePage),
          request.userAnswers.getV(EmployeeRTISubmissionPage),
          //OnPayrollBefore30thOct2020Page
          request.userAnswers.getV(PreviousFurloughPeriodsPage)) match {
          case (Valid(startDate), Valid(rtiAns), _) if startDate.isBefore(feb1st2020) | rtiAns == EmployeeRTISubmission.Yes =>
            type4EmployeeResult
          case (_, _, Valid(true)) =>
            type5aEmployeeResult
          case (_, _, Valid(false)) =>
            type5bEmployeeResult
        }
      case _ =>
        val logMsg = "[EmployeeTypeService][variablePayResolver] employee type could not be resolved"
        logger.warn(logMsg)

        //handle in flight users 5a by default

        throw new InternalServerException(logMsg)
    }

  //noinspection ScalaStyle
  def employeeTypeResolver[T](defaultResult: T,
                              regularPayEmployeeResult: Option[T],
                              variablePayEmployeeResult: Option[T],
                              type1EmployeeResult: Option[T],
                              type2aEmployeeResult: Option[T],
                              type2bEmployeeResult: Option[T],
                              type3EmployeeResult: Option[T],
                              type4EmployeeResult: Option[T],
                              type5aEmployeeResult: Option[T],
                              type5bEmployeeResult: Option[T])(implicit request: DataRequest[_]): T = {
    val defaultRegularResult: T = regularPayEmployeeResult.getOrElse(defaultResult)
    val defaultVariableResult: T = variablePayEmployeeResult.getOrElse(defaultResult)
    request.userAnswers.getV(PayMethodPage) match {
      case Valid(Regular) =>
        regularPayResolver(type1EmployeeResult, type2aEmployeeResult, type2bEmployeeResult).getOrElse(defaultRegularResult)
      case Valid(Variable) =>
        variablePayResolver(type3EmployeeResult, type4EmployeeResult, type5aEmployeeResult, type5bEmployeeResult).getOrElse(
          defaultVariableResult)
      case _ =>
        val logMsg = "[EmployeeTypeService][employeeTypeResolver] no valid answer for PayMethodPage"
        logger.warn(logMsg)
        throw new InternalServerException(logMsg)
    }
  }
}
