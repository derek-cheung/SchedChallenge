package com.derek.schedchallenge

import com.derek.schedchallenge.models.Person
import com.derek.schedchallenge.models.Session
import com.derek.schedchallenge.repositories.SessionsRepository
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test

class ChallengeViewModelTest {

  @get:Rule
  val schedulers = RxSchedulerTestRule()

  private val logger = mock<Logger> {
    on { logSessions(any()) } doReturn "Dump sessions"
    on { logPersons(any()) } doReturn "Dump persons"
  }

  private val sessionsRepository = mock<SessionsRepository>()

  private val persons = listOf(
    Person(
      id = "1",
      name = "Person1"
    ),

    Person(
      id = "2",
      name = "Person2"
    )
  )

  private val sessions = listOf(
    Session(
      id = "Session1",
      name = "FirstSession",
      description = "The first",
      speakers = listOf(persons[0].id),
      exhibitors = listOf(persons[0].id),
      moderators = listOf(persons[0].id, persons[1].id),
      artists = listOf(persons[0].id, persons[1].id),
      sponsors = listOf(persons[0].id, persons[1].id),
      startTime = 10,
      endTime = 30
    ),

    Session(
      id = "Session2",
      name = "SecondSession",
      description = "The second",
      speakers = listOf(persons[0].id),
      exhibitors = listOf(persons[1].id),
      moderators = listOf(persons[1].id, persons[0].id),
      artists = listOf(),
      sponsors = listOf(persons[0].id),
      startTime = 50,
      endTime = 300
    )
  )

  private val sessionData = SessionsRepository.SessionData(
    sessions = sessions,
    persons = persons
  )

  private val viewModel by lazy {
    ChallengeViewModel(
      logger = logger,
      sessionsRepository = sessionsRepository
    )
  }

