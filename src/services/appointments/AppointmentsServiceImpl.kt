package hu.pappbence.services.appointments

import hu.pappbence.dto.AppointmentDto
import hu.pappbence.dto.PetDto
import hu.pappbence.dto.RegistrationDto
import hu.pappbence.dto.RegistrationResultDto
import hu.pappbence.model.AppointmentTypes
import hu.pappbence.model.PetAppointmentRegistrations
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import io.ktor.features.NotFoundException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.lang.Exception
import java.util.*

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

    override fun registerPetForAppointment(petId: Int, appointmentId: Int, date: DateTime): RegistrationResultDto {
        val registrationId = transaction {
            val petIdObj = Pets.selectAll()
                .andWhere { Pets.id eq petId }
                .map{ it[Pets.id]}
                .firstOrNull() ?: throw NotFoundException("No pet found with id: $petId")

            val appointmentIdObj = AppointmentTypes.selectAll()
                .andWhere { AppointmentTypes.id eq appointmentId }
                .map{ it[AppointmentTypes.id]}
                .firstOrNull() ?: throw NotFoundException("No appointment type found with id: $appointmentId")

            PetAppointmentRegistrations.insert {
                it[PetAppointmentRegistrations.petId] = petIdObj
                it[PetAppointmentRegistrations.appointmentId] = appointmentIdObj
                it[PetAppointmentRegistrations.date] = date
            } get PetAppointmentRegistrations.id
        }.value

        return RegistrationResultDto(registrationId)
    }

    override fun listRegistrationsOfPet(petId: Int): List<RegistrationDto> {
        return transaction {
            Pets.selectAll()
                .andWhere { Pets.id eq petId }
                .map{ it[Pets.id]}
                .firstOrNull() ?: throw NotFoundException("No pet found with id: $petId")

            PetAppointmentRegistrations.selectAll()
                .andWhere { PetAppointmentRegistrations.petId eq petId }
                .map { it.toAppointmentRegistrationDto() }
        }
    }

    override fun listRegistrationsOfOwner(ownerId: Int): List<RegistrationDto> {
        return transaction {
            PetOwners.selectAll()
                .andWhere { Pets.id eq ownerId }
                .map{ it[Pets.id]}
                .firstOrNull() ?: throw NotFoundException("No owner found with id: $ownerId")

            val petIdsOfOwner = Pets.selectAll()
                .andWhere { Pets.ownerId eq ownerId }
                .map { it[Pets.id].value }

            PetAppointmentRegistrations.selectAll()
                .andWhere { PetAppointmentRegistrations.petId inList petIdsOfOwner }
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