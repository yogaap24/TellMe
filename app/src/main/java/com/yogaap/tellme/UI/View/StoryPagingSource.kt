package com.yogaap.tellme.UI.View

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yogaap.tellme.Data.pref.SessionPreferences
import com.yogaap.tellme.Data.retrofit.ApiService
import com.yogaap.tellme.Response.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val pref: SessionPreferences,
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getSession().first().token

            if (token.isNotEmpty()) {
                val responseData = apiService.getListStories(token, position, params.loadSize)
                if (responseData.isSuccessful) {
                    Log.d("Story Paging Source", "Load: ${responseData.body()}")
                    LoadResult.Page(
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                    )
                } else {
                    Log.d("Token", "Load Error: $token")
                    LoadResult.Error(Exception("Failed"))
                }
            } else {
                LoadResult.Error(Exception("Failed"))
            }
        } catch (e: Exception) {
            Log.d("Exception", "Load Error: ${e.message}")
            return LoadResult.Error(e)
        }
    }
}