package org.user.actor

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import akka.actor.{Actor, ActorLogging}
import org.db.doc.Employee
import org.domain.EmployeeRequest
import org.service.EmployeeService


class EmployeeActor extends Actor with ActorLogging {
  private val employeeService = new EmployeeService()

  override def receive: Receive = {

    case SAVE(employee: EmployeeRequest) =>
      log.info(s"received message Save with employee $employee")

      sender ! employeeService.saveEmployeeData(employee)

    case SEARCH_ALL =>
      log.info(s"received message find all")
      sender() ! employeeService.findAll

    case _ =>
      log.debug("Unhandled message!")
  }
}

sealed trait EmployeeActorMessage

case class SAVE(emp: EmployeeRequest) extends EmployeeActorMessage

case object SEARCH_ALL extends EmployeeActorMessage