package org.db

import java.time.LocalDate
import java.util.concurrent.TimeUnit

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.stream.ActorMaterializer
//import akka.stream.scaladsl.GraphDSL.Implicits.flow2flow
import akka.stream.scaladsl.{Keep, Sink, Source}
//import com.sun.org.apache.xalan.internal.res.XSLTErrorResources

//import scala.collection.{Factory, mutable}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

sealed trait EmployeeActorMessage

case class SAVE(emp: Employee) extends EmployeeActorMessage

case object SEARCH_ALL extends EmployeeActorMessage

class EmployeeActor extends Actor with ActorLogging {
  private val employeeService = new EmployeeService()

  override def receive: Receive = {

    case SAVE(employee: Employee) =>
      log.info(s"received message Save with employee $employee")
      sender ! employeeService.saveEmployeeData(employee)

    case SEARCH_ALL =>
      log.info(s"received message find all")
      sender() ! employeeService.findAll

    case _ =>
      log.debug("Unhandled message!")
  }
}

class EmployeeService {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val saveEmployeeData: Employee => Unit = (emp: Employee) => EmployeeRepo.insertData(emp)
    .onComplete {
      case Failure(exception) => println(exception.getLocalizedMessage)
      case Success(_) => None
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
