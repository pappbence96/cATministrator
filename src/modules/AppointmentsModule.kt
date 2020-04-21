package hu.pappbence.modules

import hu.pappbence.dto.AppointmentDto
import hu.pappbence.model.AppointmentTypes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

fun Application.appointmentsModule() {

    routing {
        get("/appointments") {
            call.respond(transaction {
                AppointmentTypes.selectAll().map{ it.toAppointmentDto() }
            })
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
