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

import org.scalatestplus.play.PlaySpec
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, TableRow}

class TableModelSpec extends PlaySpec {

  "TableModel" must {

    "provide an interface to GovukTable" in {

      TableModel(
        Some("heading"),
        Some(
          Header(
            Seq(
              Cell(Html("Header A")),
              Cell(Html("Header B"))
            )
          )),
        Seq(
          Row(
            Seq(
              Cell(Html("Row 1 Cell A")),
              Cell(Html("Row 1 Cell B"), classes = "customClass")
            )
          ),
          Row(
            Seq(
              Cell(Html("Row 2 Cell A")),
              Cell(Html("Row 2 Cell B"), classes = "customClass")
            )
          ),
          Row(
            Seq(
              Cell(Html("Row 3 Cell A")),
              Cell(Html("Row 3 Cell B"), classes = "customClass")
            )
          )
        )
      ).toGovukTable mustBe Table(
        head = Some(
          Seq(
            HeadCell(HtmlContent(Html("Header A"))),
            HeadCell(HtmlContent(Html("Header B")))
          )
        ),
        rows = Seq(
          Seq(
            TableRow(HtmlContent(Html("Row 1 Cell A"))),
            TableRow(HtmlContent(Html("Row 1 Cell B")), classes = "customClass")
          ),
          Seq(
            TableRow(HtmlContent(Html("Row 2 Cell A"))),
            TableRow(HtmlContent(Html("Row 2 Cell B")), classes = "customClass")
          ),
          Seq(
            TableRow(HtmlContent(Html("Row 3 Cell A"))),
            TableRow(HtmlContent(Html("Row 3 Cell B")), classes = "customClass")
          )
        ),
        caption = Some("heading")
      )
    }
  }
}
