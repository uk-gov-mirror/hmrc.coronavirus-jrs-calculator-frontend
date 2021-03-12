package assets

import java.time.LocalDate

trait BaseITConstants {

  val claimStartDate = LocalDate.parse("2020-03-01")
  val claimEndDate = LocalDate.parse("2020-03-31")
  val extensionStartDate = LocalDate.parse("2020-11-01")
  val phaseTwoStartDate  = LocalDate.parse("2020-07-01")

}
