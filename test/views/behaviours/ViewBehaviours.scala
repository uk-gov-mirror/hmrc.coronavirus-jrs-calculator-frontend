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

package views.behaviours

import org.jsoup.nodes.Document
import views._

trait ViewBehaviours extends ViewSpecBase {

  def normalPage(messageKeyPrefix: String, headingArgs: Seq[String] = Seq(), section: Option[String] = None)(
    implicit document: Document): Unit =
    "behave like a normal page" when {

      "rendered" must {

        "display the correct browser title" in {
          assertEqualsMessage(document, "title", title(messages(s"$messageKeyPrefix.title", headingArgs: _*), section))
        }

        "display the correct page heading" in {
          assertPageTitleEqualsMessage(document, s"$messageKeyPrefix.heading", headingArgs: _*)
        }

        if (frontendAppConfig.languageTranslationEnabled) {
          "display welsh language toggles" in {
            assertRenderedById(document, "cymraeg-switch")
          }
        }
      }
    }

  def pageWithBackLink(implicit document: Document): Unit =
    "behave like a page with a back link" must {

      "have a back link" in {
        assertRenderedById(document, "back-link")
      }
    }

  def pageWithSubHeading(subheading: String)(implicit document: Document): Unit = {

    object Selectors extends BaseSelectors

    "behave like a page with a Subheading" must {

      "display the correct subheading" in {
        assertEqualsMessage(document, Selectors.subheadingSelector, subheading)
      }
    }
  }

  def pageWithHeading(heading: String, level: Int = 1, occurrence: Int = 1)(implicit document: Document): Unit =
    s"behave like a page with a Heading$level occurrence instance $occurrence" must {

      "display the correct Heading" in {
        assertEqualsMessage(document, cssSelector = s"#main-content > div > div > div > h$level:nth-of-type($occurrence)", heading)
      }
    }

  def pageWithSignOutLink(implicit document: Document): Unit =
    "behave like a page with a Sign Out link" must {

      "have a Sign Out link" in {
        assertRenderedByCssSelector(document, "ul.govuk-header__navigation li:nth-of-type(1) a")
      }
    }

  def pageWithSubmitButton(msg: String)(implicit document: Document): Unit =
    "behave like a page with a submit button" must {

      s"have a button with message '$msg'" in {
        assertEqualsMessage(document, "#main-content > div > div > div > form > button", msg)
      }
    }

  def pageWithParagraphMessage(msg: String, selector: String)(implicit document: Document): Unit =
    s"behave like a page with paragraph $msg" must {

      s"have a button with message '$msg'" in {
        assertEqualsMessage(document, selector, msg)
      }
    }

  def pageWithExpectedMessages(checks: Seq[(String, String)])(implicit document: Document): Unit = checks.foreach {
    case (cssSelector, message) =>
      s"element with cssSelector '$cssSelector'" must {

        s"have message '$message'" in {
          val elem = document.select(cssSelector)
          elem.first.text() mustBe message
        }
      }
  }

  def pageWithBulletedPoint(msg: String, bullet: Int)(implicit document: Document): Unit =
    s"behave like a page with bullet point$bullet" must {

      s"have a button with message '$msg'" in {
        assertEqualsMessage(document,
                            cssSelector = s"#main-content > div > div > div > ul > li:nth-child($bullet)",
                            expectedMessageKey = msg)
      }
    }

  def pageWithHeading(heading: String)(implicit document: Document): Unit =
    "behave like a page with a Heading" must {

      "display the correct Heading" in {
        assertEqualsMessage(document, "#main-content > div > div > div h1", heading)
      }
    }

  def pageWithWarningText(msg: String)(implicit document: Document): Unit =
    "behave like a page with a warning" must {

      s"have a warning message '$msg'" in {
        assertEqualsMessage(document,
                            "#main-content > div > div > div > form > div > strong > span.govuk-\\!-font-weight-bold > div:nth-child(1)",
                            msg)
      }
    }

  def pageWithParagraphCustom(msg: String, child: Int)(implicit document: Document): Unit =
    s"behave like a page with paragraph $msg" must {

      s"have a button with message '$msg'" in {
        assertEqualsMessage(document, s"#main-content > div > div > div > form > p:nth-child($child)", msg)
      }
    }

  def pageWithGuidance(msg: String)(implicit document: Document): Unit =
    "behave like a page with some Guidance" must {

      s"have a guidance message '$msg'" in {
        assertEqualsMessage(document, "#main-content > div > div > div > form > h1", msg)
      }
    }

  def pageWithMultiFieldError(key: String)(implicit document: Document): Unit =
    "behave like a page with a multiField error" in {
      assertRenderedById(document, s"$key-multiField-error-message")
    }

  def pageWithLink(selector: String = "main a:nth-of-type(1)", msg: String, url: String)(implicit document: Document) =
    "has a link to view previous claims" which {

      "has the correct text" in {
        document.select(selector).text mustBe msg
      }

      "has the correct destination" in {
        document.select(selector).attr("href") mustBe url
      }
    }

  def pageWithWhatToInclude()(implicit doc: Document) =
    doc.select("#what-to-include").isEmpty mustBe false
}
