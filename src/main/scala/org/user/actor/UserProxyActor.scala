package org.user.actor


import akka.actor.{Actor, ActorRef}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationLong
import scala.language.postfixOps

object UserProxyActor {

  sealed trait Message

  case object GetUserData extends Message

  case object GetUserActivities extends Message

}

class UserProxyActor(userData: ActorRef,
                     userActivity: ActorRef) extends Actor {

  import UserProxyActor._
  import akka.pattern.{ask, pipe}

  implicit val timeout: Timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case GetUserData =>
      (userData ? UserDataActor.Get) pipeTo sender

    case GetUserActivities =>
      (userActivity ? UserActivityActor.Get) pipeTo sender
  }
}
