package org.db.config

import org.bson.{BsonDocument, Document}
import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import org.db.doc.Employee
import org.mongodb.scala.Completed
import org.mongodb.scala.model.Filters
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.Future

object EmployeeRepo{


  def delEmploy(): Unit = {
    DbConfig.employees.drop().subscribe((result: Completed) => println(s"$result"),
      (e: Throwable) => println(e.getLocalizedMessage),
      () => println("completed!"))
  }

  def createCollection(): Unit = {
    DbConfig.database.createCollection("employee").subscribe(
      (result: Completed) => println(s"$result"),
      (e: Throwable) => println(e.getLocalizedMessage),
      () => println("complete"))
  }

  def insertData(emp: Employee): Future[Completed] = {
    DbConfig.employees.insertOne(emp).toFuture()
  }

  def findAll(): Future[Seq[Employee]] = {
    DbConfig.employees.find().toFuture()
  }

  def update(emp: Employee):Future[UpdateResult] ={
    DbConfig.database.getCollection("").findOneAndUpdate(filter =  )
//    DbConfig.employees.findOneAndUpdate(Filters.eq("name","vikas"), emp)
//    DbConfig.employees.replaceOne(Filters.eq("name", emp.name), emp).toFuture()
  }

}
