package assets.messages

object PaymentFrequencyMessages {

  val heading = "How often do you pay this employee?"
  val indent = "The employees current pay frequency is different from the pay frequency in the reference period"
  val p1 = {
    "You cannot use the calculator if this employee is on fixed pay and their pay frequency has changed " +
      "between the reference period and the pay period you are calculating for. For example, " +
      "if they have changed from monthly pay to weekly pay. You will need to manually work out what you can claim."
  }
  val p2 = "You can"
  val link = {
    "read about the reference period and how to work out what you can claim manually " +
      "using the calculation guidance (opens in a new tab)."
  }

}
