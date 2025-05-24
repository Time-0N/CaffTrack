package ch.timeon.cafftrack.repository

import okhttp3.OkHttpClient
import okhttp3.Request
import ch.timeon.cafftrack.model.dao.CaffeineEntryDao
import ch.timeon.cafftrack.model.dto.LookupResult
import ch.timeon.cafftrack.model.entity.CaffeineEntryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import ch.timeon.cafftrack.BuildConfig
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext


@Singleton
class CaffeineRepository @Inject constructor(
    private val dao: CaffeineEntryDao,
    @ApplicationContext private val context: Context

) {
    private val httpClient = OkHttpClient()

    private val fallbackMap: Map<String, Pair<String, Int>> by lazy {
        context.assets.open("caffeine_fallback.json").bufferedReader().use { reader ->
            val root = JSONObject(reader.readText())
            root.keys().asSequence()
                .toList()
                .associateWith { key ->
                    val o = root.getJSONObject(key)
                    o.getString("name") to o.getInt("mg")
                }
        }
    }

    suspend fun lookupCaffeineFromUpc(upc: String): LookupResult =
        withContext(Dispatchers.IO) {
            val edamamResult = try {
                lookupViaEdamam(upc)
            } catch (e: Exception) {
                null
            }

            if (edamamResult is LookupResult.Success || edamamResult == LookupResult.NoCaffeine) {
                return@withContext edamamResult
            }

            fallbackMap[upc]?.let { (name, mg) ->
                return@withContext LookupResult.Success(
                    CaffeineEntryEntity(name = name, caffeineMg = mg)
                )
            }

            LookupResult.NotFound
        }

    private fun lookupViaEdamam(upc: String): LookupResult? {
        val parserUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.edamam.com")
            .addPathSegments("api/food-database/v2/parser")
            .addQueryParameter("upc", upc)
            .addQueryParameter("app_id", BuildConfig.EDAMAM_APP_ID)
            .addQueryParameter("app_key", BuildConfig.EDAMAM_APP_KEY)
            .addQueryParameter("nutrition-type", "logging")
            .build()

        val parserResp = httpClient.newCall(Request.Builder().url(parserUrl).get().build()).execute()
        if (!parserResp.isSuccessful) return null

        val hints = JSONObject(parserResp.body!!.string())
            .optJSONArray("hints") ?: return null
        if (hints.length() == 0) return null

        val foodUri = hints
            .getJSONObject(0)
            .getJSONObject("food")
            .optString("uri", null) ?: return null

        val nutrUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.edamam.com")
            .addPathSegments("api/food-database/v2/nutrients")
            .addQueryParameter("app_id", BuildConfig.EDAMAM_APP_ID)
            .addQueryParameter("app_key", BuildConfig.EDAMAM_APP_KEY)
            .build()

        val nutrBody = JSONObject().apply {
            put("ingredients", listOf(
                mapOf(
                    "quantity" to 1,
                    "measureURI" to "http://www.edamam.com/ontologies/edamam.owl#Measure_serving",
                    "foodURI" to foodUri
                )
            ))
        }.toString().toRequestBody("application/json".toMediaType())

        val nutrResp = httpClient.newCall(Request.Builder().url(nutrUrl).post(nutrBody).build()).execute()
        if (!nutrResp.isSuccessful) return LookupResult.NoCaffeine

        val totalN = JSONObject(nutrResp.body!!.string()).optJSONObject("totalNutrients") ?: return LookupResult.NoCaffeine
        val caff   = totalN.optJSONObject("CAFFEI") ?: return LookupResult.NoCaffeine

        val mg = caff.optDouble("quantity", 0.0).roundToInt()
        if (mg <= 0) return LookupResult.NoCaffeine

        val label = caff.optString("label", "Scanned item")
        return LookupResult.Success(CaffeineEntryEntity(name = label, caffeineMg = mg))
    }

    fun getAllEntries(): Flow<List<CaffeineEntryEntity>> = dao.getAll()

    suspend fun insert(entry: CaffeineEntryEntity) = dao.insert(entry)

    suspend fun delete(entry: CaffeineEntryEntity) = dao.delete(entry)
}