  @Test
  fun `Test import session data with no stored data`() {
    whenever(sessionsRepository.getSessionData(any())).doReturn(Single.just(sessionData))
    whenever(sessionsRepository.getAllSessions()).doReturn(Single.just(listOf()))
    whenever(sessionsRepository.getAllPersons()).doReturn(Single.just(listOf()))

    whenever(sessionsRepository.saveSessions(any())).doReturn(Single.just(sessions.size))
    whenever(sessionsRepository.deleteSessions(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.updateSessions(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.savePersons(any())).doReturn(Single.just(persons.size))
    whenever(sessionsRepository.deletePersons(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.updatePersons(any())).doReturn(Single.just(0))

    val testObserver = viewModel.observeImportDataResults().test()

    viewModel.importSessionData(SessionsRepository.SessionDataType.INITIAL)

    verify(sessionsRepository).getSessionData(SessionsRepository.SessionDataType.INITIAL)
    verify(sessionsRepository).saveSessions(sessions)
    verify(sessionsRepository).deleteSessions(listOf())
    verify(sessionsRepository).updateSessions(listOf())
    verify(sessionsRepository).savePersons(persons)
    verify(sessionsRepository).deletePersons(listOf())
    verify(sessionsRepository).updatePersons(listOf())

    testObserver
      .assertValue { importDataResults ->
        importDataResults.numberOfInsertedResults == sessions.size &&
        importDataResults.numberOfDeletedResults == 0 &&
        importDataResults.numberOfUpdatedResults == 0
      }
  }

  @Test
  fun `Test import session data with changed data`() {
    val changedData = sessionData.copy(
      sessions = listOf(
        sessions[0],
        sessions[1].copy(
          description = "Changed"
        )
      ),

      persons = listOf(
        persons[0],
        persons[1].copy(
          name = "Changed"
        )
      )
    )

    whenever(sessionsRepository.getSessionData(any())).doReturn(Single.just(changedData))
    whenever(sessionsRepository.getAllSessions()).doReturn(Single.just(sessions))
    whenever(sessionsRepository.getAllPersons()).doReturn(Single.just(persons))

    whenever(sessionsRepository.saveSessions(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.deleteSessions(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.updateSessions(any())).doReturn(Single.just(1))
    whenever(sessionsRepository.savePersons(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.deletePersons(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.updatePersons(any())).doReturn(Single.just(1))

    val testObserver = viewModel.observeImportDataResults().test()

    viewModel.importSessionData(SessionsRepository.SessionDataType.EDITED)

    verify(sessionsRepository).getSessionData(SessionsRepository.SessionDataType.EDITED)
    verify(sessionsRepository).saveSessions(listOf())
    verify(sessionsRepository).deleteSessions(listOf())
    verify(sessionsRepository).updateSessions(listOf(changedData.sessions[1]))
    verify(sessionsRepository).savePersons(listOf())
    verify(sessionsRepository).deletePersons(listOf())
    verify(sessionsRepository).updatePersons(listOf(changedData.persons[1]))

    testObserver
      .assertValue { importDataResults ->
        importDataResults.numberOfInsertedResults == 0 &&
        importDataResults.numberOfDeletedResults == 0 &&
        importDataResults.numberOfUpdatedResults == 1
      }
  }

  @Test
  fun `Test import session data with deleted data`() {
    val deletedData = sessionData.copy(
      sessions = listOf(
        sessions[1]
      ),

      persons = listOf(
        persons[1]
      )
    )

    whenever(sessionsRepository.getSessionData(any())).doReturn(Single.just(deletedData))
    whenever(sessionsRepository.getAllSessions()).doReturn(Single.just(sessions))
    whenever(sessionsRepository.getAllPersons()).doReturn(Single.just(persons))

    whenever(sessionsRepository.saveSessions(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.deleteSessions(any())).doReturn(Single.just(1))
    whenever(sessionsRepository.updateSessions(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.savePersons(any())).doReturn(Single.just(0))
    whenever(sessionsRepository.deletePersons(any())).doReturn(Single.just(1))
    whenever(sessionsRepository.updatePersons(any())).doReturn(Single.just(0))

    val testObserver = viewModel.observeImportDataResults().test()

    viewModel.importSessionData(SessionsRepository.SessionDataType.DELETED)

    verify(sessionsRepository).getSessionData(SessionsRepository.SessionDataType.DELETED)
    verify(sessionsRepository).saveSessions(listOf())
    verify(sessionsRepository).deleteSessions(listOf(sessions[0]))
    verify(sessionsRepository).updateSessions(listOf())
    verify(sessionsRepository).savePersons(listOf())
    verify(sessionsRepository).deletePersons(listOf(persons[0]))
    verify(sessionsRepository).updatePersons(listOf())

    testObserver
      .assertValue { importDataResults ->
        importDataResults.numberOfInsertedResults == 0 &&
          importDataResults.numberOfDeletedResults == 1 &&
          importDataResults.numberOfUpdatedResults == 0
      }
  }

  @Test
  fun testClearSessionData() {
    whenever(sessionsRepository.clearSessionData()).doReturn(Completable.complete())
    val testObserver = viewModel.observeDataClearedEvents().test()
    viewModel.clearSessionData()
    testObserver.assertValueCount(1)
  }

  @Test
  fun testDumpSessionData() {
    whenever(sessionsRepository.getAllSessions()).doReturn(Single.just(sessions))
    val testObserver = viewModel.observeDataDumpEvents().test()
    viewModel.dumpSessionData()

    verify(logger).logSessions(sessions)

    testObserver
      .assertValue("Dump sessions")
  }

  @Test
  fun testDumpPersonsData() {
    whenever(sessionsRepository.getAllPersons()).doReturn(Single.just(persons))
    val testObserver = viewModel.observeDataDumpEvents().test()
    viewModel.dumpPersonData()

    verify(logger).logPersons(persons)

    testObserver
      .assertValue("Dump persons")
  }
}