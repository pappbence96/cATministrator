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
        install(ContentNegotiation){
            gson{
                setPrettyPrinting()
                serializeNulls()
                setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                registerTypeAdapter(DateTime::class.java, JsonJodaTimeAdapter())
            }
        }
    }

    Database.connect("jdbc:sqlite:data/data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(PetOwners, Pets, AppointmentTypes, PetAppointmentRegistrations)
/*
        AppointmentTypes.insert {
            it[name] = "Bathing & drying"
            it[fee] = 5000
        }
        AppointmentTypes.insert {
            it[name] = "Grooming"
            it[fee] = 6500
        }
        AppointmentTypes.insert {
            it[name] = "Health checkup"
            it[fee] = 3000
        }
        AppointmentTypes.insert {
            it[name] = "Nail care"
            it[fee] = 4800
        }
        AppointmentTypes.insert {
            it[name] = "Special fur trimming"
            it[fee] = 12000
        }
        AppointmentTypes.insert {
            it[name] = "Ear care"
            it[fee] = 5000
        }*/
    }

    startKoin{
        modules(injectionModule)
    }

    routing {
        get() {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }
    }
}


fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
