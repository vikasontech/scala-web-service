package org.user.actor

import akka.actor.Actor
import akka.pattern.pipe
import org.user.actor.UserActivityActor.Get
import org.user.repositories.UserActivityRepository

import scala.concurrent.ExecutionContextExecutor

object UserActivityActor {
  case object Get
}

class UserActivityActor(val userId: String,
                        implicit val repository: UserActivityRepository)
  extends Actor {

  implicit val ec: ExecutionContextExecutor = context.dispatcher


  override def receive: Receive = {
    case Get =>
      repository.queryHistoricalActivities(userId) pipeTo sender
  }
}