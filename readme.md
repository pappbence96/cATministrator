# cATministrator API

This project is a simple API that is used to manage the everyday operation of an imaginary animal beauty salon. It is only for internal 
use so no authentication is in place. The endpoints are only accessed by the employees of the salon. 

### Features:
* Register pet owners:
    * Record and update customer information
    * Manage the balance of each client
* Manage pets:
    * Record and update pets
    * Assign the to their respective owner
* Track appointments: 
    * Register pets for appointments
    * Track registrations on a per pet or per owner basis
    * Deduct the cost of the appointment from the balance of the owner. If the balance would fall below 0, the registration
    process fails

### Used technology:
* [Kotlin](https://kotlinlang.org/) programming language
* [Ktor](https://ktor.io/) API framework
* [SQLite](https://www.sqlite.org/index.html) database engine
* [Exposed](https://github.com/JetBrains/Exposed) ORM
* [Koin](https://insert-koin.io/) dependency injection framework

# Remarks
* The endpoint descriptions are declared in their respective module files under the `modules` folder. These are registered
in the `application.conf` descriptor. Separating the whole API interface into smaller modules helps keeping the endpoints
more manageable.
* Business logic is contained only in the service classes under the `services` folder. These services are then injected
into the modules via DI, thus enforcing proper separation of the application layers.
* Translating exceptions to 400/404 statuses is done by installing custom exception handlers during startup via the _StatusPages_ 
feature. `KeyNotFound` exceptions get translated into an **404 - Not found** response, while `IllegalArgumentException` 
exceptions are returned as a **400 - Bad request** status code. 

# API documentation
The API features the following endpoints:

### _Owners_ endpoint
| Method | Url | Description | Body | Response |
| --- | --- | --- | --- | --- | 
| GET | `/owners` | List pet owners | - | List of pet owners |
| GET | `/owners/{ownerId}` | Get a specific pet owner | - | Pet owner |
| POST | `/owners` | Register a new pet owner | Pet owner | ID |
| PUT | `/owners/{ownerId}` | Update an existing pet owner | Pet owner | - |
| POST | `/owners/{ownerId}/pets` | Record a new pet for an existing owner | Pet | ID |
| GET | `/owners/{ownerId}/registrations` | List registrations of a specific owner | - | List of registrations |
### _Pets_ endpoint
| Method | Url | Description | Body | Response | 
| --- | --- | --- | --- | --- | 
| GET | `/pets` | List pets | - | List of pets | 
| GET | `/pets/{petId}` | Get a specific pet | - | Pet | 
| PUT | `/pets/{petId}` | Update an existing pet | Pet | - | 
| GET | `/pets/{petId}/registrations` | List registrations of a specific pet | List of registrations | - | 
| POST | `/pets/{petId}/registrations/{appointmentId}` | Register pet for an appointment | Date of the appointment | - | 
### _Appointments_ endpoint
| Method | Url | Description | Response | 
| --- | --- | --- | ---  | 
| GET | `/appointments` | List pets | List of appointments |
| GET | `/appointments/{appointmentId}` | Get a specific appointment | Appointment |
| GET | `/appointments/registrations` | List all registrations | Registrations |

# Data model
**AppointmentTypes**
```sqlite
CREATE TABLE "AppointmentTypes" (
    "id"	INTEGER PRIMARY KEY AUTOINCREMENT,
    "name"	VARCHAR(50) NOT NULL,
    "fee"	INT NOT NULL
);
```
**PetOwners**
```sqlite
CREATE TABLE "PetOwners" (
    "id"	INTEGER PRIMARY KEY AUTOINCREMENT,
    "name"	VARCHAR(50) NOT NULL,
    "phoneNumber"	VARCHAR(20) NOT NULL,
    "balance"	INT NOT NULL,
    "registration"	NUMERIC NOT NULL,
    CONSTRAINT "check_PetOwners_0" CHECK(balance>=0)
 );
```
**Pets**
```sqlite
CREATE TABLE "Pets" (
    "id"	INTEGER PRIMARY KEY AUTOINCREMENT,
    "name"	VARCHAR(50) NOT NULL,
    "age"	INT NOT NULL,
    "species"	VARCHAR(20) NOT NULL,
    "added"	NUMERIC NOT NULL,
    "ownerId"	INT NOT NULL,
    CONSTRAINT "fk_Pets_ownerId_id" FOREIGN KEY("ownerId") REFERENCES "PetOwners"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
 );
```
**PetAppointmentRegistrations**
```sqlite
CREATE TABLE "PetAppointmentRegistrations" (
    "id"	INTEGER PRIMARY KEY AUTOINCREMENT,
    "petId"	INT NOT NULL,
    "appointmentId"	INT NOT NULL,
    "appointmentDate"	NUMERIC NOT NULL,
    CONSTRAINT "fk_PetAppointmentRegistrations_petId_id" FOREIGN KEY("petId") REFERENCES "Pets"("id") ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT "fk_PetAppointmentRegistrations_appointmentId_id" FOREIGN KEY("appointmentId") REFERENCES "AppointmentTypes"("id") ON DELETE RESTRICT ON UPDATE RESTRICT
 );
```
