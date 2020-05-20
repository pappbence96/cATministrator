package hu.pappbence.services.appointments

import hu.pappbence.dto.AppointmentDto
import hu.pappbence.dto.RegistrationDto
import hu.pappbence.dto.RegistrationResultDto
import org.joda.time.DateTime

interface AppointmentsService {
    fun listAppointments() : List<AppointmentDto>
    fun findAppointmentById(id: Int) : AppointmentDto
    fun listRegistrations() : List<RegistrationDto>
    fun registerPetForAppointment(petId: Int, appointmentId: Int, date: DateTime) : RegistrationResultDto
    fun listRegistrationsOfPet(petId: Int) : List<RegistrationDto>
    fun listRegistrationsOfOwner(ownerId: Int) : List<RegistrationDto>
}