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

package controllers.actions

import com.google.inject.{Inject, Singleton}
import handlers.ErrorHandler
import models.requests.IdentifierRequest
import play.api.mvc.Results.{Redirect, _}
import play.api.mvc.{ActionFilter, Call, Result}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class FeatureFlagAction(
  maybeFlag: Option[FeatureFlag],
  eh: ErrorHandler,
  implicit protected val executionContext: ExecutionContext
) extends ActionFilter[IdentifierRequest] {
  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] =
    maybeFlag
      .map(flag =>
        Future.successful(if (FeatureFlag.isEnabled(flag)) {
          None
        } else {
          flag match {
            case FeatureFlagKeyWithRedirect(_, redirectRoute) => Some(Redirect(redirectRoute))
            case FeatureFlagWith404(_)                        => Some(NotFound(eh.notFoundTemplate(request)))
          }
        }))
      .getOrElse(Future.successful(None))
}

trait FeatureFlagActionProvider {
  def apply(flag: Option[FeatureFlag]): ActionFilter[IdentifierRequest]
  def apply(): ActionFilter[IdentifierRequest] = apply(None)
  def apply(flag: FeatureFlag): ActionFilter[IdentifierRequest] = apply(Some(flag))
}

class FeatureFlagActionProviderImpl @Inject()(implicit ec: ExecutionContext, eh: ErrorHandler) extends FeatureFlagActionProvider {
  override def apply(flag: Option[FeatureFlag] = None): ActionFilter[IdentifierRequest] =
    new FeatureFlagAction(flag, eh, ec)
}

sealed trait FeatureFlag {
  def key: String
}

final case class FeatureFlagKeyWithRedirect(key: String, redirectRoute: Call) extends FeatureFlag
final case class FeatureFlagWith404(key: String) extends FeatureFlag

object FeatureFlag {
  import pureconfig.ConfigSource
  import pureconfig.generic.auto._ // Do not remove this
  def isEnabled(flag: FeatureFlag): Boolean =
    Try(ConfigSource.default.at(flag.key).loadOrThrow[Boolean]).getOrElse(false) //TODO try passing flag instead of retrieving it.
}

@Singleton
class FeatureFlagHelper() {
  def apply(flag: FeatureFlag): Boolean = FeatureFlag.isEnabled(flag)
}
