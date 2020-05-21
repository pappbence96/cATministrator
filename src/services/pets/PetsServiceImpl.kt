package hu.pappbence.services.pets

import hu.pappbence.dao.PetDao
import hu.pappbence.dao.PetOwnerDao
import hu.pappbence.dto.PetDto
import hu.pappbence.model.Pets
import io.ktor.features.NotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class PetsServiceImpl : PetsService {
    override fun listPets(): List<PetDto> {
        return transaction {
            PetDao.all().map{ it.toPetDto() }
        }
    }

    override fun listPetsOfOwner(ownerId : Int): List<PetDto> {
        return transaction {
            // Throws when there is no such owner so we can get a 404 response
            val owner = PetOwnerDao.findById(ownerId)
                ?: throw NotFoundException("No owner found with id: $ownerId")

            owner.pets.map { it.toPetDto() }
        }
    }

    override fun findPetById(id: Int): PetDto {
        return transaction {
            PetDao.findById(id)?.toPetDto()
                ?: throw NotFoundException("No pet found with id: $id")
        }
    }

    override fun createPetForUser(ownerId: Int, dto: PetDto) : Int{
        validateDtoAndThrow(dto)

        return transaction {
            val petOwner = PetOwnerDao.findById(ownerId)
                ?: throw NotFoundException("No owner found with id: $ownerId")

            PetDao.new {
                name = dto.name
                age = dto.age
                species = dto.species
                added = DateTime.now()
                owner = petOwner
            }.id.value
        }
    }

    override fun updatePet(id: Int, dto: PetDto) {
        validateDtoAndThrow(dto)

        transaction {
            val pet = PetDao.findById(id)
                ?: throw NotFoundException("No pet found with id: $id")

            pet.name = dto.name
            pet.age = dto.age
            pet.species = dto.species
        }
    }

    private fun validateDtoAndThrow(dto: PetDto) {
        if(dto.name.isBlank()) {
            throw IllegalArgumentException("Name must not be blank")
        }
        if(dto.species.isBlank()) {
            throw IllegalArgumentException("Species must not be blank")
        }
        if(dto.age < 0) {
            throw java.lang.IllegalArgumentException("Age must be a non-negative integer")
        }
    }
}