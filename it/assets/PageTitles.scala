package assets

object PageTitles {
  def regularPayAmount(cutOffDate: String) = s"What was the employee paid in the last pay period ending on or before $cutOffDate?"

  val regularLengthEmployed = "Was this employee on your payroll on or before 19 March 2020?"
  val statutoryLeavePay = "How much was this employee paid for the periods of statutory leave?"
  val claimPeriodStartDate  = "What’s the start date of this claim?"
  val firstFurloughDate     = "When was this employee first furloughed?"
  val previousFurloughPeriods =
    "Has this employee been furloughed more than once since 1 November 2020? - Job Retention Scheme calculator - GOV.UK"
  val onPayrollBefore30thOct2020 = "Was this employee on your payroll on or before 30 October 2020?"

  def hasEmployeeBeenOnStatutoryLeave(boundaryStart: String, boundaryEnd: String) =
    s"Has this employee been on statutory leave for part of the period between $boundaryStart and $boundaryEnd"
}