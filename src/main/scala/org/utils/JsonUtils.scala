package org.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.Emp
import org.db.doc.Employee
import org.domain.EmployeeRequest
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, enrichAny}
trait JsonUtils extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object dateFormatter extends JsonFormat[LocalDate] {
    override def write(obj: LocalDate): JsValue = {
      JsString(obj.toString)
    }

    override def read(json: JsValue): LocalDate = {
      LocalDate.parse(json.toString(), DateTimeFormatter.ISO_DATE)
    }
  }


  implicit val employeJsonFormatter: RootJsonFormat[Employee] = DefaultJsonProtocol.jsonFormat3(Employee)
  implicit val empJsonFormatter: RootJsonFormat[Emp] = DefaultJsonProtocol.jsonFormat1(Emp)
//  implicit val employeeFormat = jsonFormat3(Employee)
  implicit val employeeRequestFormat = jsonFormat2(EmployeeRequest)
//  implicit val empFormat = jsonFormat1(Emp)

   def getJsonValue(result: Seq[Employee]): JsValue = {
    val json: JsValue = result.toJson
    json
  }

}
