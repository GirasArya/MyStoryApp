package com.dicoding.mystoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.mystoryapp.domain.model.User
import com.dicoding.mystoryapp.domain.repository.UserRepository
import com.dicoding.mystoryapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    suspend fun loginUser(email: String, password: String) {
        val response = userRepository.login(email, password)
        _loginResponse.value = response
    }

    fun saveSession(user: User){
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    fun getSession(): LiveData<User> {
        return userRepository.getSession()
    }

    fun loginSession(){
        viewModelScope.launch {
            userRepository.login()
        }
    }

}