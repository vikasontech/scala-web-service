package org.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.db.data.Employee
import org.domain.EmployeeRequest
import org.mongodb.scala.Completed
import org.mongodb.scala.result.DeleteResult
import org.user.repositories.EmployeeRepo

import scala.concurrent.{ExecutionContextExecutor, Future}

class EmployeeService {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  def saveEmployeeData: EmployeeRequest => Future[Completed] = (employeeRequest: EmployeeRequest) => {
    val employeeDoc:Employee = employeeMapperWithNewID(employeeRequest)

    EmployeeRepo.insertData(employeeDoc)
  }

  def findAll: Source[Employee, NotUsed] = {
    Source.fromFuture(EmployeeRepo.findAll())
      .mapConcat {
        identity
      }
  }

  def update(employeeRequest:EmployeeRequest, id: String): Future[Employee] = {
    val employeeDoc:Employee = employeeMapperWithNewID(employeeRequest)
    EmployeeRepo.update(emp = employeeDoc, id)
  }

  def delete(id: String): Future[DeleteResult] ={
    EmployeeRepo.delete(id)
  }
  private def employeeMapperWithNewID(employee: EmployeeRequest) = {
    Employee(name = employee.name, dateOfBirth = LocalDate.parse(employee.dateOfBirth, DateTimeFormatter.ISO_DATE),
      _id = UUID.randomUUID.toString)
  }
}
