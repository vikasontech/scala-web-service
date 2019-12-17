package org

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{get, path, post, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.{PathDirectives, RouteDirectives}
import akka.pattern.Patterns
import akka.stream.ActorMaterializer
import org.db.doc.Employee
import org.domain.EmployeeRequest
import org.user.actor.{EmployeeActor, SAVE, SEARCH_ALL}
import spray.json.DefaultJsonProtocol
//import org.db.{Employee, EmployeeActor}
//import org.service.{EmployeeActor, SEARCH_ALL}
//import org.user.actor.{UserActivityActor, UserDataActor}
//import org.user.data.{UserActivity, UserData}
//import org.user.repositories.UserActivityRepositoryImpl
import org.utils.{JsonUtils, TimeUtils}
import spray.json.JsValue

import scala.concurrent.Await


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import org.utils.JsonUtils._
  implicit val employeeFormat = jsonFormat3(Employee)
  implicit val employeeRequestFormat = jsonFormat2(EmployeeRequest)
}

class RouteConfig(implicit val userDataActorRef: ActorRef,
                  implicit val system: ActorSystem) extends JsonSupport{
  val employeeActor = system.actorOf(Props(new EmployeeActor()))
//  implicit val employeef = DefaultJsonProtocol.jsonFormat2(Employee)

  implicit val mat = ActorMaterializer()

  val getRoute: Route =

    PathDirectives.pathPrefix("user") {
      concat(
        path("find") {
          get {
            val returnValue = Patterns.ask(employeeActor, SEARCH_ALL, TimeUtils.timeoutMills)
            val result: Seq[Employee] = Await.result(returnValue, TimeUtils.atMostDuration).asInstanceOf[Seq[Employee]]
            val json: JsValue = JsonUtils.getJsonValue(result)
            RouteDirectives.complete(HttpEntity(json.toString))
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
        }
      )

    }
}
