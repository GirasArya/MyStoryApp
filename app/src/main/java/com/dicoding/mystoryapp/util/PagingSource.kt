package com.dicoding.mystoryapp.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.mystoryapp.data.local.datastore.UserPreference
import com.dicoding.mystoryapp.data.remote.api.ApiService
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.domain.model.User
import kotlinx.coroutines.flow.first

open class PagingSource<T, U>(
    private val apiService: ApiService,
    private val pref: UserPreference
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = pref.getToken().first().token
            val response = apiService.getStories(token, position, params.loadSize)

            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (stories.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("API call failed")) // Handle API error
            }

        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}