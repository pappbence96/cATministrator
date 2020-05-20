package hu.pappbence.extensions

import io.ktor.application.ApplicationCall

fun ApplicationCall.getUrlParam(parameterName: String) = this.parameters[parameterName]?.toIntOrNull()
    ?: throw IllegalArgumentException("Invalid $parameterName: must be an integer value")