package org.user.repositories

import akka.actor.ActorSystem
import org.user.data.UserActivity

import scala.concurrent.{ExecutionContextExecutor, Future}

class UserActivityRepositoryImpl(implicit system: ActorSystem) extends UserActivityRepository {

  override def queryHistoricalActivities(userId: String): Future[List[UserActivity]] = {
    implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
    Future(List(UserActivity("login")))
  }
}
