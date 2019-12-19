package org

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{get, path, post, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.{PathDirectives, RouteDirectives}
import akka.pattern.Patterns
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.db.data.Employee
import org.domain.EmployeeRequest
import org.user.actor._
import org.utils.{JsonUtils, TimeUtils}
import spray.json.enrichAny

import scala.concurrent.Await


class EmployeeRouteConfig(implicit val system: ActorSystem) extends JsonUtils {
  val employeeActor: ActorRef = system.actorOf(Props(new EmployeeActor()))

  implicit val mat: ActorMaterializer = ActorMaterializer()


  val getRoute: Route =

    PathDirectives.pathPrefix("user") {
      concat(
        path("create") {
          post {
            entity(as[EmployeeRequest]) { employee =>
              val future = Patterns.ask(employeeActor, SAVE(employee), TimeUtils.timeoutMills)
              Await.result(future, TimeUtils.atMostDuration)
              RouteDirectives.complete(HttpEntity("Data saved successfully!"))
            }
          }
        },

        path("search") {
          get {
            val resultFuture = Patterns.ask(employeeActor, SEARCH_ALL, TimeUtils.timeoutMills)
            val resultSource = Await.result(resultFuture, TimeUtils.atMostDuration).asInstanceOf[Source[Employee, NotUsed]]
            val resultByteString = resultSource.map { it => ByteString.apply(it.toJson.toString.getBytes()) }
            RouteDirectives.complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, resultByteString))
          }
        },

        path("update") {
          put {
            parameter("id") { id =>
              entity(as[EmployeeRequest]) { employee =>
                val future = Patterns.ask(employeeActor, UPDATE(employee, id), TimeUtils.timeoutMills)
                Await.result(future, TimeUtils.atMostDuration)
                RouteDirectives.complete(HttpEntity("Data updated saved successfully!"))
              }
            }
          }
        },

        path("delete") {
          delete {
            parameter("id") { id =>
              val resultFuture = Patterns.ask(employeeActor, DELETE(id), TimeUtils.timeoutMills)
              Await.result(resultFuture, TimeUtils.atMostDuration)
              RouteDirectives.complete(HttpEntity(s"Data updated saved successfully!"))
            }
          }
        }
      )
    }
}
