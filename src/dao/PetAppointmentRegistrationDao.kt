package hu.pappbence.dao

import hu.pappbence.dto.RegistrationDto
import hu.pappbence.model.PetAppointmentRegistrations
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PetAppointmentRegistrationDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PetAppointmentRegistrationDao>(PetAppointmentRegistrations)
    var pet by PetDao referencedOn PetAppointmentRegistrations.petId
    var appointment by AppointmentTypeDao referencedOn PetAppointmentRegistrations.appointmentId
    var date by PetAppointmentRegistrations.date

    fun toAppointmentRegistrationDto(): RegistrationDto = RegistrationDto(id.value, pet.id.value, appointment.id.value, date)
}