/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.TestOnlyNICGrantCalculatorFormProvider
import javax.inject.Inject
import models.{FurloughPayment, Mode, PayPeriod, TestOnlyNICGrantModel, UserAnswers}
import navigation.Navigator
import pages.TestOnlyNICGrantCalculatorPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsNumber, JsString}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.NicCalculatorService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.{NicResultView, TestOnlyNICGrantCalculatorView}

import scala.concurrent.{ExecutionContext, Future}

class TestOnlyNICGrantCalculatorController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  nicCalculatorService: NicCalculatorService,
  navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: TestOnlyNICGrantCalculatorFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TestOnlyNICGrantCalculatorView,
  nicResultView: NicResultView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) { implicit request =>
    val preparedForm =
      request.userAnswers.getOrElse(UserAnswers(request.internalId)).get(TestOnlyNICGrantCalculatorPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(
                               request.userAnswers
                                 .getOrElse(UserAnswers(request.internalId))
                                 .set(TestOnlyNICGrantCalculatorPage, value))
            _ <- {
              val nic = nicCalculatorService
                .calculateNic(
                  value.frequency,
                  FurloughPayment(value.furloughedAmount, PayPeriod(value.startDate, value.endDate)))
              sessionRepository.set(updatedAnswers.copy(data = updatedAnswers.data.+("nic", JsNumber(nic))))
            }
          } yield {
            Redirect(routes.TestOnlyNICGrantCalculatorController.showResult())
        }
      )
  }

  def showResult(): Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val storedNic = sessionRepository.get(request.internalId).map(_.flatMap(_.data.value.get("nic")))
    storedNic.map(r => Ok(nicResultView(r.map(_.toString()).getOrElse("no nic found in session"))))
  }
}
