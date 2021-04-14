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

import config.featureSwitch.FeatureSwitch.prefix

object FeatureSwitch {

  val prefix = "features"

  val switches: Seq[FeatureSwitch] = Seq(
    WelshLanguageFeature,
    ShowNewStartPage,
    ExtensionTwoNewStarterFlow,
    StatutoryLeaveFlow,
    WriteConfirmationTestCasesToFile
  )

  val booleanFeatureSwitches: Seq[BooleanFeatureSwitch]       = switches.collect { case a: BooleanFeatureSwitch => a }
  val customValueFeatureSwitch: Seq[CustomValueFeatureSwitch] = switches.collect { case a: CustomValueFeatureSwitch => a }
  val configurableConstantsKeys: Seq[String]                  = Seq[String]().map(key => s"constants.$key")

  def apply(str: String): FeatureSwitch =
    switches find (_.name == str) match {
      case Some(switch) => switch
      case None         => throw new IllegalArgumentException("Invalid feature switch: " + str)
    }

  def get(str: String): Option[FeatureSwitch] = switches find (_.name == str)

}

sealed trait FeatureSwitch {
  val name: String
  val displayText: String
  val hint: Option[String] = None
}

sealed trait BooleanFeatureSwitch extends FeatureSwitch

sealed trait CustomValueFeatureSwitch extends FeatureSwitch {
  val values: Set[String]
}

object WelshLanguageFeature extends BooleanFeatureSwitch {
  override val name: String        = s"$prefix.welsh-translation"
  override val displayText: String = "Enable or Disable welsh language translation option"
}

object ShowNewStartPage extends BooleanFeatureSwitch {
  override val name: String        = s"$prefix.showNewStartPage"
  override val displayText: String = "Enable or Disable the new Start Page content"
}

object ExtensionTwoNewStarterFlow extends BooleanFeatureSwitch {
  override val name: String        = s"$prefix.extensionTwoNewStarterFlow"
  override val displayText: String = "Enable or Disable the Extension Two New Start Flow"
}

object StatutoryLeaveFlow extends BooleanFeatureSwitch {
  override val name: String        = s"$prefix.statutoryLeaveFlow"
  override val displayText: String = "Enables the Statutory Leave flow (if selected)"
}

object WriteConfirmationTestCasesToFile extends BooleanFeatureSwitch {
  override val name: String        = s"$prefix.writeConfirmationTestCasesToFile"
  override val displayText: String = "writes the confirmation test cases to file"
}
