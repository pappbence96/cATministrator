package hu.pappbence.dto

import org.joda.time.DateTime

class AppointmentRegistrationDto (
    val date: DateTime = DateTime.now()
)