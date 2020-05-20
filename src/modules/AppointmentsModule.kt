package hu.pappbence.modules

import hu.pappbence.dto.*
import hu.pappbence.model.AppointmentTypes
import hu.pappbence.model.PetAppointmentRegistrations
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject
import java.lang.Exception

fun Application.appointmentsModule() {

    routing {
        get("/appointments") {
            call.respond(transaction {
                AppointmentTypes.selectAll().map{ it.toAppointmentDto() }
            })
        }

        get("/appointments/registrations") {
            call.respond(transaction {
                PetAppointmentRegistrations.selectAll().map{ it.toAppointmentRegistrationDto() }
            })
        }

        post("/pets/{petId}/appointments/{appointmentId}"){
            val petId = try {
                call.parameters["petId"]?.toInt() ?: throw IllegalStateException("Missing parameter: petId")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid petId: must be an integer value")
                return@post
            }
            val appointmentId = try {
                call.parameters["appointmentId"]?.toInt() ?: throw IllegalStateException("Missing parameter: appointmentId")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid appointmentId: must be an integer value")
                return@post
            }
            val dto = call.receive<AppointmentRegistrationDto>()

            call.respond(RegistrationResultDto(transaction {
                val petIdObj = Pets.selectAll()
                    .andWhere { Pets.id eq petId }
                    .map{ it[Pets.id]}
                    .first()

                val appointmentIdObj = AppointmentTypes.selectAll()
                    .andWhere { AppointmentTypes.id eq appointmentId }
                    .map{ it[AppointmentTypes.id]}
                    .first()

                PetAppointmentRegistrations.insert {
                    it[PetAppointmentRegistrations.petId] = petIdObj
                    it[PetAppointmentRegistrations.appointmentId] = appointmentIdObj
                    it[PetAppointmentRegistrations.date] = dto.date
                } get PetAppointmentRegistrations.id
            }.value))
        }

        get("/owners/{id}/appointments"){

        }

        get("/pets/{id}/appointments"){

        }
    }
}

fun ResultRow.toAppointmentDto(): AppointmentDto {
    return AppointmentDto(
        this[AppointmentTypes.id].value,
        this[AppointmentTypes.name],
        this[AppointmentTypes.fee]
    )
}

fun ResultRow.toAppointmentRegistrationDto(): RegistrationDto {
    return RegistrationDto(
        this[PetAppointmentRegistrations.id].value,
        this[PetAppointmentRegistrations.petId].value,
        this[PetAppointmentRegistrations.appointmentId].value,
        this[PetAppointmentRegistrations.date]
    )
}
