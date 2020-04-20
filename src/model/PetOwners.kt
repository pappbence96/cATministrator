package hu.pappbence.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object PetOwners : IntIdTable() {
    val name = varchar("name", 50)
    val phone = varchar("phoneNumber", 20)
    val balance = integer("balance")
    val registration = datetime("registration")
}
