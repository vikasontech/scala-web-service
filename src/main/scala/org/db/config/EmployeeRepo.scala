package org.db.config

import org.db.doc.Employee
import org.mongodb.scala.Completed
import org.utils.JsonUtils

import scala.concurrent.Future
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._


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

  def update(emp: Employee, id: String):Future[Employee] = {

    DbConfig.employees
      .findOneAndUpdate(equal("_id", id),
        setUpdateJson(emp),
        FindOneAndUpdateOptions().upsert(true)).toFuture()
  }

  private def setUpdateJson(emp:Employee) = {
    combine(
      set("name", emp.name),
      set("dateOfBirth",emp.dateOfBirth)
    )
  }
}
