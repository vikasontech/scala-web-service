package org

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props, TypedActor}
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{delete, get, path, post, put}
import akka.http.scaladsl.server.directives.RouteDirectives
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.pattern.Patterns
import org.user.actor.{UserActivityActor, UserDataActor}
import org.user.data.{UserActivity, UserData}
import org.user.repositories.{UserActivityRepositoryImpl}

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class RouteConfig(implicit val userDataActorRef: ActorRef,
                  implicit val system: ActorSystem) {
  val timeoutMills: Long = 2 * 1000


  val getRoute: Route = path("user") {
    get {
      val userData = findData(UserDataActor.Get)

      val userActivityActorRef: ActorRef =
        system.actorOf(Props(new UserActivityActor(userData.data, new UserActivityRepositoryImpl())))

      val activity: UserActivity = findUserActivityData(userActivityActorRef)
      RouteDirectives.complete(HttpEntity(activity.toString))
    }
  }

  private def findUserActivityData(userActivityActorRef: ActorRef) = {
    val resultFuture = Patterns.ask(userActivityActorRef, UserActivityActor.Get, timeoutMills)
    val result: List[UserActivity] = Await.result(resultFuture, Duration.create(2, TimeUnit.SECONDS)).asInstanceOf[List[UserActivity]]
    val activity: UserActivity = result.head
    activity
  }

  val postRoute: Route = path("user") {
    post {
      executeActorAndSearchData(UserDataActor.Post)
    }
  }

  val deleteRoute: Route = path("user") {
    delete {
      executeActorAndSearchData(UserDataActor.Delete)
    }
  }

  val putRoute: Route = path("user") {
    put {
      executeActorAndSearchData(UserDataActor.Put)
    }
  }

  val executeActorAndSearchData: Any => StandardRoute = (message: Any) => {
    val result: UserData = findData(message)
    RouteDirectives.complete(HttpEntity(result.data))
  }

  private def findData(message: Any) = {
    val resultFuture = Patterns.ask(userDataActorRef, message, timeoutMillis = timeoutMills)
    val result: UserData = Await.result(resultFuture, Duration.create(2, TimeUnit.SECONDS)).asInstanceOf[UserData]
    result
  }
}