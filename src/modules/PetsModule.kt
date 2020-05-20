package hu.pappbence.modules

import hu.pappbence.dto.PetDto
import hu.pappbence.dto.ResourceCreatedDto
import hu.pappbence.extensions.getUrlParam
import hu.pappbence.services.pets.PetsService
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

fun Application.petsModule() {
    val petsService: PetsService by inject()

    routing {
        get("/pets") {
            call.respond(petsService.listPets())
        }

        get("/pets/{petId}"){
            val id = call.getUrlParam("petId")

            call.respond(petsService.findPetById(id))
        }

        put("/pets/{petId}"){
            val id = call.getUrlParam("petId")
            val dto = call.receive<PetDto>()

            petsService.updatePet(id, dto)
            call.respond(HttpStatusCode.NoContent)
        }

        get("/owners/{ownerId}/pets"){
            val id = call.getUrlParam("ownerId")

            call.respond(petsService.listPetsOfOwner(id))
        }

        post("/owners/{ownerId}/pets"){
            val id = call.getUrlParam("ownerId")
            val dto = call.receive<PetDto>()

            call.respond(ResourceCreatedDto(petsService.createPetForUser(id, dto)))
        }
    }
}
