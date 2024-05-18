package com.dicoding.mystoryapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.domain.model.User
import com.dicoding.mystoryapp.domain.repository.UserRepository
import com.dicoding.mystoryapp.data.remote.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    val addStoryResponse: LiveData<AddStoryResponse> = repository.addStories

    fun addNewStory(token: String, image: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            repository.addStory(token, image, description)
        }
    }

    fun getSession(): LiveData<User> {
        return repository.getSession()
    }
}