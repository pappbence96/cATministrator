package hu.pappbence.services.pets

import hu.pappbence.dto.PetDto
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class PetsServiceImpl : PetsService {
    override fun listPets(): List<PetDto> {
        return transaction {
            Pets.selectAll().toList().map{ it.toPetDto() }
        }
    }

    override fun listPetsOfOwner(ownerId : Int): List<PetDto> {
        return transaction {
            // Throws when there is no such owner so we can get a 404 response
            PetOwners.selectAll()
                .andWhere { PetOwners.id eq ownerId }
                .first()

            Pets.selectAll()
                .andWhere { Pets.ownerId eq ownerId }
                .map { it.toPetDto() }
        }
    }

    override fun findPetById(id: Int): PetDto {
        return transaction {
            Pets.selectAll()
            .andWhere { Pets.id eq id }
            .map { it.toPetDto() }
            .first()
        }
    }

    override fun createPetForUser(ownerId: Int, dto: PetDto) : Int{
        val petId = transaction {
            val ownerIdObj = PetOwners.selectAll()
                .andWhere { PetOwners.id eq ownerId }
                .map{ it[PetOwners.id]}
                .first()

            Pets.insert {
                it[name] = dto.name
                it[age] = dto.age
                it[species] = dto.species
                it[added] = DateTime.now()
                it[Pets.ownerId] = ownerIdObj
            } get Pets.id
        }

        return petId.value
    }

    override fun updatePet(id: Int, dto: PetDto): Int {
        return transaction {
            Pets.update({ Pets.id eq id }) {
                it[name] = dto.name
                it[age] = dto.age
                it[species] = dto.species
            }
        }
    }

    // Map from DB entity to DTO
    private fun ResultRow.toPetDto() = PetDto(
        this[Pets.id].value,
        this[Pets.name],
        this[Pets.age],
        this[Pets.species],
        this[Pets.added],
        this[Pets.ownerId].value
    )
}