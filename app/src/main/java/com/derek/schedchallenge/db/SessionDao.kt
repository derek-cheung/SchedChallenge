package com.derek.schedchallenge.db

import androidx.room.*
import com.derek.schedchallenge.models.Session
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface SessionDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveSessions(
    sessions: List<Session>
  ): Completable

  @Update
  fun updateSessions(
    sessions: List<Session>
  ): Completable

  @Delete
  fun deleteSessions(
    sessions: List<Session>
  ): Completable

  @Query("SELECT * FROM Session")
  fun getAllSessions(): Single<List<Session>>

  @Query("SELECT * FROM Session WHERE name LIKE :input")
  fun getSessionsForInput(input: String): Single<List<Session>>
}