package com.clabs.caddyapp.network

import com.example.soundrecorder.utils.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface ApiInterface {


    @POST(SIGN_UP)
    @JvmSuppressWildcards
    fun userSignup(@Body body: Map<String, Any>): Call<Any>

    @POST(GET_ALL_USER)
    @JvmSuppressWildcards
    fun getAllUser(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>

    @POST(UPLOAD_RESULT)
    @JvmSuppressWildcards
    fun uploadResult(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>

    @POST(GET_DATA_ANALYSIS)
    @JvmSuppressWildcards
    fun getDataAnalysis(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>

    @POST(GET_DATA_ANALYSIS_ONE)
    @JvmSuppressWildcards
    fun getDataAnalysisOne(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>

    @POST(VIEW_LISTING)
    @JvmSuppressWildcards
    fun viewListing(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>


    @POST(GET_ALL_STATE)
    @JvmSuppressWildcards
    fun getAllState(@Body body: Map<String, Any>): Call<Any>

    @GET(GET_BULLETS)
    @JvmSuppressWildcards
    fun getBullets(): Call<Any>

    @GET(GET_ALL_COUNTRIES)
    @JvmSuppressWildcards
    fun getAllCountries(): Call<Any>

    @POST(LOGIN)
    fun userLogin(@Body body: Map<String, String>): Call<Any>


    @POST(GET_CALCULATION)
    @JvmSuppressWildcards
    fun getCalculation(@Body body: Map<String, Double>, @Header("Authorization") authHeader: String): Call<Any>

    @GET(GEL_ALL_OPTIONS)
    @JvmSuppressWildcards
    fun getAllOptions(@Header("Authorization") authHeader: String): Call<Any>

    @POST(FORGOT_PASSWORD)
    fun forgotPassword(@Body body: Map<String, String>): Call<Any>

    @POST(VIEW_LISTING_ONE)
    @JvmSuppressWildcards
    fun viewListingOne(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>

    @POST(VIEW_LISTING_THREE)
    @JvmSuppressWildcards
    fun viewListingThree(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>

    @POST(UPDATE_PROFILE)
    @JvmSuppressWildcards
    fun updateProfile(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>

    @POST(SEND_PAYMENT)
    @JvmSuppressWildcards
    fun sendPayment(@Body body: Map<String, Any>, @Header("Authorization") authHeader: String): Call<Any>


}