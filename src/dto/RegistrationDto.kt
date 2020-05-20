package hu.pappbence.dto

import org.joda.time.DateTime

class RegistrationDto(
    val id: Int = 0,
    val petId: Int = 0,
    val appointmentId: Int = 0,
    val date: DateTime = DateTime.now()
)