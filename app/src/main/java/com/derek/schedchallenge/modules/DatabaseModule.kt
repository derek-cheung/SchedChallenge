package com.derek.schedchallenge.modules

import androidx.room.Room
import com.derek.schedchallenge.db.SchedDatabase
import org.koin.dsl.module

object DatabaseModule {

  val databaseModule = module {
    single { Room.databaseBuilder(get(), SchedDatabase::class.java, "sched-db").build() }
    single { get<SchedDatabase>().sessionDao() }
    single { get<SchedDatabase>().personDao() }
  }
}