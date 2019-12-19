package org.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import org.db.config.EmployeeRepo
import org.db.doc.Employee
import org.domain.EmployeeRequest

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class EmployeeService {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val saveEmployeeData: EmployeeRequest => Unit = (employeeRequest: EmployeeRequest) => {
    val employeeDoc:Employee = employeeMapperWithNewID(employeeRequest)

    EmployeeRepo.insertData(employeeDoc)
      .onComplete {
        case Failure(exception) => println(exception.getLocalizedMessage)
        case Success(_) => Done
      }
  }

  private def employeeMapperWithNewID(employee: EmployeeRequest) = {
    Employee(name = employee.name, dateOfBirth = LocalDate.parse(employee.dateOfBirth, DateTimeFormatter.ISO_DATE),
      _id = UUID.randomUUID.toString)
  }

  def findAll: Source[Employee, NotUsed] = {
    Source.fromFuture(EmployeeRepo.findAll())
      .mapConcat {
        identity
      }
  }

  def update(employeeRequest:EmployeeRequest, id: String): Unit = {
    val employeeDoc:Employee = employeeMapperWithNewID(employeeRequest)
    val future: Future[Employee] = EmployeeRepo.update(emp = employeeDoc, id)
    val result: Employee = Await.result(future, 2.seconds)
    println(s"Number of record matched: ${result}")
  }
}
