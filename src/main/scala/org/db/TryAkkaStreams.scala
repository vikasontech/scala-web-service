package org.db

import java.time.LocalDate
import java.util.concurrent.TimeUnit

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.GraphDSL.Implicits.flow2flow
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.sun.org.apache.xalan.internal.res.XSLTErrorResources

import scala.collection.{Factory, mutable}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

sealed trait TryActorMessage

case class Save(emp: Employee) extends TryActorMessage

case object FindAll extends TryActorMessage

class TryAkkaActor extends Actor with ActorLogging {
  private val streams = new TryAkkaStreams()

  override def receive: Receive = {
    case Save(employee: Employee) =>
      log.info(s"received message Save with employee $employee")
      sender ! streams.saveEmployeeData(employee)
    case FindAll =>
      log.info(s"received message find all")
      //      sender() ! "hello"
      sender() ! streams.findAllv2
    case _ =>
      log.error("Unhandled message!")
  }
}

class TryAkkaStreams {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val emp = Employee("jai mata", LocalDate.of(2019, 12, 1))

  val saveEmployeeData: Employee => Unit = (emp: Employee) => TestDb.insertDataV2(emp)
    .onComplete {
      case Failure(exception) => println(exception.getLocalizedMessage)
      case Success(value) => println(value)
    }

  val findAll: Future[Done] = {
    Source.future(TestDb.findAll())
      .mapConcat {
        identity
      }
      .runForeach { it => println(s"name: ${it.name} ; and dob: ${it.dateOfBirth}") }
  }

  def findAllv2: Seq[Employee] = {
    val value: Future[Seq[Employee]] = Source.future(TestDb.findAll())
      .mapConcat {
        identity
      }
      .runWith(Sink.seq[Employee])
    val value1 = Await.result(value, Duration.create(10, TimeUnit.SECONDS))
    value1
  }
  //  saveEmployeeData(emp)
  //  findAll.onComplete {
  //    _ => actorSystem.terminate()
  //  }
}
