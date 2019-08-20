package com.derek.schedchallenge.modules

import com.derek.schedchallenge.Logger
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module

object AppModule {

  val moshiModule = module {
    single {
      Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    }
  }

  val loggerModule = module {
    single {
      Logger(get())    }
  }
}