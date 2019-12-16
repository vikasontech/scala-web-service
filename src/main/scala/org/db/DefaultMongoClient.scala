// ref: https://www.jannikarndt.de/blog/2017/08/writing_case_classes_to_mongodb_in_scala/

package org.db

import java.time.LocalDate

import ch.rasc.bsoncodec.math.BigDecimalStringCodec
import ch.rasc.bsoncodec.time.LocalDateTimeDateCodec
import com.mongodb.MongoCredential._
import com.mongodb.{MongoCredential, ServerAddress}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{Completed, MongoClientSettings, Observer}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._
import scala.language.postfixOps

object DB {

  val user: String = "root"
  val password: Array[Char] = "example".toCharArray
  val source: String = "admin"
  private val credential: MongoCredential = createCredential(user, source, password)

  import org.bson.codecs.configuration.CodecRegistries
  import org.bson.codecs.configuration.CodecRegistries._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}


  private val javaCodecs = CodecRegistries.fromCodecs(
    new LocalDateTimeDateCodec(),
    new LocalDateTimeDateCodec(),
    new BigDecimalStringCodec())

  private val registry: CodecRegistry = CodecRegistries.fromProviders(classOf[Employee])

  val settings: MongoClientSettings = MongoClientSettings.builder()
    .applyToClusterSettings(b => b.hosts(List(new ServerAddress("localhost")).asJava))
    .credential(credential)
    .codecRegistry(fromRegistries(registry, javaCodecs, DEFAULT_CODEC_REGISTRY))
    .build()

  val client: MongoClient = MongoClient(settings)

  val database: MongoDatabase = client.getDatabase("test")

  val employees: MongoCollection[Employee] = database.getCollection("employee")

}

object TestDb extends App {
  createCollection()
  insertData()
  findAll()

  def delEmploy(): Unit = {
    DB.employees.drop().subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = print("done")

      override def onError(e: Throwable): Unit = println(e)

      override def onComplete(): Unit = println("complete")
    })
  }

  def createCollection(): Unit = {
    DB.database.createCollection("employee").subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("done")

      override def onError(e: Throwable): Unit = println(e)

      override def onComplete(): Unit = println("complete")
    })
  }

  def insertData(): Completed = {
    val eventualCompleted = DB.employees.insertOne(new Employee(name = "Jai Shri Ram", LocalDate.of(2019, 12, 12))).toFuture()
    Await.result(eventualCompleted, 10.second)
  }


  def insertDataV2(emp: Employee): Future[Completed] = {
     DB.employees.insertOne(emp).toFuture()
  }

  def findAll(): Future[Seq[Employee]] = {
    DB.employees.find().toFuture()
  }
}
