package utils

import org.scalatestplus.play.ServerProvider
import play.api.Application
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{DefaultWSCookie, WSClient, WSResponse}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.http.SessionKeys
import utils.SessionCookieBaker
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}


trait CreateRequestHelper extends ServerProvider {

  val defaultSeconds = 5
  implicit val defaultDuration: FiniteDuration = Duration.apply(defaultSeconds, SECONDS)

  val app: Application

  lazy val ws: WSClient = app.injector.instanceOf(classOf[WSClient])

  implicit val defaultCookie = DefaultWSCookie("CSRF-Token","nocheck")

  def bakeCookie(sessionKvs: (String, String)*): (String, String) =
    HeaderNames.COOKIE ->
      SessionCookieBaker.bakeSessionCookie(
        (sessionKvs).toMap
      )

  def getRequest(path: String, follow: Boolean = false)(sessionKvs: (String, String)*): Future[WSResponse] = {
    val allHeaders = Seq("Csrf-Token" -> "nocheck", bakeCookie(sessionKvs:_*))
    ws.url(s"http://localhost:$port/job-retention-scheme-calculator$path")
      .withHttpHeaders(allHeaders: _*)
      .withFollowRedirects(follow)
      .get()

  }

  def getRequestHeaders(path: String, follow: Boolean = false,headers: Seq[(String, String)] = Seq.empty)(sessionKvs: (String, String)*): Future[WSResponse] = {
    val allHeaders = headers ++ Seq("Csrf-Token" -> "nocheck", bakeCookie(sessionKvs:_*))
    ws.url(s"http://localhost:$port/job-retention-scheme-calculator$path")
      .withHttpHeaders(allHeaders: _*)
      .withFollowRedirects(follow)
      .get()
  }


  def internalPostRequest(path: String, formJson: JsValue, follow: Boolean = false)(sessionKvs: (String, String)*)(): Future[WSResponse] = {
    val allHeaders = Seq("Csrf-Token" -> "nocheck", bakeCookie(sessionKvs:_*))
    ws.url(s"http://localhost:$port/internal$path")
      .withHttpHeaders(allHeaders: _*)
      .withFollowRedirects(follow)
      .post(formJson)
  }

  def postRequest(path: String, formJson: JsValue, follow: Boolean = false)(sessionKvs: (String, String)*)(): Future[WSResponse] = {
    val allHeaders = Seq("Csrf-Token" -> "nocheck", bakeCookie(sessionKvs:_*))
    ws.url(s"http://localhost:$port/job-retention-scheme-calculator$path")
      .withHttpHeaders(allHeaders: _*)
      .withFollowRedirects(follow)
      .post(formJson)
  }

  def postRequestHeader(path: String, formJson: JsValue, follow: Boolean = false, headers: Seq[(String, String)] = Seq.empty)
                       (sessionKvs: (String, String)*)(): Future[WSResponse] = {

    val allHeaders = headers ++ Seq("Csrf-Token" -> "nocheck", bakeCookie(sessionKvs:_*))
    ws.url(s"http://localhost:$port/job-retention-scheme-calculator$path")
      .withHttpHeaders(allHeaders: _*)
      .withFollowRedirects(follow)
      .post(formJson)
  }


}
