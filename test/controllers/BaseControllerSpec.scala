/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import akka.util.Timeout
import base.SpecBaseWithApplication
import handlers.ErrorHandler
import models.Salary
import models.requests.DataRequest
import org.scalatestplus.mockito.MockitoSugar
import pages.{PayDatePage, SalaryQuestionPage}
import play.api.http.Status._
import play.api.mvc.Results._
import play.api.mvc.{MessagesControllerComponents, Result}
import play.api.test.Helpers.{contentAsString, status}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class BaseControllerSpec extends SpecBaseWithApplication with MockitoSugar {
  lazy val fakeDataRequest = DataRequest(fakeRequest, "id", emptyUserAnswers)

  def futureResult[A]: A => Future[Result] = x => Future.successful(Ok(s"Answer: $x"))
  implicit lazy val errorHandler = injector.instanceOf[ErrorHandler]

  object BaseController extends BaseController {
    override protected def controllerComponents: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]
  }
  implicit val duration: Timeout = 5 seconds

  "getAnswer without index" when {

    "answer is not found" must {

      "return None" in {
        val result = BaseController.getAnswer(SalaryQuestionPage)(DataRequest(fakeRequest, "id", emptyUserAnswers), implicitly)

        result mustBe None
      }

    }

    "answer is found" must {

      "return Some(A)" in {
        val userAnswers = emptyUserAnswers.set(SalaryQuestionPage, Salary(100))(implicitly).success.value

        val result = BaseController.getAnswer(SalaryQuestionPage)(DataRequest(fakeRequest, "id", userAnswers), implicitly)

        result mustBe Some(Salary(100))
      }

    }

  }

  "getAnswer with index" when {

    "answer is not found" must {

      "return None" in {
        val result = BaseController.getAnswer(PayDatePage, 1)(DataRequest(fakeRequest, "id", emptyUserAnswers), implicitly)

        result mustBe None
      }

    }

    "answer is found" must {

      "return Some(A)" in {
        val userAnswers = emptyUserAnswers.set(PayDatePage, LocalDate.of(2000, 1, 1), Some(1))(implicitly).success.value

        val result = BaseController.getAnswer(PayDatePage, 1)(DataRequest(fakeRequest, "id", userAnswers), implicitly)

        result mustBe Some(LocalDate.of(2000, 1, 1))
      }

    }

  }

  "getRequiredAnswer without index" when {

    "answer is not found" must {

      "return internal server error" in {
        val result = BaseController.getRequiredAnswer(SalaryQuestionPage)(futureResult)(
          DataRequest(fakeRequest, "id", emptyUserAnswers),
          implicitly,
          errorHandler)

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

    }

    "answer is found" must {

      "execute provided function" in {
        val userAnswers = emptyUserAnswers.set(SalaryQuestionPage, Salary(123.45))(implicitly).success.value

        val result = BaseController.getRequiredAnswer(SalaryQuestionPage)(futureResult)(
          DataRequest(fakeRequest, "id", userAnswers),
          implicitly,
          errorHandler)

        status(result) mustBe OK
        contentAsString(result) mustBe "Answer: Salary(123.45)"
      }

    }

  }

  "getRequiredAnswer with index" when {

    "answer is not found" must {

      "return internal server error" in {
        val result = BaseController.getRequiredAnswer(PayDatePage, 1)(futureResult)(
          DataRequest(fakeRequest, "id", emptyUserAnswers),
          implicitly,
          errorHandler)

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

    }

    "answer is found" must {

      "execute provided function" in {
        val userAnswers = emptyUserAnswers.set(PayDatePage, LocalDate.of(2000, 1, 1), Some(1))(implicitly).success.value

        val result = BaseController.getRequiredAnswer(PayDatePage, 1)(futureResult)(
          DataRequest(fakeRequest, "id", userAnswers),
          implicitly,
          errorHandler)

        status(result) mustBe OK
        contentAsString(result) mustBe "Answer: 2000-01-01"
      }

    }

  }

}
