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

package pages.behaviours

import cats.data.Chain
import cats.data.Validated.{Invalid, Valid}
import cats.scalatest.{ValidatedMatchers, ValidatedValues}
import generators.Generators
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.{MustMatchers, OptionValues, TryValues, WordSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.libs.json._
import queries.Gettable

trait PageBehaviours
    extends WordSpec with MustMatchers with ScalaCheckPropertyChecks with Generators with OptionValues with ValidatedValues
    with ValidatedMatchers with TryValues {

  def emptyError(path: JsPath, error: String = "error.path.missing"): Invalid[Chain[JsError]] =
    Invalid(Chain(JsError(path -> JsonValidationError(List(error)))))

  class BeRetrievable[A] {
    def apply[P <: Gettable[A]](genP: Gen[P])(implicit ev1: Arbitrary[A], ev2: Format[A]): Unit = {

      import models.RichJsObject

      "return None" when {

        "being retrieved from UserAnswers" when {

          "the question has not been answered" in {

            val gen = for {
              page        <- genP
              userAnswers <- arbitrary[UserAnswers]
              json = userAnswers.data.removeObject(page.path).asOpt.getOrElse(userAnswers.data)
            } yield (page, userAnswers.copy(data = json))

            forAll(gen) {
              case (page, userAnswers) =>
                userAnswers.getV(page) mustBe invalid
            }
          }
        }
      }

      "return invalid" when {

        "being retrieved from UserAnswers" when {

          "the question has not been answered" in {

            val gen = for {
              page        <- genP
              userAnswers <- arbitrary[UserAnswers]
              json = userAnswers.data.removeObject(page.path).asOpt.getOrElse(userAnswers.data)
            } yield (page, userAnswers.copy(data = json))

            forAll(gen) {
              case (page, userAnswers) =>
                userAnswers.getV(page) mustBe emptyError(page.path)
            }
          }
        }
      }

      "return the saved value" when {

        "being retrieved from UserAnswers" when {

          "the question has been answered" in {

            val gen = for {
              page        <- genP
              savedValue  <- arbitrary[A]
              userAnswers <- arbitrary[UserAnswers]
              json = userAnswers.data.setObject(page.path, Json.toJson(savedValue)).asOpt.value
            } yield (page, savedValue, userAnswers.copy(data = json))

            forAll(gen) {
              case (page, savedValue, userAnswers) =>
                userAnswers.getV(page).value mustEqual savedValue
            }
          }
        }
      }

      "return the validated saved value" when {

        "being retrieved from UserAnswers" when {

          "the question has been answered" in {

            val gen = for {
              page        <- genP
              savedValue  <- arbitrary[A]
              userAnswers <- arbitrary[UserAnswers]
              json = userAnswers.data.setObject(page.path, Json.toJson(savedValue)).asOpt.value
            } yield (page, savedValue, userAnswers.copy(data = json))

            forAll(gen) {
              case (page, savedValue, userAnswers) =>
                userAnswers.getV(page) mustEqual Valid(savedValue)
            }
          }
        }
      }
    }
  }

  class BeSettable[A] {
    def apply[P <: QuestionPage[A]](genP: Gen[P])(implicit ev1: Arbitrary[A], ev2: Format[A]): Unit =
      "be able to be set on UserAnswers" in {

        val gen = for {
          page        <- genP
          newValue    <- arbitrary[A]
          userAnswers <- arbitrary[UserAnswers]
        } yield (page, newValue, userAnswers)

        forAll(gen) {
          case (page, newValue, userAnswers) =>
            val updatedAnswers = userAnswers.set(page, newValue).success.value
            updatedAnswers.getV(page).value mustEqual newValue
        }
      }

  }

  class BeRemovable[A] {
    def apply[P <: QuestionPage[A]](
      genP: Gen[P]
    )(implicit ev1: Arbitrary[A], ev2: Format[A]): Unit =
      "be able to be removed from UserAnswers" in {

        val gen = for {
          page        <- genP
          savedValue  <- arbitrary[A]
          userAnswers <- arbitrary[UserAnswers]
        } yield (page, userAnswers.set(page, savedValue).success.value)

        forAll(gen) {
          case (page, userAnswers) =>
            val updatedAnswers = userAnswers.remove(page).success.value
            updatedAnswers.getV(page) mustEqual emptyError(page.path)
        }
      }
  }

  def beRetrievable[A]: BeRetrievable[A] = new BeRetrievable[A]

  def beSettable[A]: BeSettable[A] = new BeSettable[A]

  def beRemovable[A]: BeRemovable[A] = new BeRemovable[A]
}
