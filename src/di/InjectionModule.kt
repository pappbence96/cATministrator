package hu.pappbence.di

import hu.pappbence.services.owners.*
import hu.pappbence.services.pets.*
import org.koin.dsl.module

val injectionModule = module {
    single<OwnersService> { OwnersServiceImpl() }
    single<PetsService> { PetsServiceImpl() }
}