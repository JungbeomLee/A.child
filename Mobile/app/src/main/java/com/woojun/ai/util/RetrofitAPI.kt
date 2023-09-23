package com.woojun.ai.util

import com.woojun.ai.BuildConfig
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RetrofitAPI {
    @GET(BuildConfig.GETURL)
    fun getAiResult(
        @Query("esntlId") esntlId: String,
        @Query("authKey") authKey: String,
        @Query("rowSize") rowSize: Int,
        @Query("nm") nm: String?,
        @Query("age1") age1: Int?,
        @Query("age2") age2: Int?
    ): Call<AiResultList>

    @GET("v2/local/search/address")
    fun getSearchKeyword(
        @Header("Authorization") key: String,
        @Query("query") query: String

    ): Call<ResultSearchKeyword>

    @Multipart
    @POST(BuildConfig.POSTURL1)
    fun setChildImage(
        @Part FixImage: MultipartBody.Part,
        @Part("id") id: String
    ): Call<String>

    @Multipart
    @POST(BuildConfig.POSTURL2)
    fun findChildImage(
        @Part FixImage: MultipartBody.Part,
    ): Call<FindChildImageResult>

    @POST(BuildConfig.POSTURL3)
    fun deleteChildImage(
        @Part("id") id: String
    ): Call<String>

}