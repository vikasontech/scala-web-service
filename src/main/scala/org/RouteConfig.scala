package org

import java.time
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props, TypedActor}
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{delete, get, path, post, put}
import akka.http.scaladsl.server.directives.{PathDirectives, RouteDirectives}
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.pattern.Patterns
import org.db.{Employee, FindAll, TryAkkaActor, TryAkkaStreams}
import org.user.actor.{UserActivityActor, UserDataActor}
import org.user.data.{UserActivity, UserData}
import org.user.repositories.UserActivityRepositoryImpl

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.http.scaladsl.server.Directives._
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, enrichAny}
import spray.json.DefaultJsonProtocol._

import scala.util.parsing.json.JSONFormat



class RouteConfig(implicit val userDataActorRef: ActorRef,
                  implicit val system: ActorSystem) {
  val timeoutMills: Long = 2 * 1000

  implicit object dateFormatter extends JsonFormat[LocalDate] {
    override def write(obj: LocalDate): JsValue = {
        JsString(obj.toString)
    }

    override def read(json: JsValue): LocalDate = {
      LocalDate.parse(json.toString(), DateTimeFormatter.ISO_DATE)
    }
  }
  val getRoute: Route =


    PathDirectives.pathPrefix("user"){
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
            val tryAkkaActorRef = system.actorOf(Props(new TryAkkaActor()))
            val returnValue = Patterns.ask(tryAkkaActorRef, FindAll, 10 * 1000)

            val result: Seq[Employee] = Await.result(returnValue, Duration.create(5, TimeUnit.SECONDS)).asInstanceOf[Seq[Employee]]
            implicit val formatr = DefaultJsonProtocol.jsonFormat2(Employee)
            val json: JsValue = result.toJson
            println(json.toString())
            RouteDirectives.complete(HttpEntity(json.toString))
          }
        }
      )
    }





  private def findUserActivityData(userActivityActorRef: ActorRef) = {
    val resultFuture = Patterns.ask(userActivityActorRef, UserActivityActor.Get, timeoutMills)
    val result: List[UserActivity] = Await.result(resultFuture, Duration.create(2, TimeUnit.SECONDS)).asInstanceOf[List[UserActivity]]
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
    val resultFuture = Patterns.ask(userDataActorRef, message, timeoutMillis = timeoutMills)
    val result: UserData = Await.result(resultFuture, Duration.create(2, TimeUnit.SECONDS)).asInstanceOf[UserData]
    result
  }
}