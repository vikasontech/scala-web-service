package org.db.doc

import java.lang.annotation.Documented
import java.time.LocalDate
import java.util.UUID
@Documented
case class Employee(id: String, name: String, dateOfBirth: LocalDate)
