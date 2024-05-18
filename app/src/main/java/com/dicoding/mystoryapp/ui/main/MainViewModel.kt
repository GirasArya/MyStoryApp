package com.dicoding.mystoryapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.domain.model.User
import com.dicoding.mystoryapp.domain.repository.UserRepository
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> get() = _listStory

    fun getListStories(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getStories(token)
                _listStory.value = response.listStory
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error getting stories: ${e.message}")
            }
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getSession(): LiveData<User> {
        return repository.getSession()
    }
}


