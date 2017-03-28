package com.example.camundapoc.gatling

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

  val baseUrls: List[String] = List(
    "http://localhost:8080",
    "http://localhost:8081",
    "http://localhost:8083"
  )

  val httpProtocol = http
    .inferHtmlResources()
    .acceptHeader("application/hal+json, application/json; q=0.5")
    .acceptEncodingHeader("gzip, deflate, sdch, br")
    .acceptLanguageHeader("en-GB,en-US;q=0.8,en;q=0.6")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")

  val headers_0 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Content-Type" -> "application/x-www-form-urlencoded",
    "Accept-Encoding" -> "gzip, deflate, br"
  )

  val headers_4 = Map(
    "Accept-Encoding" -> "gzip, deflate, br",
    "Content-Type" -> "application/json"
  )

  val loginRequest = exec(http("logon_request")
    .post("${url}/api/admin/auth/user/default/login/tasklist")
    .headers(headers_0)
    .formParam("username", "demo")
    .formParam("password", "demo")
  )

  val startProcess = exec(
    http("start_process_request")
      .post("${url}/api/engine/engine/default/process-definition/${processDef}/submit-form")
      .headers(headers_4)
      .body(RawFileBody("RecordedSimulation_0008_request.txt"))
  )


  val loginThenStartProcesses = scenario("Login, Start Process")
    .exec(session => {
      val r = scala.util.Random
      session.set("url", baseUrls.get(r.nextInt(baseUrls.size)))
    })
    .exec(session => {
      session.set("processDef", "TestProcess:1:93837135-0a6c-11e7-8782-80fa5b27feef")
    })
    .exec(loginRequest)
    .exec(startProcess)


  setUp(
    loginThenStartProcesses.inject(rampUsers(3000) over (60 seconds))
  ).protocols(httpProtocol)


}