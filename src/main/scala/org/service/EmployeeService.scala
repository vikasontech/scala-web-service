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
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
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
        case Success(_) => None
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

  def update(employeeRequest:EmployeeRequest): Unit = {
    val employeeDoc:Employee = employeeMapperWithNewID(employeeRequest)

    val future = EmployeeRepo.update(emp = employeeDoc)
    val result: UpdateResult = Await.result(future, Duration.Inf)
    println(s"Number of record matched: ${result.getMatchedCount}")
    println(s"Number of record updated: ${result.getModifiedCount}")
//      .onComplete {
//        case Failure(exception) => println(exception.getLocalizedMessage)
//        case Success(v)=> println(s"data saved with message $v")
//      }
  }
}
