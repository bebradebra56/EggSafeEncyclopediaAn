package com.eggsa.enois.eggsafeutils.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class EggSafeParam (
    @SerializedName("af_id")
    val afId: String,
    @SerializedName("bundle_id")
    val bundleId: String = "com.eggsa.enois",
    @SerializedName("os")
    val os: String = "Android",
    @SerializedName("store_id")
    val storeId: String = "com.eggsa.enois",
    @SerializedName("locale")
    val locale: String,
    @SerializedName("push_token")
    val pushToken: String,
    @SerializedName("firebase_project_id")
    val firebaseProjectId: String = "eggsafe-encyclopedia",

)