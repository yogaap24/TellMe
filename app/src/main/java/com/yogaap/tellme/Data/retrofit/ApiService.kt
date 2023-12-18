package com.yogaap.tellme.Data.retrofit

import com.yogaap.tellme.Response.AddStoryResponse
import com.yogaap.tellme.Response.LoginResponse
import com.yogaap.tellme.Response.RegisterResponse
import com.yogaap.tellme.Response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    // Register
    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    // Login
    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // Get List Stories
    @GET("stories")
     suspend fun getListStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
     ): Response<StoriesResponse>

    // Get List Stories With Location
    @GET("stories")
    fun getListStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") loc: Int = 1
    ): Call<StoriesResponse>

    // Post Story
    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<AddStoryResponse>
}