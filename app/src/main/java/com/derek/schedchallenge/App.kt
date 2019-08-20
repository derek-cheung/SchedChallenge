package com.derek.schedchallenge

import android.app.Application
import com.derek.schedchallenge.modules.AppModule
import com.derek.schedchallenge.modules.DatabaseModule
import com.derek.schedchallenge.modules.RepositoryModule
import com.derek.schedchallenge.modules.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidLogger()
      androidContext(this@App)

      modules(
        listOf(
          AppModule.moshiModule,
          AppModule.loggerModule,
          DatabaseModule.databaseModule,
          RepositoryModule.repositoriesModule,
          ViewModelModule.viewModelModule
        )
      )
    }
  }
}