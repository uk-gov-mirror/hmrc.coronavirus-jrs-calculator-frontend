/*
 * Copyright 2021 HM Revenue & Customs
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

package config.featureSwitch

import config.FrontendAppConfig

import scala.sys.SystemProperties

trait FeatureSwitching {

  val FEATURE_SWITCH_ON = "true"
  val FEATURE_SWITCH_OFF = "false"

  def isEnabled(featureSwitch: FeatureSwitch)(implicit config: FrontendAppConfig): Boolean =
    getValue(featureSwitch).toBoolean

  def isDisabled(featureSwitch: FeatureSwitch)(implicit config: FrontendAppConfig): Boolean =
    !getValue(featureSwitch).toBoolean

  def getValue(featureSwitch: FeatureSwitch)(implicit config: FrontendAppConfig): String =
    getValue(featureSwitch.name)

  def getValue(key: String)(implicit config: FrontendAppConfig): String =
    sys.props.get(key).getOrElse(config.servicesConfig.getString(key))

  def setValue(featureSwitch: FeatureSwitch, value: String): SystemProperties =
    setValue(featureSwitch.name, value)

  def setValue(key: String, value: String): SystemProperties =
    sys.props += key -> value

  def enable(featureSwitch: FeatureSwitch): Unit = setValue(featureSwitch, FEATURE_SWITCH_ON)

  def disable(featureSwitch: FeatureSwitch): Unit = setValue(featureSwitch, FEATURE_SWITCH_OFF)

  def fsGuard[T](switch: FeatureSwitch, offAction: T)(onAction: => T)(implicit config: FrontendAppConfig): T =
    if (isEnabled(switch)) onAction else offAction
}

object FeatureSwitching extends FeatureSwitching
