package hu.pappbence.modules

import hu.pappbence.dto.PetDto
import hu.pappbence.dto.PetOwnerCreatedDto
import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.services.owners.OwnersService
import hu.pappbence.services.pets.PetsService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing
import kotlinx.html.*
import org.koin.ktor.ext.inject
import java.lang.Exception

fun Application.ownersModule() {
    val ownersService: OwnersService by inject()

    routing {
        get("/owners") {
            call.respond(ownersService.listAll())
        }

        get("/owners/{id}") {
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@get
            }

            call.respond(ownersService.findById(id))
        }

        post("/owners") {
            val dto = call.receive<PetOwnerDto>()
            val id = ownersService.create(dto)
            call.respond(PetOwnerCreatedDto(id))
        }

        put("/owners/{id}") {
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@put
            }
            val dto = call.receive<PetOwnerDto>()

            ownersService.update(id, dto)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}