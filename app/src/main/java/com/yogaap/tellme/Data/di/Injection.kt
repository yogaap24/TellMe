package com.yogaap.tellme.Data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yogaap.tellme.Data.database.StoryRepository
import com.yogaap.tellme.Data.pref.SessionPreferences
import com.yogaap.tellme.Data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val preferences = SessionPreferences.getInstance(context.dataStore)
        val user = runBlocking { preferences.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(preferences, apiService)
    }
}