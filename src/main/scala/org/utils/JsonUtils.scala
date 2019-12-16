package org.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.db.doc.Employee
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, enrichAny}
import spray.json.DefaultJsonProtocol._
object JsonUtils {

  implicit object dateFormatter extends JsonFormat[LocalDate] {
    override def write(obj: LocalDate): JsValue = {
      JsString(obj.toString)
    }

    override def read(json: JsValue): LocalDate = {
      LocalDate.parse(json.toString(), DateTimeFormatter.ISO_DATE)
    }
  }


  implicit val employeJsonFormatter: RootJsonFormat[Employee] = DefaultJsonProtocol.jsonFormat2(Employee)


   def getJsonValue(result: Seq[Employee]) = {
    val json: JsValue = result.toJson
    json
  }

}
