/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers.actions

import com.google.inject.{Inject, Singleton}
import controllers.routes
import models.requests.IdentifierRequest
import play.api.Configuration
import play.api.mvc.Results.{Redirect, _}
import play.api.mvc.{ActionFilter, Call, Result}

import scala.concurrent.{ExecutionContext, Future}

class FeatureFlagAction(
  maybeFlag: Option[FeatureFlag],
  configuration: Configuration,
  implicit protected val executionContext: ExecutionContext
) extends ActionFilter[IdentifierRequest] {
  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] =
    maybeFlag
      .map(flag =>
        Future.successful(if (FeatureFlag.isEnabled(flag, configuration)) {
          None
        } else {
          flag match {
            case FeatureFlagKeyWithRedirect(_, redirectRoute) => Some(Redirect(redirectRoute))
            case FeatureFlagWith404(_)                        => Some(NotFound)
          }
        }))
      .getOrElse(Future.successful(None))
}

trait FeatureFlagActionProvider {
  def apply(flag: Option[FeatureFlag]): ActionFilter[IdentifierRequest]
  def apply(): ActionFilter[IdentifierRequest] = apply(None)
  def apply(flag: FeatureFlag): ActionFilter[IdentifierRequest] = apply(Some(flag))
}

class FeatureFlagActionProviderImpl @Inject()(configuration: Configuration, ec: ExecutionContext) extends FeatureFlagActionProvider {
  override def apply(flag: Option[FeatureFlag] = None): ActionFilter[IdentifierRequest] =
    new FeatureFlagAction(flag, configuration, ec)
}

sealed trait FeatureFlag {
  def key: String
}

final case class FeatureFlagKeyWithRedirect(key: String, redirectRoute: Call) extends FeatureFlag
final case class FeatureFlagWith404(key: String) extends FeatureFlag

object FeatureFlag {
  lazy val VariableJourneyFlag = FeatureFlagKeyWithRedirect(
    "variable.journey.enabled",
    routes.ComingSoonController.onPageLoad()
  )
  lazy val TopupJourneyFlag = FeatureFlagKeyWithRedirect(
    "topup.journey.enabled",
    routes.ComingSoonController.onPageLoad(showCalculateTopupsLink = true)
  )

  def isEnabled(flag: FeatureFlag, configuration: Configuration): Boolean =
    configuration.getOptional[Boolean](flag.key).getOrElse(false)
}

@Singleton
class FeatureFlagHelper @Inject()(configuration: Configuration) {

  def apply(flag: FeatureFlag): Boolean = FeatureFlag.isEnabled(flag, configuration)

}
