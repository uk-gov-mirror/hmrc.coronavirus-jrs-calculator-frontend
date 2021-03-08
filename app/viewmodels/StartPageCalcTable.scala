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

package viewmodels

import play.api.i18n.Messages
import play.twirl.api.Html

import javax.inject.Inject

case class StartPageCalcTable @Inject()(
  table: views.html.components.table,
  strong: views.html.components.strong,
  visuallyhidden: views.html.components.visuallyhidden) {

  def renderTable(implicit messages: Messages): Html =
    table(
      TableModel(
        heading = None,
        headerRow = Some(
          Header(
            Seq(
              Cell(visuallyhidden(Html(messages("startPage.whatCanYouClaim.table.column1HeadingHidden")))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.column2Heading"))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.column3Heading")))
            )
          )),
        rows = Seq(
          Row(
            Seq(
              Cell(strong(Html(messages("month.5")))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.mayJunGovt"))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.naughtPercent")))
            )
          ),
          Row(
            Seq(
              Cell(strong(Html(messages("month.6")))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.mayJunGovt"))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.naughtPercent")))
            )
          ),
          Row(
            Seq(
              Cell(strong(Html(messages("month.7")))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.julGovt"))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.julyEmp")))
            )
          ),
          Row(
            Seq(
              Cell(strong(Html(messages("month.8")))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.augSepGovt"))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.augSepEmp")))
            )
          ),
          Row(
            Seq(
              Cell(strong(Html(messages("month.9")))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.augSepGovt"))),
              Cell(Html(messages("startPage.whatCanYouClaim.table.augSepEmp")))
            )
          )
        )
      )
    )
}
