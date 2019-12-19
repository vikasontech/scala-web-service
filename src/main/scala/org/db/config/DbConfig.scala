package org.db.config

import ch.rasc.bsoncodec.math.BigDecimalStringCodec
import ch.rasc.bsoncodec.time.LocalDateTimeDateCodec
import com.mongodb.MongoCredential.createCredential
import com.mongodb.{MongoCredential, ServerAddress}
import org.bson.codecs.configuration.CodecRegistry
import org.db.data.Employee
import org.mongodb.scala.MongoClientSettings

import scala.collection.JavaConverters.seqAsJavaListConverter

object DbConfig {

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
