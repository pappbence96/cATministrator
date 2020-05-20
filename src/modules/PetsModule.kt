package hu.pappbence.modules

import hu.pappbence.dto.PetDto
import hu.pappbence.dto.ResourceCreatedDto
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

        get("/pets/{id}"){
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@get
            }
            try {
                call.respond(petsService.findPetById(id))
            } catch(e : NoSuchElementException){
                call.respond(HttpStatusCode.NotFound)
            }
        }

        put("/pets/{id}"){
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@put
            }
            val dto = call.receive<PetDto>()

            petsService.updatePet(id, dto)
            call.respond(HttpStatusCode.NoContent)
        }

        get("/owners/{id}/pets"){
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@get
            }

            call.respond(petsService.listPetsOfOwner(id))
        }

        post("/owners/{id}/pets"){
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@post
            }
            val dto = call.receive<PetDto>()

            call.respond(ResourceCreatedDto(petsService.createPetForUser(id, dto)))
        }
    }
}
