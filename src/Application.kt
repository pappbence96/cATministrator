package hu.pappbence

import hu.pappbence.adapters.JsonJodaTimeAdapter
import hu.pappbence.di.injectionModule
import hu.pappbence.model.AppointmentTypes
import hu.pappbence.model.PetAppointmentRegistrations
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.logging.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.NotFoundException
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.koin.core.context.startKoin
import java.sql.Connection

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                serializeNulls()
                setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                registerTypeAdapter(DateTime::class.java, JsonJodaTimeAdapter())
            }
        }
        install(StatusPages) {
            exception<NotFoundException> { cause ->
                call.respond(HttpStatusCode.NotFound, cause.message ?: "")
            }
        }
    }

    Database.connect("jdbc:sqlite:data/data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(PetOwners, Pets, AppointmentTypes, PetAppointmentRegistrations)
    }

    startKoin {
        modules(injectionModule)
    }
}
