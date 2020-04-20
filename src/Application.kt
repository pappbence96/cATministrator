package hu.pappbence

import hu.pappbence.adapters.JsonJodaTimeAdapter
import hu.pappbence.dto.PetOwnerCreatedDto
import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
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
import java.lang.Exception
import java.lang.IllegalArgumentException
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
        SchemaUtils.create(PetOwners, Pets)
    }

    routing {
        get("/owners"){
            call.respond( transaction {
                PetOwners.selectAll().map { petOwnerEntityToDto(it) }
            })
        }

        get("/owners/{id}"){
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch(e : Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@get
            }

            call.respond(transaction {
                val query = PetOwners.selectAll()
                    .andWhere { PetOwners.id eq id }
                    .map{ petOwnerEntityToDto(it) }
                if(query.count() == 1){
                    query.first()
                } else {
                    HttpStatusCode.NotFound
                }
            })
        }

        put("/owners"){
            val dto = call.receive<PetOwnerDto>()
            val id = transaction {
                PetOwners.insert {
                    it[name] = dto.name
                    it[phone] = dto.phone
                    it[balance] = dto.balance
                    it[registration] = dto.registration
                } get PetOwners.id
            }
            call.respond(PetOwnerCreatedDto(id.value))
        }

        put("/owners/{id}"){
            val id = try {
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("Missing parameter: id")
            } catch(e : Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id: must be an integer value")
                return@put
            }
            val dto = call.receive<PetOwnerDto>()

            transaction {
                PetOwners.update ({PetOwners.id eq id}){
                    it[name] = dto.name
                    it[phone] = dto.phone
                    it[balance] = dto.balance
                    it[registration] = dto.registration
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }


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

fun petOwnerEntityToDto(entity: ResultRow) = PetOwnerDto(
    entity[PetOwners.name],
    entity[PetOwners.phone],
    entity[PetOwners.balance],
    entity[PetOwners.registration]
)

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
