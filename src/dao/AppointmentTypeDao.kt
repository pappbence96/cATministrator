package hu.pappbence.dao

import hu.pappbence.dto.AppointmentDto
import hu.pappbence.model.AppointmentTypes
import hu.pappbence.model.PetAppointmentRegistrations
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AppointmentTypeDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AppointmentTypeDao>(AppointmentTypes)
    var name by AppointmentTypes.name
    var fee by AppointmentTypes.fee
    val registrations by PetAppointmentRegistrationDao referrersOn PetAppointmentRegistrations.appointmentId


    fun toAppointmentDto() = AppointmentDto(id.value, name, fee)
}