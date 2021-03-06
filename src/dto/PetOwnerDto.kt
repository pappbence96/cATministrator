package hu.pappbence.dto

import org.joda.time.DateTime

class PetOwnerDto (
    val id : Int = 0,
    val name: String = "",
    val phone: String = "",
    val balance: Int = 0,
    val registration: DateTime = DateTime.now()
)