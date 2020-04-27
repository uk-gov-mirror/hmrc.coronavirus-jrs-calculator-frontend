/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package base

import models.UserAnswers
import org.scalatest.{MustMatchers, OptionValues, TryValues, WordSpec}
import play.api.libs.json.Writes
import queries.Settable

trait SpecBase extends WordSpec with MustMatchers with TryValues with OptionValues {

  implicit class UserAnswersHelper(val userAnswers: UserAnswers) {
    def setValue[A](page: Settable[A], value: A, idx: Option[Int] = None)(implicit writes: Writes[A]) =
      userAnswers.set(page, value, idx).success.value
  }

}
