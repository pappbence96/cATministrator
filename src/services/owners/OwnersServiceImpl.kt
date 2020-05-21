package hu.pappbence.services.owners

import hu.pappbence.dao.PetOwnerDao
import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.model.PetOwners
import io.ktor.features.NotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

/*
    Service handling pet owner related tasks
 */
class OwnersServiceImpl : OwnersService {
    // List all registered pet owners
    override fun listAll(): List<PetOwnerDto> {
        return transaction {
            PetOwnerDao.all().map{ it.toPetOwnerDto() }
        }
    }

    // Find a pet owner specified by their ID
    override fun findById(id: Int) : PetOwnerDto {
        return transaction {
            PetOwnerDao.findById(id)?.toPetOwnerDto()
                ?: throw NotFoundException("No owner found with id: $id")
        }
    }

    // Register a new pet owner
    override fun create(dto: PetOwnerDto): Int {
        validateDtoAndThrow(dto)

        return transaction {
             PetOwnerDao.new {
                name = dto.name
                phone = dto.phone
                balance = dto.balance
                registration = DateTime.now()
            }.id.value
        }
    }

    // Update an existing pet owner specified by their ID
    override fun update(id: Int, dto: PetOwnerDto){
        validateDtoAndThrow(dto)

        transaction {
            val owner = PetOwnerDao.findById(id)
                ?: throw NotFoundException("No owner found with id: $id")

            owner.name = dto.name
            owner.phone = dto.phone
            owner.balance = dto.balance
        }
    }

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