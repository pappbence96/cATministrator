package hu.pappbence.services

import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.model.PetOwners
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class OwnersServiceImpl : OwnersService {
    override fun listAll(): List<PetOwnerDto> {
        return transaction {
            PetOwners.selectAll().toList().map{ it.toPetOwnerDto() }
        }
    }

    override fun findById(id: Int) : PetOwnerDto {
        return transaction { PetOwners.selectAll()
                .andWhere { PetOwners.id eq id }
                .map { it.toPetOwnerDto() }
                .first()
        }
    }

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

    private fun ResultRow.toPetOwnerDto() = PetOwnerDto(
        this[PetOwners.name],
        this[PetOwners.phone],
        this[PetOwners.balance],
        this[PetOwners.registration]
    )
}