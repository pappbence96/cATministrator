package hu.pappbence.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object Pets : IntIdTable() {
    val name = varchar("name", 50)
    val age = integer("age")
    val species = varchar("species", 20)
    val added = datetime("added")
    val owner = reference("owner", PetOwners).references(PetOwners.id)
}