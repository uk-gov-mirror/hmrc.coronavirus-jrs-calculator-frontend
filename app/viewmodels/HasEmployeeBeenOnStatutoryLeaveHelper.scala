package viewmodels

import config.FrontendAppConfig
import models.requests.DataRequest
import play.api.i18n.Messages
import utils.LocalDateHelpers._
import utils.{EmployeeTypeUtil, LocalDateHelpers}
import views.ViewUtils.dateToString

object HasEmployeeBeenOnStatutoryLeaveHelper extends EmployeeTypeUtil {

  def boundaryStart()(implicit request: DataRequest[_], appConfig: FrontendAppConfig, messages: Messages) =
    variablePayResolver[String](
      type3EmployeeResult = Some(dateToString(apr6th2019)),
      type4EmployeeResult = ???,
      type5aEmployeeResult = ???,
      type5bEmployeeResult = ???
    )

}
