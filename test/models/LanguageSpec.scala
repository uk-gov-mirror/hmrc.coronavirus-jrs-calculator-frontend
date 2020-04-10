/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package models

import org.scalatest.{EitherValues, FreeSpec, MustMatchers}
import play.api.mvc.PathBindable

class LanguageSpec extends FreeSpec with MustMatchers with EitherValues {

  "Language" - {

    val pathBindable = implicitly[PathBindable[Language]]

    "must bind Cymraeg from a URL" in {

      val result = pathBindable.bind("language", Language.Cymraeg.toString)
      result.right.value mustEqual Language.Cymraeg
    }

    "must bind English from a URL" in {

      val result = pathBindable.bind("language", Language.English.toString)
      result.right.value mustEqual Language.English
    }

    "must unbind Cymraeg" in {

      val result = pathBindable.unbind("language", Language.Cymraeg)
      result mustEqual Language.Cymraeg.toString
    }

    "must unbind English" in {

      val result = pathBindable.unbind("language", Language.English)
      result mustEqual Language.English.toString
    }
  }
}
