package hu.pappbence.modules

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.testModule(){
    routing {
        get("test"){
            call.respond("Test successful")
        }
    }
}