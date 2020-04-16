/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package forms.mappings

import java.time.LocalDate

import models.Enumerable
import play.api.data.FieldMapping
import play.api.data.Forms.of

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required"): FieldMapping[String] =
    of(stringFormatter(errorKey))

  protected def int(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric"): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey))

  protected def double(requiredKey: String = "error.required", nonNumericKey: String = "error.nonNumeric"): FieldMapping[Double] =
    of(doubleFormatter(requiredKey, nonNumericKey))

  protected def bigDecimal(requiredKey: String = "error.required", nonNumericKey: String = "error.nonNumeric"): FieldMapping[BigDecimal] =
    of(bigDecimalFormatter(requiredKey, nonNumericKey))

  protected def boolean(requiredKey: String = "error.required", invalidKey: String = "error.boolean"): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey))

  protected def enumerable[A](requiredKey: String = "error.required", invalidKey: String = "error.invalid")(
    implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def localDate(
    invalidKey: String,
    allRequiredKey: String,
    twoRequiredKey: String,
    requiredKey: String,
    args: Seq[String] = Seq.empty): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, args))
}
