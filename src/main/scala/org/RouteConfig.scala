package org

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{delete, get, path, post, put, _}
import akka.http.scaladsl.server.directives.{PathDirectives, RouteDirectives}
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.pattern.Patterns
import org.db.doc.Employee
import org.user.actor.{EmployeeActor, SEARCH_ALL}
//import org.db.{Employee, EmployeeActor}
//import org.service.{EmployeeActor, SEARCH_ALL}
import org.user.actor.{UserActivityActor, UserDataActor}
import org.user.data.{UserActivity, UserData}
import org.user.repositories.UserActivityRepositoryImpl
import org.utils.{JsonUtils, TimeUtils}
import spray.json.JsValue

import scala.concurrent.Await


class RouteConfig(implicit val userDataActorRef: ActorRef,
                  implicit val system: ActorSystem) {


  val getRoute: Route =

    PathDirectives.pathPrefix("user") {
      concat(
        path("activity") {
          get {

            val userData = findData(UserDataActor.Get)

            val userActivityActorRef: ActorRef =
              system.actorOf(Props(new UserActivityActor(userData.data, new UserActivityRepositoryImpl())))

            val activity: UserActivity = findUserActivityData(userActivityActorRef)
            RouteDirectives.complete(HttpEntity(activity.toString))
          }
        },
        path("find") {
          get {
            val tryAkkaActorRef = system.actorOf(Props(new EmployeeActor()))
            val returnValue = Patterns.ask(tryAkkaActorRef, SEARCH_ALL, TimeUtils.timeoutMills)
            val result: Seq[Employee] = Await.result(returnValue, TimeUtils.atMostDuration).asInstanceOf[Seq[Employee]]
            val json: JsValue = JsonUtils.getJsonValue(result)
            RouteDirectives.complete(HttpEntity(json.toString))
          }
        }
      )
    }

  private def findUserActivityData(userActivityActorRef: ActorRef): UserActivity = {
    val resultFuture = Patterns.ask(userActivityActorRef, UserActivityActor.Get, TimeUtils.timeoutMills)
    val result: List[UserActivity] = Await.result(resultFuture, TimeUtils.atMostDuration).asInstanceOf[List[UserActivity]]
    val activity: UserActivity = result.head
    activity
  }

  val postRoute: Route = path("user") {
    post {
      //TODO: DO SOME OPERATION TO SAVE USER DATA
      executeActorAndSearchData(UserDataActor.Post)
    }
  }

  val deleteRoute: Route = path("user") {
    delete {
      //TODO: DO SOME OPERATION TO DELETE USER DATA
      executeActorAndSearchData(UserDataActor.Delete)
    }
  }

  val putRoute: Route = path("user") {
    put {
      //TODO: DO SOME OPERATION TO UPDATE USER DATA
      executeActorAndSearchData(UserDataActor.Put)
    }
  }

  val executeActorAndSearchData: Any => StandardRoute = (message: Any) => {
    val result: UserData = findData(message)
    RouteDirectives.complete(HttpEntity(result.data))
  }

  private def findData(message: Any) = {
    val resultFuture = Patterns.ask(userDataActorRef, message, timeoutMillis = TimeUtils.timeoutMills)
    val result: UserData = Await.result(resultFuture, TimeUtils.atMostDuration).asInstanceOf[UserData]
    result
  }
}
