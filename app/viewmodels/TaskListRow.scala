/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package viewmodels

import play.api.mvc.Call

case class TaskListRow(name: String, link: Call, state: String)
