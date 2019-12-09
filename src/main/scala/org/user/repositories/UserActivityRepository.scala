package org.user.repositories



import org.user.data.UserActivity

import scala.concurrent.Future

trait UserActivityRepository {
  def queryHistoricalActivities(userId: String):
  Future[List[UserActivity]]
}
