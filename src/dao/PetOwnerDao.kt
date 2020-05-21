package hu.pappbence.dao

import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PetOwnerDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PetOwnerDao>(PetOwners)
    var name by PetOwners.name
    var phone by PetOwners.phone
    var balance by PetOwners.balance
    var registration by PetOwners.registration
    val pets by PetDao referrersOn Pets.ownerId

    fun toPetOwnerDto() = PetOwnerDto(id.value, name, phone, balance, registration)
}