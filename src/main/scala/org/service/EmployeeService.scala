package org.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.db.config.EmployeeRepo
import org.db.doc.Employee
import org.domain.EmployeeRequest

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

class EmployeeService {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val saveEmployeeData: EmployeeRequest => Unit = (employee: EmployeeRequest) => {
    val employeeDoc:Employee = Employee(name = employee.name, dateOfBirth = LocalDate.parse(employee.dateOfBirth, DateTimeFormatter.ISO_DATE),
      _id = UUID.randomUUID.toString)

    EmployeeRepo.insertData(employeeDoc)
      .onComplete {
        case Failure(exception) => println(exception.getLocalizedMessage)
        case Success(_) => None
      }
  }

  def findAll: Source[Employee, NotUsed] = {
    Source.fromFuture(EmployeeRepo.findAll())
      .mapConcat {
        identity
      }
  }
}
