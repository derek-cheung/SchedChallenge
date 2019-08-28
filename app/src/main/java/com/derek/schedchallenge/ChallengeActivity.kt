package com.derek.schedchallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.derek.schedchallenge.db.SchedDatabase
import com.derek.schedchallenge.repositories.SessionDataImporter
import com.derek.schedchallenge.repositories.SessionsRepository
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class ChallengeActivity : AppCompatActivity() {

  private lateinit var viewModel: ChallengeViewModel

  private val disposables = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val database = Room.databaseBuilder(this, SchedDatabase::class.java, "sched-db").build()

    viewModel = ChallengeViewModel(
      logger = Logger(
        Moshi.Builder()
          .add(KotlinJsonAdapterFactory())
          .build()
      ),

      sessionsRepository = SessionsRepository(
        schedDatabase = database,
        sessionDao = database.sessionDao(),
        personDao = database.personDao(),
        sessionDataImporter = SessionDataImporter(
          context = this,
          moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        )
      )
    )
    setContentView(R.layout.activity_main)

    dataTypeSelectionGroup.check(R.id.initialJsonSelection)
    observeImportButtonClicks()
    observeClearButtonClicks()
    observeDumpSessionButtonClicks()
    observeDumpPersonButtonClicks()
    observeImportDataResults()
    observeDataClearedEvents()
    observeDataDumpEvents()
    observeSearchInput()
    observeSessions()
  }

  override fun onDestroy() {
    disposables.dispose()
    super.onDestroy()
  }

  private fun observeImportButtonClicks() {
    disposables.add(
      buttonImport.clicks()
        .subscribe {
          val dataType = when (dataTypeSelectionGroup.checkedRadioButtonId) {
            R.id.initialJsonSelection -> SessionsRepository.SessionDataType.INITIAL
            R.id.editedJsonSelection -> SessionsRepository.SessionDataType.EDITED
            R.id.deletedJsonSelection -> SessionsRepository.SessionDataType.DELETED
            else -> SessionsRepository.SessionDataType.INITIAL
          }

          viewModel.importSessionData(
            sessionDataType = dataType
          )
        }
    )
  }

  private fun observeClearButtonClicks() {
    disposables.add(
      buttonClear.clicks()
        .subscribe { viewModel.clearSessionData() }
    )
  }

  private fun observeDumpSessionButtonClicks() {
    disposables.add(
      buttonSessionDump.clicks()
        .subscribe { viewModel.dumpSessionData() }
    )
  }

  private fun observeDumpPersonButtonClicks() {
    disposables.add(
      buttonPersonDump.clicks()
        .subscribe { viewModel.dumpPersonData() }
    )
  }

  private fun observeSessions() {
    disposables.add(
      viewModel.observeSessions()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { sessions ->
          if (sessions.isNotEmpty()) {
            val adapter = SessionAdapter(sessions)
            listSessions.adapter = adapter
            listSessions.layoutManager = LinearLayoutManager(
              this,
              RecyclerView.VERTICAL,
              false
            )

            dataTypeSelectionGroup.isVisible = false
            buttonClear.isVisible = false
            buttonImport.isVisible = false
            buttonPersonDump.isVisible = false
            buttonSessionDump.isVisible = false
          }
        }
    )
  }

  private fun observeSearchInput() {
    disposables.add(
      searchBar.textChanges()
        .subscribe { text ->
          viewModel.search(text.toString())
        }
    )
  }

  private fun observeImportDataResults() {
    disposables.add(
      viewModel.observeImportDataResults()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { importDataResults ->
          textResults.text =
            "Inserted: ${importDataResults.numberOfInsertedResults}\n" +
            "Deleted: ${importDataResults.numberOfDeletedResults}\n" +
            "Updated: ${importDataResults.numberOfUpdatedResults}"
        }
    )
  }

  private fun observeDataClearedEvents() {
    disposables.add(
      viewModel.observeDataClearedEvents()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { textResults.text = "Data cleared" }
    )
  }

  private fun observeDataDumpEvents() {
    disposables.add(
      viewModel.observeDataDumpEvents()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { dataDump ->
          textResults.text = dataDump
        }
    )
  }
}
