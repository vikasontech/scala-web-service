package org.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.db.data.Employee
import org.domain.EmployeeRequest
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat}
trait JsonUtils extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object dateFormatter extends JsonFormat[LocalDate] {
    override def write(obj: LocalDate): JsValue = {
      JsString(obj.toString)
    }

    override def read(json: JsValue): LocalDate = {
      LocalDate.parse(json.toString(), DateTimeFormatter.ISO_DATE)
    }
  }
  implicit val employeeJsonFormatter: RootJsonFormat[Employee] = DefaultJsonProtocol.jsonFormat3(Employee)
  implicit val employeeRequestFormat: RootJsonFormat[EmployeeRequest] = jsonFormat2(EmployeeRequest)
}
