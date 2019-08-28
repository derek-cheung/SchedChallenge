package com.derek.schedchallenge

import androidx.lifecycle.ViewModel
import com.derek.schedchallenge.models.Person
import com.derek.schedchallenge.models.Session
import com.derek.schedchallenge.repositories.SessionsRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.functions.Function6
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ChallengeViewModel(
  private val logger: Logger,
  private val sessionsRepository: SessionsRepository
) : ViewModel() {

  data class ImportDataResults(
    val numberOfInsertedResults: Int,
    val numberOfDeletedResults: Int,
    val numberOfUpdatedResults: Int
  )

  private val disposables = CompositeDisposable()

  private val sessions = BehaviorSubject.create<List<Session>>()
  private val importDataResults = PublishSubject.create<ImportDataResults>()
  private val dataClearedEvents = PublishSubject.create<Unit>()
  private val dataDumpEvents = PublishSubject.create<String>()

  private var allSessions: List<Session> = listOf()

  override fun onCleared() {
    disposables.dispose()
    super.onCleared()
  }

  fun observeSessions(): Observable<List<Session>> = sessions.hide()
  fun observeImportDataResults(): Observable<ImportDataResults> = importDataResults.hide()
  fun observeDataClearedEvents(): Observable<Unit> = dataClearedEvents.hide()
  fun observeDataDumpEvents(): Observable<String> = dataDumpEvents.hide()

  fun importSessionData(
    sessionDataType: SessionsRepository.SessionDataType
  ) {
    disposables.add(Single.zip(
      sessionsRepository.getSessionData(sessionDataType),
      sessionsRepository.getAllSessions(),
      sessionsRepository.getAllPersons(),
      Function3 { sessionData: SessionsRepository.SessionData, sessions: List<Session>, persons: List<Person> ->
        Triple(sessionData, sessions, persons)
      }
    )
      .flatMap { (sessionData, sessions, persons) ->
        val savedSessionsIntersect = sessions.intersect(sessionData.sessions).toList()
        val newSessionsIntersect = sessionData.sessions.intersect(savedSessionsIntersect).toList()
        val changedSessions = savedSessionsIntersect.calculateChangedSessions(newSessionsIntersect)
        val addedSessions = sessionData.sessions.minus(sessions)
        val deletedSessions = sessions.minus(sessionData.sessions)

        val savedPersonsIntersect = persons.intersect(sessionData.persons).toList()
        val newPersonsIntersect = sessionData.persons.intersect(savedPersonsIntersect).toList()
        val changedPersons = savedPersonsIntersect.calculateChangedPersons(newPersonsIntersect)
        val addedPersons = sessionData.persons.minus(persons)
        val deletedPersons = persons.minus(sessionData.persons)

        Single.zip(
          sessionsRepository.saveSessions(addedSessions),
          sessionsRepository.deleteSessions(deletedSessions),
          sessionsRepository.updateSessions(changedSessions),
          sessionsRepository.savePersons(addedPersons),
          sessionsRepository.deletePersons(deletedPersons),
          sessionsRepository.updatePersons(changedPersons),
          Function6 { inserts: Int, deletes: Int, updates: Int, _: Int, _: Int, _: Int ->
            sessionData.sessions to ImportDataResults(
              numberOfInsertedResults = inserts,
              numberOfDeletedResults = deletes,
              numberOfUpdatedResults = updates
            )
          }
        )
      }
      .subscribeOn(Schedulers.io())
      .subscribe { (sessions, results) ->
        this.sessions.onNext(sessions)
        this.allSessions = sessions
        importDataResults.onNext(results)
      }
    )
  }

  fun clearSessionData() {
    disposables.add(sessionsRepository.clearSessionData()
      .subscribeOn(Schedulers.io())
      .subscribe { dataClearedEvents.onNext(Unit) }
    )
  }

  fun dumpSessionData() {
    disposables.add(sessionsRepository.getAllSessions()
      .subscribeOn(Schedulers.io())
      .subscribe { sessions ->
        dataDumpEvents.onNext(
          logger.logSessions(
            sessions = sessions
          )
        )
      }
    )
  }

  fun search(input: String) {
    disposables.add(sessionsRepository
      .getAllSessionsForInput(input)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe { filteredSessions ->
        this.sessions.onNext(filteredSessions)
      })
  }

  fun dumpPersonData() {
    disposables.add(sessionsRepository.getAllPersons()
      .subscribeOn(Schedulers.io())
      .subscribe { persons ->
        dataDumpEvents.onNext(
          logger.logPersons(
            persons = persons
          )
        )
      }
    )
  }

  private fun List<Session>.calculateChangedSessions(
    newSessions: List<Session>
  ): List<Session> =
    newSessions.filter { newSession ->
      find { session ->
        session == newSession &&
        (session.name != newSession.name ||
        session.description != newSession.description ||
        session.startTime != newSession.startTime ||
        session.endTime != newSession.endTime ||
        session.artists != newSession.artists ||
        session.exhibitors != newSession.exhibitors ||
        session.moderators != newSession.moderators ||
        session.speakers != newSession.speakers ||
        session.sponsors != newSession.sponsors)
      } != null
    }

  private fun List<Person>.calculateChangedPersons(
    newPersons: List<Person>
  ): List<Person> =
    newPersons.filter { newPerson ->
      find { person ->
        person == newPerson &&
        person.name != newPerson.name
      } != null
    }
}