package com.derek.schedchallenge.db

import androidx.room.*
import com.derek.schedchallenge.models.Person
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PersonDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun savePersons(
    persons: List<Person>
  ): Completable

  @Update
  fun updatePersons(
    persons: List<Person>
  ): Completable

  @Delete
  fun deletePerson(
    persons: List<Person>
  ): Completable

  @Query("SELECT * FROM Person")
  fun getAllPersons(): Single<List<Person>>
}