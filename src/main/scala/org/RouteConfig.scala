package org

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{delete, get, path, post, put}
import akka.http.scaladsl.server.directives.RouteDirectives
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.pattern.Patterns
import org.user.actor.UserDataActor
import org.user.data.UserData

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class RouteConfig(implicit val userDataActorRef: ActorRef) {
  val timeoutMills: Long = 2 * 1000

   val getRoute: Route = path("user") {
    get { executeActorAndSearchData (UserDataActor.Get)}
  }

   val postRoute: Route = path("user") {
    post { executeActorAndSearchData (UserDataActor.Post) }
  }

   val deleteRoute: Route = path("user") {
    delete { executeActorAndSearchData (UserDataActor.Delete) }
  }

   val putRoute: Route = path("user") {
    put { executeActorAndSearchData (UserDataActor.Put) }
  }

  val executeActorAndSearchData: Any => StandardRoute = (message:Any ) => {
    val resultFuture =  Patterns.ask(userDataActorRef, message, timeoutMillis = timeoutMills)
    val result: UserData = Await.result(resultFuture, Duration.create(2, TimeUnit.SECONDS)).asInstanceOf[UserData]
    RouteDirectives.complete(HttpEntity(result.data))
  }
}