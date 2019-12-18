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
import org.db.doc.Employee
import org.domain.EmployeeRequest
import org.user.actor.{EmployeeActor, SAVE, SEARCH_ALL, UPDATE}
import spray.json.enrichAny
import org.utils.{JsonUtils, TimeUtils}

import scala.concurrent.Await


class RouteConfig(implicit val userDataActorRef: ActorRef,
                  implicit val system: ActorSystem) extends JsonUtils{
  val employeeActor: ActorRef = system.actorOf(Props(new EmployeeActor()))

  implicit val mat: ActorMaterializer = ActorMaterializer()


  val getRoute: Route =

    PathDirectives.pathPrefix("user") {
      concat(
        path("find") {
          get {
            val returnValue = Patterns.ask(employeeActor, SEARCH_ALL, TimeUtils.timeoutMills)
            val resultFuture = Await.result(returnValue, TimeUtils.atMostDuration).asInstanceOf[Source[Employee, NotUsed]]
            val result = resultFuture.map { it => ByteString.apply(it.toJson.toString().getBytes())}
            RouteDirectives.complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, result))
          }
        },
        path("save") {
          post {
            entity(as[EmployeeRequest]) { employee =>
              val future = Patterns.ask(employeeActor, SAVE(employee), TimeUtils.timeoutMills)
              Await.result(future, TimeUtils.atMostDuration)
              RouteDirectives.complete(HttpEntity("Data saved successfully!"))
            }
          }
        },
        path("update") {
          put {
            entity(as[EmployeeRequest]) { employee =>
              val future = Patterns.ask(employeeActor, UPDATE(employee), TimeUtils.timeoutMills)
              Await.result(future, TimeUtils.atMostDuration)
              RouteDirectives.complete(HttpEntity("Data updated saved successfully!"))
            }
          }
        }
      )
    }
}

