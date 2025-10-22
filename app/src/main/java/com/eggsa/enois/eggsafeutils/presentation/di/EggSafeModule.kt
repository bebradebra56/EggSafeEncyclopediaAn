package com.eggsa.enois.eggsafeutils.presentation.di

import com.eggsa.enois.eggsafeutils.data.repo.EggSafeRepository
import com.eggsa.enois.eggsafeutils.data.shar.EggSafeSharedPreference
import com.eggsa.enois.eggsafeutils.data.utils.EggSafePushToken
import com.eggsa.enois.eggsafeutils.data.utils.EggSafeSystemService
import com.eggsa.enois.eggsafeutils.domain.usecases.EggSafeGetAllUseCase
import com.eggsa.enois.eggsafeutils.presentation.pushhandler.EggSafePushHandler
import com.eggsa.enois.eggsafeutils.presentation.ui.load.EggSafeLoadViewModel
import com.eggsa.enois.eggsafeutils.presentation.ui.view.EggSafeViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val eggSafeModule = module {
    factory {
        EggSafePushHandler()
    }
    single {
        EggSafeRepository()
    }
    single {
        EggSafeSharedPreference(get())
    }
    factory {
        EggSafePushToken()
    }
    factory {
        EggSafeSystemService(get())
    }
    factory {
        EggSafeGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        EggSafeViFun(get())
    }
    viewModel {
        EggSafeLoadViewModel(get(), get(), get())
    }
}