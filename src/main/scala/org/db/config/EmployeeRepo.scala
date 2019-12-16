package org.db.config

import org.db.doc.Employee
import org.mongodb.scala.{Completed, Observer}

import scala.concurrent.Future

object EmployeeRepo{

  def delEmploy(): Unit = {
    DbConfig.employees.drop().subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = print("done")

      override def onError(e: Throwable): Unit = println(e)

      override def onComplete(): Unit = println("complete")
    })
  }

  def createCollection(): Unit = {
    DbConfig.database.createCollection("employee").subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("done")

      override def onError(e: Throwable): Unit = println(e)

      override def onComplete(): Unit = println("complete")
    })
  }

  def insertData(emp: Employee): Future[Completed] = {
     DbConfig.employees.insertOne(emp).toFuture()
  }

  def findAll(): Future[Seq[Employee]] = {
    DbConfig.employees.find().toFuture()
  }
}
