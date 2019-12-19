package org.db.config

import org.db.doc.Employee
import org.mongodb.scala.Completed
import org.utils.JsonUtils

import scala.concurrent.Future

object EmployeeRepo extends JsonUtils {


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

  def update(emp: Employee):Future[Employee] = {
    import org.mongodb.scala.model.Filters._
    import org.mongodb.scala.model.Updates._
    import org.mongodb.scala.model._

    DbConfig.employees
      .findOneAndUpdate(equal("_id", "bc39e364-21f9-42a1-9ac5-d081f6a40ba0"),
        set("name","mamta"), FindOneAndUpdateOptions().upsert(true)).toFuture()
  }
}
