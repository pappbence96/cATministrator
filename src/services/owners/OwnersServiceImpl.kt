package hu.pappbence.services.owners

import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.model.PetOwners
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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
                .first()
        }
    }

    // Register a new pet owner
    override fun create(dto: PetOwnerDto): Int {
        val id = transaction {
            PetOwners.insert {
                it[name] = dto.name
                it[phone] = dto.phone
                it[balance] = dto.balance
                it[registration] = dto.registration
            } get PetOwners.id
        }
        return id.value
    }

    // Update an existing pet owner specified by their ID
    override fun update(id: Int, dto: PetOwnerDto) {
        transaction {
            PetOwners.update({ PetOwners.id eq id }) {
                it[name] = dto.name
                it[phone] = dto.phone
                it[balance] = dto.balance
                it[registration] = dto.registration
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
}