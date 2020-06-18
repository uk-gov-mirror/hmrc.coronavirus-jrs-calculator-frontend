/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import java.time.LocalDateTime

import cats.data.{NonEmptyChain, NonEmptyList, ValidatedNec}
import cats.syntax.validated._
import models.UserAnswers.AnswerV
import org.slf4j.Logger
import play.api.libs.json._
import queries.{Gettable, Query, Settable}

import scala.util.{Failure, Success, Try}

final case class UserAnswers(
  id: String,
  data: JsObject = Json.obj(),
  lastUpdated: LocalDateTime = LocalDateTime.now
) {

  private def path[T <: Query](page: T, idx: Option[Int]): JsPath = idx.fold(page.path)(idx => page.path \ (idx - 1))

  def getV[A](page: Gettable[A], idx: Option[Int] = None)(implicit rds: Reads[A]): AnswerV[A] =
    Reads.at(path(page, idx)).reads(data) match {
      case JsSuccess(value, _) => value.validNec
      case error @ JsError(_) =>
        NonEmptyChain
          .fromNonEmptyList(NonEmptyList.fromListUnsafe(List(AnswerValidation(error, data, idx))))
          .invalid[A]
    }

  def getList[A](page: Gettable[A])(implicit rds: Reads[A]): Seq[A] =
    page.path.read[Seq[A]].reads(data).getOrElse(Seq.empty)

  def setListItemWithInvalidation[A](page: Settable[A] with Gettable[A], value: A, idx: Int)(
    implicit writes: Writes[A],
    rds: Reads[A]): Try[UserAnswers] = {
    val list = page.path.read[Seq[A]].reads(data).getOrElse(Seq.empty)
    val amendedList = list.patch(idx - 1, Seq(value), list.size)

    val updatedData = data.setObject(path(page, None), Json.toJson(amendedList)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }
  }

  def setList[A](page: Settable[A], value: Seq[A])(implicit writes: Writes[A]): Try[UserAnswers] = {
    val updatedData = data.setObject(path(page, None), Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(None, updatedAnswers)
    }
  }

  def set[A](page: Settable[A], value: A, idx: Option[Int] = None)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData = data.setObject(path(page, idx), Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](page: Settable[A], idx: Option[Int] = None): Try[UserAnswers] = {

    val result = idx match {
      case Some(_) => data.setObject(path(page, idx), JsNull)
      case None    => data.removeObject(path(page, None))
    }

    val updatedData = result match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(None, updatedAnswers)
    }
  }
}

trait AnswerValidation {

  def message: String

  def underlying: JsError

  def data: JsObject
}

object AnswerValidation {
  def apply(
    jsError: JsError,
    data: JsObject = JsObject(Seq.empty),
    idx: Option[Int] = None
  ): AnswerValidation =
    if (jsError.errors.size == 1) {
      jsError.errors.head match {
        case (path, error) if error == Seq(JsonValidationError(Seq("error.path.missing"))) =>
          EmptyAnswerError(path, jsError, data)
        case (path, error) =>
          GenericValidationError("Generic exception", jsError, data)
      }
    } else {
      GenericValidationError("Generic exception", jsError, data)
    }
}

case class EmptyAnswerError(
  message: String,
  underlying: JsError,
  data: JsObject
) extends AnswerValidation

object EmptyAnswerError {
  def apply(
    path: JsPath,
    jsError: JsError = JsError(),
    data: JsObject = JsObject(Seq.empty)
  ): EmptyAnswerError =
    EmptyAnswerError(
      s"${path.toJsonString} was empty",
      jsError,
      data
    )
}

case class DateParsingException(
  message: String,
  underlying: JsError,
  data: JsObject = JsObject(Seq.empty)
) extends AnswerValidation

case class GenericValidationError(
  message: String,
  underlying: JsError,
  data: JsObject = JsObject(Seq.empty)
) extends AnswerValidation

object UserAnswers {

  type AnswerV[A] = ValidatedNec[AnswerValidation, A]

  def logErrors(nec: NonEmptyChain[AnswerValidation])(implicit logger: Logger): Unit = {
    logger.error(s"Encountered validation errors")
    nec.toNonEmptyList.toList.foreach { validation =>
      val err = validation.underlying.errors.headOption match {
        case Some((path, errors)) => s"${path.toJsonString}; JSON error: ${errors.map(_.toString)}"
        case None => ""
      }
      logger.error(s"""
        | Encountered validation error: ${validation.message};
        | Underlying error: $err;
        | Answer data: ${Json.prettyPrint(validation.data)}
        | """
      )
    }
  }
  def logWarnings(nec: NonEmptyChain[AnswerValidation])(implicit logger: Logger): Unit = {
    logger.error(s"Encountered validation warnings")
    nec.toNonEmptyList.toList.foreach { validation =>
      val err = validation.underlying.errors.headOption match {
        case Some((path, errors)) => s"${path.toJsonString}; JSON error: ${errors.map(_.toString)}"
        case None => ""
      }
      logger.warn(s"""
                      | Encountered validation error: ${validation.message};
                      | Underlying error: $err;
                      | Answer data: ${Json.prettyPrint(validation.data)}
                      | """
      )
    }
  }

  implicit lazy val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoDateTimeFormats.localDateTimeRead)
    )(UserAnswers.apply _)
  }

  implicit lazy val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoDateTimeFormats.localDateTimeWrite)
    )(unlift(UserAnswers.unapply))
  }
}
