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


@Singleton
class CaffeineRepository @Inject constructor(
    private val dao: CaffeineEntryDao
) {
    private val httpClient = OkHttpClient()

    fun getAllEntries(): Flow<List<CaffeineEntryEntity>> = dao.getAll()

    suspend fun insert(entry: CaffeineEntryEntity) = dao.insert(entry)

    suspend fun delete(entry: CaffeineEntryEntity) = dao.delete(entry)

    /**
     * Fetches product info for this UPC from OpenFoodFacts,
     * parses out caffeine_100g + serving size, computes mg,
     * and returns a CaffeineEntryEntity ready for insertion.
     */
    suspend fun lookupCaffeineFromUpc(upc: String): LookupResult =
        withContext(Dispatchers.IO) {
            // ─── Step 1: Resolve the food URI ────────────────────
            val parserUrl = HttpUrl.Builder()
                .scheme("https")
                .host("api.edamam.com")
                .addPathSegments("api/food-database/v2/parser")
                .addQueryParameter("upc", upc)
                .addQueryParameter("app_id", BuildConfig.EDAMAM_APP_ID)
                .addQueryParameter("app_key", BuildConfig.EDAMAM_APP_KEY)
                .build()

            val parserReq = Request.Builder()
                .url(parserUrl)
                .get()
                .build()

            val parserResp = httpClient.newCall(parserReq).execute()
            if (!parserResp.isSuccessful) return@withContext LookupResult.NotFound

            val parserJson = JSONObject(parserResp.body!!.string())
            val hints     = parserJson.optJSONArray("hints") ?: return@withContext LookupResult.NotFound
            if (hints.length() == 0) return@withContext LookupResult.NotFound

            val foodUri = hints
                .getJSONObject(0)
                .getJSONObject("food")
                .optString("uri", null)
                ?: return@withContext LookupResult.NotFound

            // ─── Step 2: Fetch nutrient details ─────────────────
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

            val nutrReq = Request.Builder()
                .url(nutrUrl)
                .post(nutrBody)
                .build()

            val nutrResp = httpClient.newCall(nutrReq).execute()
            if (!nutrResp.isSuccessful) return@withContext LookupResult.NoCaffeine

            val nutrJson = JSONObject(nutrResp.body!!.string())
            val totalN  = nutrJson.optJSONObject("totalNutrients") ?: JSONObject()
            val caff    = totalN.optJSONObject("CAFFEI") ?: return@withContext LookupResult.NoCaffeine

            val mg = caff.optDouble("quantity", 0.0).roundToInt()
            if (mg <= 0) return@withContext LookupResult.NoCaffeine

            val nameLabel = caff.optString("label", "Scanned item")
            LookupResult.Success(CaffeineEntryEntity(name = nameLabel, caffeineMg = mg))
        }
}