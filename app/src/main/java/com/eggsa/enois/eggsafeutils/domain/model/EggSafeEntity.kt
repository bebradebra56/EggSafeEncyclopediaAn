package com.eggsa.enois.eggsafeutils.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class EggSafeEntity (
    @SerializedName("ok")
    val ok: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("expires")
    val expires: Int,
)