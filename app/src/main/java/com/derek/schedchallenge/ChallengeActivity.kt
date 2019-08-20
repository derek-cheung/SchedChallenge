package com.derek.schedchallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.derek.schedchallenge.repositories.SessionsRepository
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChallengeActivity : AppCompatActivity() {

  private val viewModel: ChallengeViewModel by viewModel()

  private val disposables = CompositeDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    dataTypeSelectionGroup.check(R.id.initialJsonSelection)
    observeImportButtonClicks()
    observeClearButtonClicks()
    observeDumpSessionButtonClicks()
    observeDumpPersonButtonClicks()
    observeImportDataResults()
    observeDataClearedEvents()
    observeDataDumpEvents()
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
