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

import play.api.Logger.logger
import java.io.{BufferedWriter, File, FileWriter}

trait FileUtil {

  def writeFile(filename: String, string: String, path: String = "", append: Boolean = false): Unit = {
    new File(path).mkdirs()
    val file = new File(path + filename)
    val bw   = new BufferedWriter(new FileWriter(file, append))
    bw.write(string)
    bw.close()
    logger.debug(s"[FileUtil][writeFile] file written to $path$filename")
  }
}
