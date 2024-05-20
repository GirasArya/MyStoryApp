package com.dicoding.mystoryapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.mystoryapp.DataDummy
import com.dicoding.mystoryapp.MainDispatcherRule
import com.dicoding.mystoryapp.data.local.datastore.UserPreference
import com.dicoding.mystoryapp.data.remote.api.ApiService
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.domain.repository.UserRepository
import com.dicoding.mystoryapp.getOrAwaitValue
import com.dicoding.mystoryapp.util.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock private lateinit var repository: UserRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoriesResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        `when`(repository.getStories()).thenReturn(expectedStory)
        mainViewModel = MainViewModel(repository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.getListStories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        `when`(repository.getStories()).thenReturn(expectedStory)
        val mainViewModel = MainViewModel(repository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.getListStories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}


val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource(apiService: ApiService, pref: UserPreference) :
    PagingSource<Int, LiveData<List<ListStoryItem>>>(
        apiService, pref
    ) {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
