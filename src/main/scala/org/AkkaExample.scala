package org

import java.awt.SequencedEvent

import akka.actor.{Actor, ActorRef}
import akka.util.Timeout

import scala.concurrent.duration.DurationLong
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object UserProxyActor {

  sealed trait Message

  case object GetUserData extends Message

  case object GetUserActivities extends Message

}
class UserProxyActor(userData: ActorRef,
                     userActivity: ActorRef)
extends Actor {
  import UserProxyActor._
  import akka.pattern.{ask, pipe}

  implicit val timeout: Timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case GetUserData =>
      (userData ? UserDataActor.Get).pipeTo(sender)

    case GetUserActivities =>
      (userActivity ? UserActivityActor.Get)
        .pipeTo(sender())
  }
}

case class UserData(data: String)

case class UserActivity(activity: String)

//Backend Service
class UserDataActor extends Actor {

  import UserDataActor._

  //holds the user data internally
  val internalData: UserData =
    UserData("Internal Data")

  override def receive: Receive = {
    case Get =>
      sender() ! internalData
  }
}

object UserDataActor {

  case object Get

}

trait UserActivityRepository {
  def queryHistoricalActivities(userId: String):
  Future[List[UserActivity]]
}
class UserActivityRepositoryImpl extends UserActivityRepository {
  override def queryHistoricalActivities(userId: String): Future[List[UserActivity]] = {
    Future(List(UserActivity("login")))
  }
}

class UserActivityActor(val userId: String,
                        val repository: UserActivityRepository)
  extends Actor {

  import akka.pattern.pipe
  import org.UserActivityActor._

  implicit val ec: ExecutionContextExecutor = context.dispatcher


  override def receive: Receive = {
    case Get =>
      // user's historical activities are retrieved
      // via the separate repository
      repository.queryHistoricalActivities(userId) pipeTo sender
  }
}

object UserActivityActor {

  case object Get

}

