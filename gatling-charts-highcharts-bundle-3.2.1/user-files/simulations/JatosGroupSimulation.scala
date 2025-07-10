
import scala.concurrent.duration._
import scala.util.Random

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import java.net.URLDecoder

class JatosGroupSimulation extends Simulation {

  val cpus = System.getenv("JATOS_CPUS")
  val mem = System.getenv("JATOS_MEM")
  val javaXmx = System.getenv("JAVA_XMX")
  val dbPoolSize = System.getenv("JATOS_DB_CONNECTIONPOOL_SIZE")
  val threadPoolSize = System.getenv("JATOS_THREADPOOL_SIZE")
  val host = System.getenv("JATOS_HOST")
  val studyCode = System.getenv("JATOS_STUDY_CODE")
  val simulation = System.getenv("GATLING_SIMULATION")
  val injectType = System.getenv("GATLING_INJECT_TYPE")
  val users = System.getenv("GATLING_USERS").toDouble
  val duration = System.getenv("GATLING_DURATION").toLong
  val successRate = System.getenv("GATLING_SUCCESS_RATE").toDouble

  println(s"cpus:$cpus mem:$mem javaXmx:$javaXmx dbPoolSize:$dbPoolSize threadPoolSize:$threadPoolSize $host $studyCode $simulation $injectType users:$users duration:$duration successRate:$successRate")

  val httpProtocol = http
    .baseUrl(s"https://$host")
    .wsBaseUrl(s"wss://$host")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:69.0) Gecko/20100101 Firefox/69.0")

  val header_html = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val header_json = Map(
    "Accept" -> "application/json, text/javascript, */*; q=0.01",
    "X-Requested-With" -> "XMLHttpRequest")

  val header_text = Map("Content-Type" -> "text/plain")

  val header_ajax = Map(
    "Content-Type" -> "text/plain; charset=UTF-8",
    "X-Requested-With" -> "XMLHttpRequest")


  val scn = scenario("JatosGroupSimulation")
    .exec(session => session.set("componentUuid1", "50e32e16-1831-495b-9826-f05e1eeccc87"))
    .exec(session => session.set("componentUuid2", "cf187900-9e44-44b0-9e3d-779ba80ceaed"))
    .exec(
      http("Start").get("/publix/B64GEqy93Fe").check(bodyString.saveAs("BODY")).headers(header_html)
    ).exec(getCookieValue(CookieKey("JATOS_IDS_0"))
  ).exec(session => {
    val cookie = session("JATOS_IDS_0").as[String]
    val cookieParas = parseUrlParameters(cookie)
    val studyResultUuid = cookieParas("studyResultUuid")
    println(s"JATOS_IDS_0: $studyResultUuid")
    session.set("studyResultUuid", studyResultUuid)
  }).exec(
    http("Get init data").get("/publix/${studyResultUuid}/${componentUuid1}/initData").headers(header_json)
  ).exec(
    ws("Open batch channel").wsName("batchChannel").connect("/publix/${studyResultUuid}/batch/open")
  ).exec(
    http("Heartbeat").post("/publix/${studyResultUuid}/heartbeat").headers(header_ajax)
  ).exec(
    ws("Join group").wsName("groupChannel").connect("/publix/${studyResultUuid}/group/join")
  ).pause(5 seconds).exec(
    http("Reassign group").get("/publix/${studyResultUuid}/group/reassign").headers(header_ajax)
  ).pause(5 seconds).exec(
    http("Leave group").get("/publix/${studyResultUuid}/group/leave").headers(header_ajax)
  ).pause(5 seconds).exec(
    http("Post study session data").post("/publix/${studyResultUuid}/studySessionData").headers(header_ajax).body(StringBody("""{"foo":"bar"}"""))
  ).exec(
    http("Post results").post("/publix/${studyResultUuid}/${componentUuid1}/resultData").headers(header_ajax).body(StringBody("""{"foo":"bar"}"""))
  ).exec(
    http("Finish study").get("/publix/${studyResultUuid}/end").headers(header_ajax)
  ).exec(ws("Close batch channel").wsName("batchChannel").close)

  def parseUrlParameters(url: String) = {
    url.split("&").map(v => {
      val m = v.split("=", 2).map(s => URLDecoder.decode(s, "UTF-8"))
      m(0) -> m(1)
    }).toMap
  }

  injectType match {
    case "atOnceUsers" =>
      setUp(scn.inject(atOnceUsers(users.toInt))).protocols(httpProtocol).assertions(
        global.successfulRequests.percent.gte(successRate)
      )
    case "rampUsersPerSec" =>
      setUp(scn.inject(rampUsersPerSec(0) to (users) during (duration seconds))).protocols(httpProtocol).assertions(
        global.successfulRequests.percent.gte(successRate)
      )
    case "constantConcurrentUsers" =>
      setUp(scn.inject(constantConcurrentUsers(users.toInt) during (duration seconds))).protocols(httpProtocol).assertions(
        global.successfulRequests.percent.gte(successRate)
      )
    case "rampConcurrentUsers" =>
      setUp(scn.inject(rampConcurrentUsers(0) to (users.toInt) during (duration seconds))).protocols(httpProtocol).assertions(
        global.successfulRequests.percent.gte(successRate)
      )
    case _ => "injectType - no match"
  }
  //  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  //  setUp(scn.inject(rampUsersPerSec(0.1) to (0.3) during (600 seconds))).protocols(httpProtocol)
  //  setUp(scn.inject(constantConcurrentUsers(20) during (6000 seconds))).protocols(httpProtocol)
  //  setUp(scn.inject(rampConcurrentUsers(0) to (100) during (600 seconds))).protocols(httpProtocol)
}

