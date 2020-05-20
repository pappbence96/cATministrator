package hu.pappbence.modules

import hu.pappbence.dto.*
import hu.pappbence.model.AppointmentTypes
import hu.pappbence.model.PetAppointmentRegistrations
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import hu.pappbence.services.appointments.AppointmentsService
import hu.pappbence.services.owners.OwnersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject
import java.lang.Exception

fun Application.appointmentsModule() {

    val appointmentsService: AppointmentsService by inject()

    routing {
        get("/appointments") {
            call.respond(appointmentsService.listAppointments())
        }

        get("/appointments/{id}") {
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@get
            }
            try {
                call.respond(appointmentsService.findAppointmentById(id))
            } catch(e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/appointments/registrations") {
            call.respond(appointmentsService.listRegistrations())
        }

        post("/pets/{petId}/registrations/{appointmentId}"){
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

            call.respond(appointmentsService.registerPetForAppointment(petId, appointmentId, dto.date))
        }

        get("/owners/{ownerId}/registrations"){
            val ownerId = try {
                call.parameters["ownerId"]?.toInt() ?: throw IllegalStateException("Missing parameter: ownerId")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ownerId: must be an integer value")
                return@get
            }

            call.respond(appointmentsService.listRegistrationsOfOwner(ownerId))
        }

        get("/pets/{petId}/registrations"){
            val petId = try {
                call.parameters["petId"]?.toInt() ?: throw IllegalStateException("Missing parameter: petId")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid petId: must be an integer value")
                return@get
            }

            call.respond(appointmentsService.listRegistrationsOfPet(petId))
        }
    }
}