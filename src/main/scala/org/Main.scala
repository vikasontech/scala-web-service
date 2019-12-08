package org

import java.util.concurrent.TimeUnit

import akka.actor
import akka.actor.Status.Success
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration.{Duration, DurationLong}
import scala.util.Try

object Main extends App {

  import akka.pattern.{ask, Patterns, pipe}

  private val system: ActorSystem = ActorSystem("sample")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(5 seconds)
  private val userDataActorRef: ActorRef = system.actorOf(actor.Props(new UserDataActor()))
  //  private val userActivityActorRef: ActorRef =


  private val eventualData: Future[UserData] = ask(userDataActorRef, UserDataActor.Get).mapTo[UserData]

  eventualData.collect{
    case x=>
      getActivityList(x.data)
    case _ => println("unknown")
  }

  def getActivityList( userId: String) {
    val repository = new UserActivityRepositoryImpl()
    val ref = system.actorOf(actor.Props(new UserActivityActor(userId, repository)))

    val future = (ref ? UserActivityActor.Get).mapTo[List[UserActivity]]

    future.collect {
      case x =>
        println(s"User Data: ${x}")
        println(s"activity data: ${x}")
      case _ =>
        println(s"no activity found!")
    }.onComplete(x =>
      system.terminate())
  }
}

