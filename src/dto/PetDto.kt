package hu.pappbence.dto

import org.joda.time.DateTime

class PetDto (
    val id : Int = 0,
    val name: String = "",
    val age: Int = 0,
    val species: String = "",
    val added: DateTime = DateTime.now(),
    val owner: Int = 0
)