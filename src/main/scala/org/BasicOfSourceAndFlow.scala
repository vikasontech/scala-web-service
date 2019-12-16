package org

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}
import akka.util.ccompat.Factory
import ch.rasc.bsoncodec.time.DurationInt64Codec

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object BasicOfSourceAndFlow extends App {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mat = ActorMaterializer()


  val source = Source(1 to 3)

  val sink = Sink.fold[Int, Int](0)(_ + _)

//  // connect the Source to the Sink, obtaining a RunnableGraph
//  val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)
//
//  // materialize the flow and get the value of the FoldSink
//  val sum: Future[Int] = runnable.run()

  private val future: Future[Seq[Int]] = source.runWith(Sink.seq[Int])
  println(Await.result(future, Duration.Inf))

  val s2 = Source(List(Dummy("vikas"), Dummy("mamta")))
  private val future1: Future[Seq[Dummy]] = s2.runWith(Sink.seq)
  val seq: Seq[Dummy] = Await.result(future1, Duration.Inf)
  println(seq.iterator.toList)




  actorSystem.terminate()

}

case class Dummy (name: String)