
import scala.concurrent.duration._
import scala.util.Random

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import java.net.URLDecoder

class JatosMassiveAssetsLoading extends Simulation {

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


  val scn = scenario("JatosMassiveAssetsLoading")
    .exitBlockOnFail {
      .exec(session => session.set("componentUuid1", "50e32e16-1831-495b-9826-f05e1eeccc87"))
        .exec(session => session.set("componentUuid2", "cf187900-9e44-44b0-9e3d-779ba80ceaed"))

        // ### 1. Component ###
        .exec(
          http("Start").get("/publix/B64GEqy93Fe").check(bodyString.saveAs("BODY")).headers(header_html)
        )
        .exec(getCookieValue(CookieKey("JATOS_IDS_0"))
        )
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
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/24px.svg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/24px.svg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/2670_fma-164754_001_00-03-37.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/2671_fma-164755_001_00-00-02.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/A-Christmas-adventure-Part-2_fma-184032_001_00-00-08.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Adventures-with-Paddy_fma-174838_001_00-07-01.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Allegro-Can-Bro-Beethoven-Symphony-No5_fma-116584_001_00-00-00.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Ancient-Heavy-Tech-Donjon_fma-158398_001_00-01-33.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Asturiass-Hole_fma-157343_001_00-02-17.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/A-weird-mechanism_fma-162973_001_00-00-47.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Beach_fma-178536_001_00-00-17.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Bleu-Matinal_fma-156848_001_00-02-34.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Childhood-scene_fma-153138_001_00-01-18.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Chocolate-Valley_fma-169769_001_00-01-05.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/City_fma-174834_001_00-01-41.wav").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_002.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_003.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_004.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_005.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_006.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_007.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_008.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_009.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_010.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_011.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_012.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_013.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_014.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_015.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_016.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_017.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_018.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_019.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_020.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_021.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_022.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_023.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_024.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_025.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_026.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_027.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_028.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_029.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_030.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_031.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_032.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_033.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_034.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_035.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_036.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_037.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_038.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_039.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_040.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_041.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_042.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_043.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_044.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_045.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_046.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_047.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_048.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_049.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_050.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_051.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_052.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_053.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_054.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_055.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_056.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_057.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_058.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_059.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_060.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_061.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_062.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_063.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_064.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_065.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_066.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_067.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_068.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_069.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_070.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_071.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2_072.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/faviconV2.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/Free_Test_Data_100KB_MP3.mp3").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_002.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_002.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_003.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_003.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_004.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_004.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_005.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_005.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_006.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_006.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_007.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_007.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_008.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_008.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_009.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_009.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_010.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_010.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_011.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_011.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_012.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_012.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_013.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_013.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_014.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_014.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_015.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_015.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_016.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_016.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_017.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_017.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_018.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_018.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_019.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_019.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_020.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_020.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_021.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_021.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_022.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_022.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_023.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_023.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_024.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_024.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_025.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_025.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_026.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_026.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_027.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_027.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_028.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_028.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_029.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_029.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_030.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_030.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_031.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_032.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_033.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_034.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_035.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_036.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_037.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_038.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_039.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_040.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_041.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_042.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_043.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_044.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_045.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_046.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_047.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_048.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_049.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_050.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_051.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_052.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_053.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_054.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_055.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_056.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_057.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_058.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_059.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_060.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_061.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_062.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_063.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_064.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_065.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_066.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_067.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_068.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_069.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_070.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_071.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_072.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_073.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_074.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_075.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_076.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_077.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_078.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_079.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_080.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_081.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_082.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_083.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_084.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_085.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_086.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_087.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_088.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_089.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_090.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_091.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_092.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_093.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_094.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_095.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_096.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_097.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_098.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_099.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_100.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_101.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_102.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_103.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_104.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_105.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_106.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_107.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_108.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_109.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_110.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_111.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_112.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_113.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_114.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_115.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_116.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_117.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_118.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_119.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_120.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_121.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_122.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_123.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_124.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_125.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_126.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_127.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_128.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_129.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_130.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_131.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_132.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_133.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_134.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_135.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_136.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_137.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_138.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_139.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_140.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_141.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_142.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_143.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_144.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_145.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_146.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_147.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_148.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_149.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_150.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_151.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_152.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_153.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_154.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_155.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_156.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_157.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_158.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_159.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_160.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images_161.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images.jpeg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/images.png").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/pexels-chokniti-khongchum-1197604-2280547.jpg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/pexels-pixabay-276267.jpg").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/sample-12s.mp3").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/sample-15s.mp3").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/sample-6s.mp3").headers(header_ajax)
        )
        .exec(
          http("Download asset file").get("/study_assets/jatos-load-test/assets/sample-9s.mp3").headers(header_ajax)
        ).pause(3 seconds)
        .exec(
          http("Post study session data").post("/publix/${studyResultUuid}/studySessionData").headers(header_ajax).body(StringBody("""{"foo":"bar"}"""))
        )
        .exec(
          http("Post result").post("/publix/${studyResultUuid}/${componentUuid1}/resultData").headers(header_ajax).body(StringBody(Random.alphanumeric.take(100).mkString("")))
        )
        .exec(
          http("Leave group").get("/publix/${studyResultUuid}/group/leave").headers(header_ajax)
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

