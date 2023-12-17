package com.yogaap.tellme.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yogaap.tellme.Data.database.StoryRepository
import com.yogaap.tellme.Data.di.Event
import com.yogaap.tellme.Data.pref.SessionModel
import com.yogaap.tellme.Response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel (private val repository: StoryRepository) : ViewModel() {
    val toastText: LiveData<Event<String>> = repository.toastText
    val getListStories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<SessionModel> {
        return repository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}