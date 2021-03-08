package models

import utils.LocalDateHelpers._

import java.time.LocalDate

sealed trait EmployeeType

object EmployeeType extends Enumerable.Implicits {

  sealed trait RegularPayEmployee extends EmployeeType {
    val referencePayPeriodCutoff: LocalDate
  }

  sealed trait VariablePayEmployee extends EmployeeType

  sealed trait PreCovidStarter extends EmployeeType

  sealed trait Extension1NewStarter extends EmployeeType

  sealed trait Extension2NewStarter extends EmployeeType

  case object Type1Employee extends RegularPayEmployee with PreCovidStarter {
    //The last pay period pre-covid
    override val referencePayPeriodCutoff: LocalDate = mar19th2020
  }

  case object Type2aEmployee extends RegularPayEmployee with Extension1NewStarter {
    //The last pay period ending on or before 30 October 2020
    override val referencePayPeriodCutoff: LocalDate = oct30th2020
  }

  case object Type2bEmployee extends RegularPayEmployee with Extension2NewStarter {
    //The last pay period ending on or before 2 March 2021
    override val referencePayPeriodCutoff: LocalDate = mar2nd2021
  }

  case object Type3Employee extends VariablePayEmployee with PreCovidStarter {
    /*    Pay from reference period in previous tax year 2019/20
          or average wages payable from previous tax year
          whichever is highest
   */
  }

  case object Type4Employee extends VariablePayEmployee with PreCovidStarter {
    /*    The average wages between 6 April 2019 and the day before furlough started
          (or 5 April 2020, whichever is earliest)
     */
    val referencePayPeriodCutoff: LocalDate = apr5th2020
  }

  case object Type5aEmployee extends VariablePayEmployee with Extension1NewStarter {
    /*    The average wages between 6 April 2020 (or the date the employment started if later)
          and the day before the employee is first furloughed on or after 1 November 2020
     */
    val referencePayPeriodCutoff: LocalDate = nov1st2020
  }

  case object Type5bEmployee extends VariablePayEmployee with Extension2NewStarter {
    /*    The average wages between 6 April 2020 (or the date the employment started if later)
          and the day before the employee is first furloughed on or after 1 May 2021
     */
    val referencePayPeriodCutoff: LocalDate = may1st2021
  }
}
