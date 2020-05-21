package hu.pappbence.dao

import hu.pappbence.dto.PetDto
import hu.pappbence.model.PetAppointmentRegistrations
import hu.pappbence.model.PetOwners
import hu.pappbence.model.Pets
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PetDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PetDao>(Pets)

    var name by Pets.name
    var age by Pets.age
    var species by Pets.species
    var added by Pets.added
    var owner by PetOwnerDao referencedOn Pets.ownerId
    val registrations by PetAppointmentRegistrationDao referrersOn PetAppointmentRegistrations.petId

    fun toPetDto() = PetDto(id.value, name, age, species, added, owner.id.value)
}