package hu.pappbence.services.pets

import hu.pappbence.dto.PetDto

interface PetsService {
    fun listPets() : List<PetDto>
    fun listPetsOfOwner(ownerId: Int) : List<PetDto>
    fun findPetById(id: Int) : PetDto
    fun createPetForUser(ownerId: Int, dto: PetDto) : Int
    fun updatePet(id: Int, dto: PetDto)
}