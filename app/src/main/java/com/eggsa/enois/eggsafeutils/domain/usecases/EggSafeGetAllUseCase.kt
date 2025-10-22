package com.eggsa.enois.eggsafeutils.domain.usecases

import android.util.Log
import com.eggsa.enois.EggSafeApp
import com.eggsa.enois.eggsafeutils.data.repo.EggSafeRepository
import com.eggsa.enois.eggsafeutils.data.utils.EggSafePushToken
import com.eggsa.enois.eggsafeutils.data.utils.EggSafeSystemService
import com.eggsa.enois.eggsafeutils.domain.model.EggSafeEntity
import com.eggsa.enois.eggsafeutils.domain.model.EggSafeParam
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp
import kotlinx.coroutines.flow.collect

class EggSafeGetAllUseCase(
    private val repository: EggSafeRepository,
    private val systemService: EggSafeSystemService,
    private val pushToken: EggSafePushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : EggSafeEntity?{
        val params = EggSafeParam(
            locale = systemService.getLocale(),
            pushToken = pushToken.getToken(),
            afId = systemService.getAppsflyerId()
        )
        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Params for request: $params")
        return repository.getClient(params, conversion)
    }



}