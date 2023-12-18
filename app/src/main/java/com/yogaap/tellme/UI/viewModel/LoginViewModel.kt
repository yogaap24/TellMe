package com.yogaap.tellme.UI.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogaap.tellme.Data.database.StoryRepository
import com.yogaap.tellme.Data.di.Event
import com.yogaap.tellme.Data.pref.SessionModel
import com.yogaap.tellme.Response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val loginResponse: LiveData<LoginResponse> = repository.loginResponse
    val toastText: LiveData<Event<String>> = repository.toastText

    fun postLogin(email: String, password: String) {
        viewModelScope.launch {
            repository.postLogin(email, password)
        }
    }

    fun saveSession(session: SessionModel) {
        viewModelScope.launch {
            repository.saveSession(session)
        }
    }

    fun login() {
        viewModelScope.launch {
            repository.login()
        }
    }
}