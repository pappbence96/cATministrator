package hu.pappbence.services.appointments

import hu.pappbence.dto.*
import hu.pappbence.model.AppointmentTypes
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
            AppointmentTypes.selectAll().toList().map { it.toAppointmentDto() }
        }
    }

    override fun findAppointmentById(id: Int): AppointmentDto {
        return transaction {
            AppointmentTypes.selectAll()
                .andWhere { AppointmentTypes.id eq id }
                .firstOrNull()
                ?.toAppointmentDto() ?: throw NotFoundException("No appointment type found with id: $id")
        }
    }

    override fun listRegistrations(): List<RegistrationDto> {
        return transaction {
            PetAppointmentRegistrations.selectAll()
                .map { it.toAppointmentRegistrationDto() }
        }
    }

    override fun registerPetForAppointment(petId: Int, appointmentId: Int, date: DateTime): ResourceCreatedDto {
        val registrationId = transaction {
            val pet = Pets.selectAll()
                .andWhere { Pets.id eq petId }
                .firstOrNull() ?: throw NotFoundException("No pet found with id: $petId")

            val owner = PetOwners.selectAll()
                .andWhere { PetOwners.id eq pet[Pets.ownerId] }
                .first() // Should not be null as that's a DB constraint violation

            val appointment = AppointmentTypes.selectAll()
                .andWhere { AppointmentTypes.id eq appointmentId }
                .firstOrNull() ?: throw NotFoundException("No appointment type found with id: $appointmentId")

            val ownerBalance = owner[PetOwners.balance]
            val appointmentFee = appointment[AppointmentTypes.fee]
            if(ownerBalance < appointmentFee){
                throw IllegalArgumentException("The owner of the specified pet has insufficient funds (has $ownerBalance, required: $appointmentFee)")
            }

            val registrationId = PetAppointmentRegistrations.insert {
                it[PetAppointmentRegistrations.petId] = pet[Pets.id]
                it[PetAppointmentRegistrations.appointmentId] = appointment[AppointmentTypes.id]
                it[PetAppointmentRegistrations.date] = date
            } get PetAppointmentRegistrations.id
            PetOwners.update ({ PetOwners.id eq pet[Pets.ownerId] }) {
                it[balance] = ownerBalance - appointmentFee
            }

            registrationId
        }.value

        return ResourceCreatedDto(registrationId)
    }

    override fun listRegistrationsOfPet(petId: Int): List<RegistrationDto> {
        return transaction {
            Pets.selectAll()
                .andWhere { Pets.id eq petId }
                .firstOrNull() ?: throw NotFoundException("No pet found with id: $petId")

            PetAppointmentRegistrations.selectAll()
                .andWhere { PetAppointmentRegistrations.petId eq petId }
                .map { it.toAppointmentRegistrationDto() }
        }
    }

    override fun listRegistrationsOfOwner(ownerId: Int): List<RegistrationDto> {
        return transaction {
            PetOwners.selectAll()
                .andWhere { PetOwners.id eq ownerId }
                .firstOrNull() ?: throw NotFoundException("No owner found with id: $ownerId")

            (Pets innerJoin PetAppointmentRegistrations)
                .select { Pets.ownerId eq ownerId }
                .map { it.toAppointmentRegistrationDto() }
        }
    }

    // Map from DB entity to DTO
    private fun ResultRow.toAppointmentDto() = AppointmentDto(
        this[AppointmentTypes.id].value,
        this[AppointmentTypes.name],
        this[AppointmentTypes.fee]
    )

    private fun ResultRow.toAppointmentRegistrationDto(): RegistrationDto {
        return RegistrationDto(
            this[PetAppointmentRegistrations.id].value,
            this[PetAppointmentRegistrations.petId].value,
            this[PetAppointmentRegistrations.appointmentId].value,
            this[PetAppointmentRegistrations.date]
        )
    }
}