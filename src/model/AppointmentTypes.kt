package hu.pappbence.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object AppointmentTypes : IntIdTable() {
    val name = varchar("name", 50)
    val fee = integer("fee")
}