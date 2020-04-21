package hu.pappbence.services.owners

import hu.pappbence.dto.PetOwnerDto
import hu.pappbence.model.PetOwners
import org.jetbrains.exposed.sql.Query

interface OwnersService {
    fun listAll() : List<PetOwnerDto>
    fun findById(id: Int): PetOwnerDto
    fun create(dto: PetOwnerDto): Int
    fun update(id: Int, dto: PetOwnerDto) : Int
}