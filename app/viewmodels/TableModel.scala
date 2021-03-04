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

import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}

case class Cell(value: Html, classes: String = "")

case class Header(cells: Seq[Cell]) {
  def toGovukHeaderCells: Seq[HeadCell] = cells.map { cell =>
    HeadCell(
      content = HtmlContent(cell.value),
      classes = cell.classes
    )
  }
}

case class Row(cells: Seq[Cell]) {
  def toGovukTableRow: Seq[TableRow] =
    cells.map(
      cell =>
        TableRow(
          content = HtmlContent(cell.value),
          classes = cell.classes
      ))
}

case class TableModel(heading: Option[String], headerRow: Option[Header], rows: Seq[Row]) {

  def toGovukTable = Table(
    caption = heading,
    rows = rows.map(_.toGovukTableRow),
    head = headerRow.map(_.toGovukHeaderCells)
  )
}
