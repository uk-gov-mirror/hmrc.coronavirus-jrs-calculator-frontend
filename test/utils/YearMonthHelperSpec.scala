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

package utils

import base.SpecBase

import java.time.{LocalDate, Month, Year, YearMonth}

class YearMonthHelperSpec extends SpecBase with YearMonthHelper {

  val aug2021: YearMonth = YearMonth.of(2021, Month.AUGUST)
  val sep2021: YearMonth = YearMonth.of(2021, Month.SEPTEMBER)
  val oct2021: YearMonth = YearMonth.of(2021, Month.OCTOBER)

  "y2020" in {

    y2020 mustBe Year.of(2020)
  }

  "y2021" in {

    y2021 mustBe Year.of(2021)
  }

  "monthExt" when {

    "withYear" in {

      Month.SEPTEMBER.inYear(y2021) mustBe sep2021
    }
  }

  "yearMonthExt" when {

    "isEqualToOrBefore" must {

      "return true" when {

        "yearMonth is equal to another yearMonth" in {

          sep2021.isEqualToOrBefore(sep2021) mustBe true
        }

        "yearMonth is before another yearMonth" in {

          sep2021.isEqualToOrBefore(oct2021) mustBe true
        }
      }

      "return false" when {

        "yearMonth is after another yearMonth" in {

          sep2021.isEqualToOrBefore(aug2021) mustBe false
        }
      }
    }

    "isEqualToOrAfter" must {

      "return true" when {

        "yearMonth is equal to another yearMonth" in {

          sep2021.isEqualToOrAfter(sep2021) mustBe true
        }

        "yearMonth is after another yearMonth" in {

          sep2021.isEqualToOrAfter(aug2021) mustBe true
        }
      }

      "return false" when {

        "yearMonth is before another yearMonth" in {

          sep2021.isEqualToOrAfter(oct2021) mustBe false
        }
      }
    }

    "isBetweenInclusive" must {

      "return true" when {

        "yearMonth is equal to min" in {

          aug2021.isBetweenInclusive(aug2021, oct2021) mustBe true
        }

        "yearMonth is equal to max" in {

          oct2021.isBetweenInclusive(aug2021, oct2021) mustBe true
        }

        "yearMonth is between min and max" in {

          sep2021.isBetweenInclusive(aug2021, oct2021) mustBe true
        }
      }

      "return false" when {

        "yearMonth is less than min" in {

          aug2021.isBetweenInclusive(sep2021, oct2021) mustBe false
        }

        "yearMonth is greater than max" in {

          oct2021.isBetweenInclusive(aug2021, sep2021) mustBe false
        }
      }
    }
  }

  "localDateExt" when {

    "getYearMonth" in {

      LocalDate.parse("2021-09-20").getYearMonth mustBe sep2021
    }
  }

}
