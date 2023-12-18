package com.yogaap.tellme.Data.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.yogaap.tellme.Data.di.Event
import com.yogaap.tellme.Data.pref.SessionModel
import com.yogaap.tellme.Data.pref.SessionPreferences
import com.yogaap.tellme.Data.retrofit.ApiService
import com.yogaap.tellme.Response.AddStoryResponse
import com.yogaap.tellme.Response.ListStoryItem
import com.yogaap.tellme.Response.LoginResponse
import com.yogaap.tellme.Response.RegisterResponse
import com.yogaap.tellme.Response.StoriesResponse
import com.yogaap.tellme.UI.View.StoryPagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val pref: SessionPreferences,
    private val apiService: ApiService
) {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _uploadResponse = MutableLiveData<AddStoryResponse>()
    val uploadResponse: LiveData<AddStoryResponse> = _uploadResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _list = MutableLiveData<StoriesResponse>()
    val list: LiveData<StoriesResponse> = _list

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun postRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.postRegister(name, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _registerResponse.value = response.body()
                    _toastText.value = Event(response.body()?.message.toString())
                } else {
                    _toastText.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.postLogin(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _loginResponse.value = response.body()
                    _toastText.value = Event(response.body()?.message.toString())
                } else {
                    _toastText.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(pref, apiService)
            }
        ).liveData
    }

    fun getListStoriesWithLocation(token: String) {
        _isLoading.value = true
        val client = apiService.getListStoriesWithLocation(token)
        Log.d(TAG, "getListStoriesWithLocation: $token")

        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val allStories = response.body()?.listStory
                    if (allStories != null) {
                        val storiesWithLocation = allStories.filter { it.lat != null && it.lon != null }
                        _list.value = StoriesResponse(
                            error = response.body()?.error ?: false,
                            message = response.body()?.message ?: "",
                            listStory = storiesWithLocation
                        )
                    }
                } else if (response.code() == 401) {
                    _toastText.value = Event("Unauthorized: Please check your credentials.")
                } else {
                    _toastText.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ) {
        _isLoading.value = true
        val client = apiService.postStory(token, file, description, lat, lon)

        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _uploadResponse.value = response.body()
                    _toastText.value = Event(response.body()?.message.toString())
                } else {
                    _toastText.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "onFailure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                Log.d("error upload", t.message.toString())
            }

        })
    }

    fun getSession(): LiveData<SessionModel> {
        return pref.getSession().asLiveData()
    }

    suspend fun saveSession(session: SessionModel) {
        pref.saveSession(session)
    }

    suspend fun login() {
        pref.login()
    }

    suspend fun logout() {
        pref.logout()
    }

    companion object {
        private const val TAG = "StoryRepository"

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            preferences: SessionPreferences,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(preferences, apiService)
            }.also { instance = it }
    }
}
