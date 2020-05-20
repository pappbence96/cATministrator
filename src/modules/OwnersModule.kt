package hu.pappbence.modules

import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.dto.ResourceCreatedDto
import hu.pappbence.extensions.getUrlParam
import hu.pappbence.services.owners.OwnersService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import java.lang.Exception

fun Application.ownersModule() {
    val ownersService: OwnersService by inject()

    routing {
        get("/owners") {
            call.respond(ownersService.listAll())
        }

        get("/owners/{ownerId}") {
            val id = call.getUrlParam("ownerId")

            call.respond(ownersService.findById(id))
        }

        post("/owners") {
            val dto = call.receive<PetOwnerDto>()
            val id = ownersService.create(dto)

            call.respond(ResourceCreatedDto(id))
        }

        put("/owners/{ownerId}") {
            val id = call.getUrlParam("ownerId")
            val dto = call.receive<PetOwnerDto>()

            ownersService.update(id, dto)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}