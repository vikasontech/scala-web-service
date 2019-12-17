package org.db.config

import org.db.doc.Employee
import org.mongodb.scala.{Completed, Observer}

import scala.concurrent.Future

object EmployeeRepo {


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


}
