package com.dicoding.mystoryapp.ui.register


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.dicoding.mystoryapp.domain.repository.UserRepository

import com.dicoding.mystoryapp.data.remote.response.RegisterResponse


class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

     suspend fun registerUser(name: String, email: String, password: String) {
        val response = userRepository.register(name, email, password)
        _registerResponse.value = response
    }
}