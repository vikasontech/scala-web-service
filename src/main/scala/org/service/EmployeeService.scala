package org.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.db.config.EmployeeRepo
import org.db.doc.Employee
import akka.stream.scaladsl.{Sink, Source}
import org.domain.EmployeeRequest

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class EmployeeService {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val saveEmployeeData: EmployeeRequest => Unit = (employee: EmployeeRequest) => {
    val employeeDoc:Employee = Employee(name = employee.name, dateOfBirth = LocalDate.parse(employee.dateOfBirth, DateTimeFormatter.ISO_DATE),
      id = UUID.randomUUID.toString)

    EmployeeRepo.insertData(employeeDoc)
      .onComplete {
        case Failure(exception) => println(exception.getLocalizedMessage)
        case Success(_) => None
      }
  }



  def findAll: Seq[Employee] = {
    val value: Future[Seq[Employee]] = Source.future(EmployeeRepo.findAll())
      .mapConcat {
        identity
      }
      .runWith(Sink.seq[Employee])
    val value1 = Await.result(value, Duration.create(10, TimeUnit.SECONDS))
    value1
  }
}
