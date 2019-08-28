package com.derek.schedchallenge.repositories

import com.derek.schedchallenge.db.PersonDao
import com.derek.schedchallenge.db.SchedDatabase
import com.derek.schedchallenge.db.SessionDao
import com.derek.schedchallenge.models.Person
import com.derek.schedchallenge.models.Session
import com.derek.schedchallenge.models.SessionResponse
import io.reactivex.Completable
import io.reactivex.Single

class SessionsRepository(
  private val schedDatabase: SchedDatabase,
  private val sessionDao: SessionDao,
  private val personDao: PersonDao,
  private val sessionDataImporter: SessionDataImporter
) {

  enum class SessionDataType(
    val fileName: String
  ) {
    INITIAL("export.json"),
    EDITED("edited.json"),
    DELETED("deleted.json")
  }

  data class SessionData(
    val sessions: List<Session>,
    val persons: List<Person>
  )

  fun getSessionData(
    dataType: SessionDataType
  ): Single<SessionData> =
    sessionDataImporter.importSessionData(
      sessionDataType = dataType
    )
      .map { sessionResponses ->
        val sessions = sessionResponses.map { sessionResponse ->
          sessionResponse.toSession()
        }

        val persons = sessionResponses.fold<SessionResponse, MutableSet<Person>>(mutableSetOf()) { persons, sessionResponse ->
          persons.addAll(
            sessionResponse.artists
            .plus(sessionResponse.exhibitors)
            .plus(sessionResponse.moderators)
            .plus(sessionResponse.speakers)
            .plus(sessionResponse.sponsors)
          )
          persons
        }

        SessionData(
          sessions = sessions,
          persons = persons.toList()
        )
      }

  fun saveSessions(
    sessions: List<Session>
  ): Single<Int> =
    if (sessions.isEmpty()) {
      Single.just(0)
    } else {
      sessionDao.saveSessions(
        sessions = sessions
      ).toSingle { sessions.size }
    }

  fun updateSessions(
    sessions: List<Session>
  ): Single<Int> =
    if (sessions.isEmpty()) {
      Single.just(0)
    } else {
      sessionDao.updateSessions(
        sessions = sessions
      ).toSingle { sessions.size }
    }

  fun deleteSessions(
    sessions: List<Session>
  ): Single<Int> =
    if (sessions.isEmpty()) {
      Single.just(0)
    } else {
      sessionDao.deleteSessions(
        sessions = sessions
      ).toSingle { sessions.size }
    }

  fun savePersons(
    persons: List<Person>
  ): Single<Int> =
    if (persons.isEmpty()) {
      Single.just(0)
    } else {
      personDao.savePersons(
        persons = persons
      ).toSingle { persons.size }
    }

  fun updatePersons(
    persons: List<Person>
  ): Single<Int> =
    if (persons.isEmpty()) {
      Single.just(0)
    } else {
      personDao.updatePersons(
        persons = persons
      ).toSingle { persons.size }
    }

  fun deletePersons(
    persons: List<Person>
  ): Single<Int> =
    if (persons.isEmpty()) {
      Single.just(0)
    } else {
      personDao.deletePerson(
        persons = persons
      ).toSingle { persons.size }
    }

  fun getAllSessions(): Single<List<Session>> =
    sessionDao.getAllSessions()

  fun getAllSessionsForInput(input: String): Single<List<Session>> =
    sessionDao.getSessionsForInput(input)

  fun getAllPersons(): Single<List<Person>> =
    personDao.getAllPersons()

  fun clearSessionData(): Completable =
    Completable.fromCallable { schedDatabase.clearAllTables() }

  private fun SessionResponse.toSession(): Session =
    Session(
      id = id,
      name = name,
      description = description,
      speakers = speakers.map { it.id },
      sponsors = sponsors.map { it.id },
      moderators = moderators.map { it.id },
      artists = artists.map { it.id },
      exhibitors = exhibitors.map { it.id },
      startTime = startTime,
      endTime = endTime
    )
}