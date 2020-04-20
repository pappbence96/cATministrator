package hu.pappbence.di

import hu.pappbence.services.OwnersService
import hu.pappbence.services.OwnersServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val injectionModule = module {
    single<OwnersService> { OwnersServiceImpl() }
}