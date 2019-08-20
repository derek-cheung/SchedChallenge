package com.derek.schedchallenge.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.derek.schedchallenge.models.Person
import com.derek.schedchallenge.models.Session

@Database(entities = [
  Session::class,
  Person::class
], version = 1)
@TypeConverters(DatabaseConverters::class)
abstract class SchedDatabase : RoomDatabase() {

  abstract fun sessionDao(): SessionDao
  abstract fun personDao(): PersonDao
}