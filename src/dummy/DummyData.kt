package hu.pappbence.dummy

import hu.pappbence.dao.AppointmentTypeDao
import hu.pappbence.dao.PetAppointmentRegistrationDao
import hu.pappbence.dao.PetDao
import hu.pappbence.dao.PetOwnerDao
import org.joda.time.DateTime

fun initDbDummyData(){
    if(PetOwnerDao.count() > 0L
        || PetDao.count() > 0L
        || AppointmentTypeDao.count() > 0L
        || PetAppointmentRegistrationDao.count() > 0L){
        return
    }

    val o1 = PetOwnerDao.new {
        name = "Teszt Elek"
        phone = "0123456"
        registration = DateTime.now()
        balance = 30000
    }
    val o2 = PetOwnerDao.new {
        name = "Próba Patrícia"
        phone = "9876543"
        registration = DateTime.now()
        balance = 0
    }
    val o3 = PetOwnerDao.new {
        name = "Timothy Test"
        phone = "456789"
        registration = DateTime.now()
        balance = 1000000
    }

    val p1 = PetDao.new {
        name = "Catto"
        species = "Cat"
        age = 9
        added = DateTime.now()
        owner = o1
    }
    val p2 = PetDao.new {
        name = "Klotild"
        species = "Dog"
        age = 4
        added = DateTime.now()
        owner = o2
    }
    val p3 = PetDao.new {
        name = "Herold"
        species = "Fish"
        age = 1
        added = DateTime.now()
        owner = o3
    }
    val p4 = PetDao.new {
        name = "Marlo"
        species = "Dog"
        age = 10
        added = DateTime.now()
        owner = o2
    }
    val p5 = PetDao.new {
        name = "Furball"
        species = "Cat"
        age = 3
        added = DateTime.now()
        owner = o1
    }

    val a1 = AppointmentTypeDao.new {
        name = "Fur care"
        fee = 6000

    }
    val a2 = AppointmentTypeDao.new {
        name = "Health checkup"
        fee = 15000
    }
    val a3 = AppointmentTypeDao.new {
        name = "Nail trimming"
        fee = 5000
    }
    val a4 = AppointmentTypeDao.new {
        name = "Washing & drying"
        fee = 8500
    }
    val a5 = AppointmentTypeDao.new {
        name = "Fur trimming"
        fee = 9990
    }

    PetAppointmentRegistrationDao.new {
        pet = p1
        appointment = a1
        date = DateTime(2020, 6, 10, 14, 30)
    }
    PetAppointmentRegistrationDao.new {
        pet = p1
        appointment = a2
        date = DateTime(2020, 6, 24, 12, 0)
    }
    PetAppointmentRegistrationDao.new {
        pet = p2
        appointment = a3
        date = DateTime(2020, 9, 20, 8, 15)
    }
    PetAppointmentRegistrationDao.new {
        pet = p3
        appointment = a4
        date = DateTime(2020, 8, 3, 16, 0)
    }
    PetAppointmentRegistrationDao.new {
        pet = p5
        appointment = a1
        date = DateTime(2020, 7, 8, 15, 30)
    }
    PetAppointmentRegistrationDao.new {
        pet = p5
        appointment = a5
        date = DateTime(2020, 8, 9, 8, 45)
    }
}