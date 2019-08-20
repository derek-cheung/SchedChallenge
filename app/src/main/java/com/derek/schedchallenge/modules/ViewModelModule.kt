package com.derek.schedchallenge.modules

import com.derek.schedchallenge.ChallengeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {

  val viewModelModule = module {
    viewModel { ChallengeViewModel(get(), get()) }
  }
}