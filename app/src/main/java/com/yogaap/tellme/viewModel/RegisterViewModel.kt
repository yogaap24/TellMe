package com.yogaap.tellme.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogaap.tellme.Data.database.StoryRepository
import com.yogaap.tellme.Data.di.Event
import com.yogaap.tellme.Response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel (private val repository: StoryRepository) : ViewModel() {
    val registerResponse: LiveData<RegisterResponse> = repository.registerResponse
    val toastText: LiveData<Event<String>> = repository.toastText

    fun postRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.postRegister(name, email, password)
        }
    }
}