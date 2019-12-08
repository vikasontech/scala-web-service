package org

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.{path, pathPrefix}
import akka.http.scaladsl.server.directives.RouteDirectives
import akka.pattern.Patterns
import akka.stream.ActorMaterializer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.StdIn

object RouteConfig {

  import akka.http.scaladsl.server.directives.PathDirectives.{pathPrefix, path}
  import akka.http.scaladsl.server.directives.MethodDirectives._

}

object WebServer extends App {
  implicit val system = ActorSystem("web-app")
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val userDataActorRef: ActorRef = system.actorOf(Props(new UserDataActor()))

  val routes = {
    pathPrefix("api") {
      path("hello") {
        get {
          val fut = Patterns.ask(userDataActorRef, UserDataActor.Get, 2 * 1000)
          val result: UserData = Await.result(fut, Duration.create(2, TimeUnit.SECONDS)).asInstanceOf[UserData]
          RouteDirectives.complete(HttpEntity(result.data))
//
        }
      }
    }
  }

  val serverFuture = Http().bindAndHandle(routes, "localhost", 8080)

  println("Server started ....")
  StdIn.readLine()
  serverFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
