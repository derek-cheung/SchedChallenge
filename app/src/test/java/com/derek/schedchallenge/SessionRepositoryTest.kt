package com.derek.schedchallenge

import com.derek.schedchallenge.db.PersonDao
import com.derek.schedchallenge.db.SchedDatabase
import com.derek.schedchallenge.db.SessionDao
import com.derek.schedchallenge.models.Person
import com.derek.schedchallenge.models.Session
import com.derek.schedchallenge.models.SessionResponse
import com.derek.schedchallenge.repositories.SessionDataImporter
import com.derek.schedchallenge.repositories.SessionsRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test

class SessionRepositoryTest {

  private val persons = listOf(
    Person(
      id = "1",
      name = "Name1"
    ),

    Person(
      id = "2",
      name = "Name2"
    )
  )

  private val sessionResponses = listOf(
    SessionResponse(
      id = "S1",
      name = "N1",
      description = "D1",
      artists = listOf(persons[0]),
      exhibitors = listOf(persons[1]),
      moderators = listOf(persons[0], persons[1]),
      speakers = listOf(),
      sponsors = listOf(persons[1]),
      startTime = 89,
      endTime = 343
    ),

    SessionResponse(
      id = "S2",
      name = "N2",
      description = "D2",
      artists = listOf(persons[1]),
      exhibitors = listOf(persons[0]),
      moderators = listOf(persons[1], persons[0]),
      speakers = listOf(),
      sponsors = listOf(persons[0]),
      startTime = 890,
      endTime = 3434
    )
  )

  private val schedDatabase = mock<SchedDatabase>()

  private val sessionDao = mock<SessionDao> {
    on { saveSessions(any()) } doReturn Completable.complete()
    on { updateSessions(any()) } doReturn Completable.complete()
    on { deleteSessions(any()) } doReturn Completable.complete()
  }

  private val personDao = mock<PersonDao> {
    on { savePersons(any()) } doReturn Completable.complete()
    on { updatePersons(any()) } doReturn Completable.complete()
    on { deletePerson(any()) } doReturn Completable.complete()
  }

  private val sessionDataImporter = mock<SessionDataImporter> {
    on { importSessionData(any()) } doReturn Single.just(sessionResponses)
  }

  private val repository by lazy {
    SessionsRepository(
      schedDatabase = schedDatabase,
      sessionDao = sessionDao,
      personDao = personDao,
      sessionDataImporter = sessionDataImporter
    )
  }

  @Test
  fun testGetSessionData() {
    repository.getSessionData(
      dataType = SessionsRepository.SessionDataType.INITIAL
    )
      .test()
      .assertValue { sessionData ->
        var sessionsEqual = true
        for (i in 0 until sessionResponses.size) {
          if (!sessionResponses[i].isEqual(sessionData.sessions[i])) {
            sessionsEqual = false
          }
        }

        sessionsEqual && sessionData.persons == persons
      }
  }

  @Test
  fun testSaveSessions() {
    repository.getSessionData(
      dataType = SessionsRepository.SessionDataType.INITIAL
    )
      .flatMap { sessionData ->
        repository.saveSessions(sessionData.sessions)
      }
      .test()
      .assertValue(2)
  }

  @Test
  fun `Test save sessions with no data`() {
    repository.saveSessions(listOf())
      .test()
      .assertValue(0)
  }

  @Test
  fun testUpdateSessions() {
    repository.getSessionData(
      dataType = SessionsRepository.SessionDataType.INITIAL
    )
      .flatMap { sessionData ->
        repository.updateSessions(sessionData.sessions)
      }
      .test()
      .assertValue(2)
  }

  @Test
  fun `Test update sessions with no data`() {
    repository.updateSessions(listOf())
      .test()
      .assertValue(0)
  }

  @Test
  fun testDeleteSessions() {
    repository.getSessionData(
      dataType = SessionsRepository.SessionDataType.INITIAL
    )
      .flatMap { sessionData ->
        repository.deleteSessions(sessionData.sessions)
      }
      .test()
      .assertValue(2)
  }

  @Test
  fun `Test delete sessions with no data`() {
    repository.deleteSessions(listOf())
      .test()
      .assertValue(0)
  }

  @Test
  fun testSavePersons() {
    repository.getSessionData(
      dataType = SessionsRepository.SessionDataType.INITIAL
    )
      .flatMap { sessionData ->
        repository.savePersons(sessionData.persons)
      }
      .test()
      .assertValue(2)
  }

  @Test
  fun `Test save persons with no data`() {
    repository.savePersons(listOf())
      .test()
      .assertValue(0)
  }

  @Test
  fun testUpdatePersons() {
    repository.getSessionData(
      dataType = SessionsRepository.SessionDataType.INITIAL
    )
      .flatMap { sessionData ->
        repository.updatePersons(sessionData.persons)
      }
      .test()
      .assertValue(2)
  }

  @Test
  fun `Test update persons with no data`() {
    repository.updatePersons(listOf())
      .test()
      .assertValue(0)
  }

  @Test
  fun testDeletePersons() {
    repository.getSessionData(
      dataType = SessionsRepository.SessionDataType.INITIAL
    )
      .flatMap { sessionData ->
        repository.deletePersons(sessionData.persons)
      }
      .test()
      .assertValue(2)
  }

  @Test
  fun `Test delete persons with no data`() {
    repository.deletePersons(listOf())
      .test()
      .assertValue(0)
  }

  @Test
  fun testClearAllData() {
    repository.clearSessionData().test()
    verify(schedDatabase).clearAllTables()
  }

  private fun SessionResponse.isEqual(session: Session): Boolean =
    id == session.id &&
      name == session.name &&
      description == session.description &&
      startTime == session.startTime &&
      endTime == session.endTime &&
      exhibitors.map { it.id } == session.exhibitors &&
      artists.map { it.id } == session.artists &&
      sponsors.map { it.id } == session.sponsors &&
      speakers.map { it.id } == session.speakers &&
      moderators.map { it.id } == session.moderators
}