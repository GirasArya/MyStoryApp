package com.dicoding.mystoryapp.di

import android.content.Context
import com.dicoding.mystoryapp.data.local.datastore.UserPreference
import com.dicoding.mystoryapp.data.local.datastore.datastore
import com.dicoding.mystoryapp.domain.repository.UserRepository
import com.dicoding.mystoryapp.data.remote.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.datastore)
        val user = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}