/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package handlers

import java.time.LocalDate

import models.{FurloughQuestion, NicCategory, PayPeriod, PayQuestion, PaymentFrequency, PensionStatus, UserAnswers}
import pages._

case class MandatoryData(
  claimPeriod: PayPeriod,
  paymentFrequency: PaymentFrequency,
  nicCategory: NicCategory,
  pensionStatus: PensionStatus,
  payQuestion: PayQuestion,
  furloughQuestion: FurloughQuestion,
  payDates: Seq[LocalDate]) //TODO make it a NonEmptyList

trait DataExtractor {

  def extract(userAnswers: UserAnswers): Option[MandatoryData] =
    for {
      claimStart  <- userAnswers.get(ClaimPeriodStartPage)
      claimEnd    <- userAnswers.get(ClaimPeriodEndPage)
      frequency   <- userAnswers.get(PaymentFrequencyPage)
      nic         <- userAnswers.get(NicCategoryPage)
      pension     <- userAnswers.get(PensionAutoEnrolmentPage)
      payQuestion <- userAnswers.get(PayQuestionPage)
      furlough    <- userAnswers.get(FurloughQuestionPage)
      payDate = userAnswers.getList(PayDatePage)
    } yield MandatoryData(PayPeriod(claimStart, claimEnd), frequency, nic, pension, payQuestion, furlough, payDate)

}
