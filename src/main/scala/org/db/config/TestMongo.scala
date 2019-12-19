package org.db.config

import org.db.doc.Employee
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.FindOneAndUpdateOptions
import org.mongodb.scala.model.Updates._
import org.utils.JsonUtils

import scala.concurrent.Await
import scala.concurrent.duration.{DurationInt, FiniteDuration}


object TestMongo extends JsonUtils with App {
  val timeout: FiniteDuration = 1.minutes
  val keyField = "_id"
  val idValue = "bc39e364-21f9-42a1-9ac5-d081f6a40ba0"
  val valueField = "name"
  private val employees: MongoCollection[Employee] = DbConfig.employees
  println(Await.result(employees.findOneAndUpdate(equal(keyField, idValue), combine(set(valueField, "vedanta"),set(valueField, "vedanta")),
    FindOneAndUpdateOptions().upsert(true))
    .toFuture(), timeout))
}
