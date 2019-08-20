package com.derek.schedchallenge.modules

import com.derek.schedchallenge.repositories.SessionDataImporter
import com.derek.schedchallenge.repositories.SessionsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object RepositoryModule {

  val repositoriesModule = module {
    single { SessionsRepository(get(), get(), get(), get()) }
    single { SessionDataImporter(androidContext(), get()) }
  }
}