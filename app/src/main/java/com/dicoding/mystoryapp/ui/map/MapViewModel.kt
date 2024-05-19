package com.dicoding.mystoryapp.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.domain.model.User
import com.dicoding.mystoryapp.domain.repository.UserRepository
import kotlinx.coroutines.launch

class MapViewModel(private val repository: UserRepository) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> get() = _listStory


    fun getListStoriesWithLocation(token: String){
        viewModelScope.launch {
            try {
                val storyResponse = repository.getStoriesWithLocation(token)
                _listStory.postValue(storyResponse.listStory) // Update LiveData here
            } catch (e : Exception){
                Log.e("MainViewModel", "Error getting stories: ${e.message}")
            }
        }
    }


    fun getSession(): LiveData<User> {
        return repository.getSession()
    }

}