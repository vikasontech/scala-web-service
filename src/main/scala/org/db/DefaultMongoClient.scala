package org.db

import com.mongodb.{MongoCredential, ServerAddress}
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCollection, MongoDatabase}

import scala.jdk.CollectionConverters._
import com.mongodb.MongoCredential._

object DefaultMongoClient {

  val user: String = "root"
  val password: Array[Char] = "example".toCharArray
  val source: String = "admin"
  private val credential: MongoCredential = createCredential(user, source, password)

  val settings: MongoClientSettings = MongoClientSettings.builder()
    .applyToClusterSettings(b =>
      b.hosts(List(new ServerAddress("localhost")).asJava))
    .credential(credential)
    .build()

  val client: MongoClient = MongoClient(settings)

}

object DefaultDatabaseOperations {
  val client:MongoClient = DefaultMongoClient.client
  private val db: MongoDatabase = client.getDatabase("test")
  MongoCollection[]



}