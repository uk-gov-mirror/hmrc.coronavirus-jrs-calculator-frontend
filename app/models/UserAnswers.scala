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

import play.api.libs.json._
import queries.{Gettable, Query, Settable}

import scala.util.{Failure, Success, Try}
import cats.data.{NonEmptyChain, NonEmptyList, ValidatedNec}
import cats.syntax.validated._

final case class UserAnswers(
  id: String,
  data: JsObject = Json.obj(),
  lastUpdated: LocalDateTime = LocalDateTime.now
) {

  type Answer[A] = ValidatedNec[JsError, A]

  private def path[T <: Query](page: T, idx: Option[Int]): JsPath = idx.fold(page.path)(idx => page.path \ (idx - 1))

  def getV[A](page: Gettable[A], idx: Option[Int] = None)(implicit rds: Reads[A]): Answer[A] =
    Reads.at(path(page, idx)).reads(data) match {
      case JsSuccess(value, _) => value.validNec
      case error @ JsError(_) =>
        NonEmptyChain
          .fromNonEmptyList(NonEmptyList.fromListUnsafe(List(error)))
          .invalid[A]
    }

  def get[A](page: Gettable[A], idx: Option[Int] = None)(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(path(page, idx))).reads(data).getOrElse(None)

  def getList[A](page: Gettable[A])(implicit rds: Reads[A]): Seq[A] =
    page.path.read[Seq[A]].reads(data).getOrElse(Seq.empty)

  def setListWithInvalidation[A](page: Settable[A] with Gettable[A], value: A, idx: Int)(
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

    val updatedData = data.setObject(path(page, idx), JsNull) match {
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

object UserAnswers {

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
