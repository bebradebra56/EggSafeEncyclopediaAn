package com.eggsa.enois.eggsafeutils.data.repo

import android.util.Log
import com.eggsa.enois.eggsafeutils.domain.model.EggSafeEntity
import com.eggsa.enois.eggsafeutils.domain.model.EggSafeParam
import com.eggsa.enois.eggsafeutils.presentation.app.EggSafeApp.Companion.EGGSAFE_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EggSafeApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun getClient(
        @Body jsonString: JsonObject,
    ): Call<EggSafeEntity>
}


private const val EGGSAFE_MAIN = "https://eggsafeencyclopedia.com/"
class EggSafeRepository {
//    private val ktorClient = HttpClient(OkHttp){
//        install(ContentNegotiation) {
//            json(Json {
//                ignoreUnknownKeys = true
//            })
//        }
//        install(HttpTimeout) {
//            connectTimeoutMillis = 30000
//            socketTimeoutMillis = 30000
//            requestTimeoutMillis = 30000
//        }
////        install(DefaultRequest) {
////            header("User-Agent", System.getProperty("http.agent") ?: "")
////        }
//
//    }

    suspend fun getClient(
        param: EggSafeParam,
        conversion: MutableMap<String, Any>?
    ): EggSafeEntity? {
        val gson = Gson()
        val api = getApi(EGGSAFE_MAIN, null)

        val jsonObject = gson.toJsonTree(param).asJsonObject
        conversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            jsonObject.add(key, element)
        }
        return try {
            val request: Call<EggSafeEntity> = api.getClient(
                jsonString = jsonObject,
            )
            Log.d(
                EGGSAFE_MAIN_TAG,
                "Retrofit: Request URL = ${(request.request() as Request).url}"
            )
            Log.d(
                EGGSAFE_MAIN_TAG,
                "Retrofit: Request Body = ${(request.request() as Request).body}"
            )
            val result = request.awaitResponse()
            Log.d(EGGSAFE_MAIN_TAG, "Retrofit: Result code: ${result.code()}")
            if (result.code() == 200) {
                Log.d(EGGSAFE_MAIN_TAG, "Retrofit: Get request success")
                Log.d(EGGSAFE_MAIN_TAG, "Retrofit: Code = ${result.code()}")
                Log.d(EGGSAFE_MAIN_TAG, "Retrofit: ${result.body()}")
                result.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(EGGSAFE_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(EGGSAFE_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


//    suspend fun get(param: EggSafeParam, conversion: MutableMap<String, Any>?) : EggSafeEntity? = withContext(Dispatchers.IO) {
//        ktorClient.plugin(HttpSend).intercept { request ->
//            Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Ktor: Intercept body ${request.body}")
//            execute(request)
//        }
//        Log.d(EggSafeApp.EGGSAFE_MAIN_TAG, "Ktor: conversation json: ${conversion.toString()}")
//        return@withContext try {
//            val response = ktorClient.post(EGGSAFE_MAIN) {
//                contentType(ContentType.Application.Json)
//                setBody(
//                    param
//                )
//                setBody(
//                    conversion.toString()
//                )
//            }
//            val code = response.status.value
//            Log.d(EGGSAFE_MAIN_TAG, "Ktor: Request status code: $code")
//            if (code == 200) {
//                val body = response.body<EggSafeEntity>()
//                Log.d(EGGSAFE_MAIN_TAG, "Ktor: Get request success")
//                Log.d(EGGSAFE_MAIN_TAG, "Ktor: ${response.body<EggSafeEntity>()}")
//                body
//            } else {
//                Log.d(EGGSAFE_MAIN_TAG, "Ktor: Status code invalid, return null")
//                Log.d(EGGSAFE_MAIN_TAG, "Ktor: ${response.body<String>()}")
//                null
//            }
//
//        } catch (e: Exception) {
//            Log.d(EGGSAFE_MAIN_TAG, "Ktor: Get request failed")
//            Log.d(EGGSAFE_MAIN_TAG, "Ktor: ${e.message}")
//            null
//        }
//    }

    private fun getApi(url: String, client: OkHttpClient?) : EggSafeApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
