/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

sealed trait PaymentFrequency
case object Monthly extends PaymentFrequency
case object FourWeekly extends PaymentFrequency
case object Fortnightly extends PaymentFrequency
case object Weekly extends PaymentFrequency
