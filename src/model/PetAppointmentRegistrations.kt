package hu.pappbence.model

import hu.pappbence.model.Pets.references
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object PetAppointmentRegistrations : IntIdTable() {
    val petId = reference("petId", Pets).references(Pets.id)
    val appointmentId = reference("appointmentId", AppointmentTypes).references(AppointmentTypes.id)
    val date = datetime("appointmentDate")
}