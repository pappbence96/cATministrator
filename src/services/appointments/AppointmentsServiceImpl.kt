package hu.pappbence.services.appointments

import hu.pappbence.dao.AppointmentTypeDao
import hu.pappbence.dao.PetAppointmentRegistrationDao
import hu.pappbence.dao.PetDao
import hu.pappbence.dao.PetOwnerDao
import hu.pappbence.dto.*
import hu.pappbence.model.PetAppointmentRegistrations
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import io.ktor.features.NotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class AppointmentsServiceImpl : AppointmentsService {
    override fun listAppointments(): List<AppointmentDto> {
        return transaction {
            AppointmentTypeDao.all().map { it.toAppointmentDto() }
        }
    }

    override fun findAppointmentById(id: Int): AppointmentDto {
        return transaction {
            AppointmentTypeDao.findById(id)?.toAppointmentDto() ?:
                throw NotFoundException("No appointment type found with id: $id")
        }
    }

    override fun listRegistrations(): List<RegistrationDto> {
        return transaction {
            PetAppointmentRegistrationDao.all().map{ it.toAppointmentRegistrationDto() }
        }
    }

    override fun registerPetForAppointment(petId: Int, appointmentId: Int, date: DateTime): ResourceCreatedDto {
        val registrationId = transaction {
            val pet = PetDao.findById(petId)
                ?: throw NotFoundException("No pet found with id: $petId")
            val appointment = AppointmentTypeDao.findById(appointmentId)
                ?: throw NotFoundException("No appointment type found with id: $appointmentId")

            if(pet.owner.balance < appointment.fee){
                throw IllegalArgumentException("The owner of the specified pet has insufficient funds (has ${pet.owner.balance}, required: ${appointment.fee})")
            }
            pet.owner.balance -= appointment.fee

            PetAppointmentRegistrationDao.new {
                this.pet = pet
                this.appointment = appointment
                this.date = date
            }.id.value
        }

        return ResourceCreatedDto(registrationId)
    }

    override fun listRegistrationsOfPet(petId: Int): List<RegistrationDto> {
        return transaction {
            PetDao.findById(petId)?.registrations?.map { it.toAppointmentRegistrationDto() }
                ?: throw NotFoundException("No pet found with id: $petId")
        }
    }

    override fun listRegistrationsOfOwner(ownerId: Int): List<RegistrationDto> {
        return transaction {
            PetOwnerDao.findById(ownerId)
                ?: throw NotFoundException("No owner found with id: $ownerId")

            // Leaving this as a query instead of DAO access because this will perform better
            (Pets innerJoin PetAppointmentRegistrations)
                .select { Pets.ownerId eq ownerId }
                .map { it.toAppointmentRegistrationDto() }
        }
    }

    private fun ResultRow.toAppointmentRegistrationDto(): RegistrationDto {
        return RegistrationDto(
            this[PetAppointmentRegistrations.id].value,
            this[PetAppointmentRegistrations.petId].value,
            this[PetAppointmentRegistrations.appointmentId].value,
            this[PetAppointmentRegistrations.date]
        )
    }
}