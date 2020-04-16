/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package views

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.data.Form
import play.api.i18n.Messages

object ViewUtils {

  def title(form: Form[_], titleStr: String, section: Option[String] = None, titleMessageArgs: Seq[String] = Seq())(
    implicit messages: Messages): String =
    titleNoForm(s"${errorPrefix(form)} ${messages(titleStr, titleMessageArgs: _*)}", section)

  def titleNoForm(title: String, section: Option[String] = None, titleMessageArgs: Seq[String] = Seq())(
    implicit messages: Messages): String =
    s"${messages(title, titleMessageArgs: _*)} - ${section.fold("")(messages(_) + " - ")}${messages("service.name")} - ${messages("site.govuk")}"

  def errorPrefix(form: Form[_])(implicit messages: Messages): String =
    if (form.hasErrors || form.hasGlobalErrors) messages("error.browser.title.prefix") else ""

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  private val dateFormatterWithoutYear = DateTimeFormatter.ofPattern("d MMMM")
  def dateToString(date: LocalDate): String = dateFormatter.format(date)
  def dateToStringWithoutYear(date: LocalDate): String = dateFormatterWithoutYear.format(date)
}
