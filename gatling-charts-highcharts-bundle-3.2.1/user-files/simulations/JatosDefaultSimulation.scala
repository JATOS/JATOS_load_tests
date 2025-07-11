
import scala.concurrent.duration._
import scala.util.Random

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import java.net.URLDecoder

class JatosDefaultSimulation extends Simulation {
  val jatosVersion = System.getenv("JATOS_VERSION")
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

  println(s"$jatosVersion cpus:$cpus mem:$mem javaXmx:$javaXmx dbPoolSize:$dbPoolSize threadPoolSize:$threadPoolSize $host $studyCode $simulation $injectType users:$users duration:$duration successRate:$successRate")

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

  val scn = scenario("JatosDefaultSimulation")
    .exitBlockOnFail {
      exec(session => session.set("componentUuid1", "50e32e16-1831-495b-9826-f05e1eeccc87"))
        .exec(session => session.set("componentUuid2", "cf187900-9e44-44b0-9e3d-779ba80ceaed"))
        .exec(session => session.set("filename", "1577217737269.video")) // Filename for result file upload / download

        // ### 1. Component ###
        .exec(
          http("Start").get(s"/publix/$studyCode").check(bodyString.saveAs("BODY")).headers(header_html)
        )
        .exec(getCookieValue(CookieKey("JATOS_IDS_0")))
        .exec(session => {
          val cookie = session("JATOS_IDS_0").as[String]
          val cookieParas = parseUrlParameters(cookie)
          val studyResultUuid = cookieParas("studyResultUuid")
          println(s"JATOS_IDS_0: $studyResultUuid")
          session.set("studyResultUuid", studyResultUuid)
        })
        .exec(
          http("Get init data").get("/publix/${studyResultUuid}/${componentUuid1}/initData").headers(header_json)
        )
        .exec(
          ws("Open batch channel").wsName("batchChannel").connect("/publix/${studyResultUuid}/batch/open")
        )
        .exec(
          http("Heartbeat").post("/publix/${studyResultUuid}/heartbeat").headers(header_text)
        )
        .exec(
          ws("Join group").wsName("groupChannel").connect("/publix/${studyResultUuid}/group/join")
        ).pause(1 seconds)
        .exec(
          http("File upload").post("/publix/${studyResultUuid}/${componentUuid1}/files/${filename}").bodyPart(RawFileBodyPart("file", "${filename}").fileName("${filename}")).asMultipartForm
        ).pause(1 seconds)
        .exec(
          http("Post study session data").post("/publix/${studyResultUuid}/studySessionData").headers(header_ajax).body(StringBody("""{"foo":"bar"}"""))
        ).pause(1 seconds)
        .exec(
          http("Post result").post("/publix/${studyResultUuid}/${componentUuid1}/resultData").headers(header_ajax).body(StringBody(Random.alphanumeric.take(5000).mkString("")))
        )
        .exec(ws("Close batch channel").wsName("batchChannel").close)
        .exec(ws("Close group channel").wsName("groupChannel").close)

        // ### 2. Component ###
        .exec(
          http("Next component").get("/publix/${studyResultUuid}/${componentUuid2}/start?message=load%20test%20message%20%C2%A7%24%25%26").headers(header_html)
        )
        .exec(
          http("Get init data").get("/publix/${studyResultUuid}/${componentUuid2}/initData").headers(header_json)
        )
        .exec(
          ws("Open batch channel").wsName("batchChannel").connect("/publix/${studyResultUuid}/batch/open")
        )
        .exec(
          http("Heartbeat").post("/publix/${studyResultUuid}/heartbeat").headers(header_text)
        )
        .exec(
          ws("Join group").wsName("groupChannel").connect("/publix/${studyResultUuid}/group/join")
        ).pause(1 seconds)
        .exec(
          http("Download file").get("/publix/${studyResultUuid}/files/${filename}").headers(header_ajax)
        ).pause(1 seconds)
        .exec(
          http("Post result").post("/publix/${studyResultUuid}/${componentUuid2}/resultData").headers(header_ajax).body(StringBody(Random.alphanumeric.take(5000).mkString("")))
        )
        .exec(
          http("Leave group").get("/publix/${studyResultUuid}/group/leave").headers(header_ajax)
        )
        .exec(
          http("Post study session data").post("/publix/${studyResultUuid}/studySessionData").headers(header_json).body(StringBody("""{"foo":"bar"}"""))
        )
        .exec(
          http("Finish study").get("/publix/${studyResultUuid}/end").headers(header_ajax)
        )
        .exec(ws("Close batch channel").wsName("batchChannel").close)
    }

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

