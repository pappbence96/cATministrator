package hu.pappbence.services.owners

import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.model.PetOwners
import io.ktor.features.NotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.sqlite.SQLiteException

/*
    Service handling pet owner related tasks
 */
class OwnersServiceImpl : OwnersService {
    // List all registered pet owners
    override fun listAll(): List<PetOwnerDto> {
        return transaction {
            PetOwners.selectAll().toList().map{ it.toPetOwnerDto() }
        }
    }
    // Find a pet owner specified by their ID
    override fun findById(id: Int) : PetOwnerDto {
        return transaction { PetOwners.selectAll()
                .andWhere { PetOwners.id eq id }
                .map { it.toPetOwnerDto() }
                .firstOrNull() ?: throw NotFoundException("No owner found with id: $id")
        }
    }

    // Register a new pet owner
    override fun create(dto: PetOwnerDto): Int {
        validateDtoAndThrow(dto)

        val id = transaction {
            PetOwners.insert {
                it[name] = dto.name
                it[phone] = dto.phone
                it[balance] = dto.balance
                it[registration] = DateTime.now()
            } get PetOwners.id
        }
        return id.value
    }

    // Update an existing pet owner specified by their ID
    override fun update(id: Int, dto: PetOwnerDto){
        validateDtoAndThrow(dto)

        transaction {
            val result = PetOwners.update ({ PetOwners.id eq id }) {
                it[name] = dto.name
                it[phone] = dto.phone
                it[balance] = dto.balance
            }
            if(result == 0) {
                throw NotFoundException("No owner found with id: $id")
            }
        }
    }

    // Map from DB entity to DTO
    private fun ResultRow.toPetOwnerDto() = PetOwnerDto(
        this[PetOwners.id].value,
        this[PetOwners.name],
        this[PetOwners.phone],
        this[PetOwners.balance],
        this[PetOwners.registration]
    )

    private fun validateDtoAndThrow(dto: PetOwnerDto){
        if(dto.name.isBlank()) {
            throw IllegalArgumentException("Name must not be blank")
        }
        if(dto.phone.isBlank()) {
            throw IllegalArgumentException("Phone number must not be blank")
        }
        if(dto.balance < 0) {
            throw java.lang.IllegalArgumentException("Balance must be a non-negative integer")
        }
    }
}