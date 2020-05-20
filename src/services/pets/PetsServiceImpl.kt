package hu.pappbence.services.pets

import hu.pappbence.dto.PetDto
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import io.ktor.features.NotFoundException
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
                .firstOrNull() ?: throw NotFoundException("No owner found with id: $ownerId")

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
            .firstOrNull() ?: throw NotFoundException("No pet found with id: $id")
        }
    }

    override fun createPetForUser(ownerId: Int, dto: PetDto) : Int{
        val petId = transaction {
            val ownerIdObj = PetOwners.selectAll()
                .andWhere { PetOwners.id eq ownerId }
                .map{ it[PetOwners.id]}
                .firstOrNull() ?: throw NotFoundException("No owner found with id: $ownerId")

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

    override fun updatePet(id: Int, dto: PetDto) {
        transaction {
            val result = Pets.update({ Pets.id eq id }) {
                it[name] = dto.name
                it[age] = dto.age
                it[species] = dto.species
            }
            if(result == 0) {
                throw NotFoundException("No pet found with id: $id")
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