package org.db.config

import org.db.doc.Employee
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, SingleObservable}

import scala.concurrent.Future
import org.mongodb.scala

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

  def update(emp: Employee): SingleObservable[scala.result.UpdateResult] ={
//    DbConfig.employees.updateOne( new BsonDocument("_id", BsonString("39c34f1d-30bb-4e39-a92b-b391ca8f9bdb")), emp)
    DbConfig.employees.replaceOne(Filters.eq("_id", "39c34f1d-30bb-4e39-a92b-b391ca8f9bdb"), emp)
  }

}
