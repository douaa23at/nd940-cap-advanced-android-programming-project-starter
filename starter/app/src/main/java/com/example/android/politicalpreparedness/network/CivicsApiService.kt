package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

private const val BASE_URL = "https://www.googleapis.com/"

// TODO: Add adapters for Java Date and custom adapter ElectionAdapter (included in project)
class CustomDateAdapter : JsonAdapter<Date>() {
    private val dateFormat = SimpleDateFormat(SERVER_FORMAT, Locale.getDefault())

    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        return try {
            val dateAsString = reader.nextString()
            dateFormat.parse(dateAsString)
        } catch (e: Exception) {
            null
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            writer.value(value.toString())
        }
    }

    companion object {
        const val SERVER_FORMAT = ("yyyy-MM-dd") // define your server format here
    }
}

private val moshi = Moshi.Builder()
        .add(ElectionAdapter())
        .add(CustomDateAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(CivicsHttpClient.getClient())
        .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {

    @GET("/civicinfo/v2/elections")
    fun getElections(): Deferred<ElectionResponse>

    @GET("/civicinfo/v2/voterinfo")
    fun getVoterInfo(@Query("address") address: String, @Query("electionId") electionId: Int): Deferred<VoterInfoResponse>

    @GET("/civicinfo/v2/representatives")
    fun getRepresentatives(@Query("address") address: Address,
                           @Query("includeOffices") includeOffice: Boolean): Deferred<RepresentativeResponse>

}

object CivicsApi {
    val retrofitService = retrofit.create(CivicsApiService::class.java)
}