package com.dicoding.mystoryapp.factory

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.di.Injection
import com.dicoding.mystoryapp.ui.addstory.AddStoryViewModel
import com.dicoding.mystoryapp.ui.main.MainViewModel
import com.dicoding.mystoryapp.ui.login.LoginViewModel
import com.dicoding.mystoryapp.ui.register.RegisterViewModel

class ViewModelFactory private constructor(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(Injection.provideRepository(context)) as T
        }
        else if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(Injection.provideRepository(context)) as T
        }
        else if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(Injection.provideRepository(context)) as T
        }
        else if(modelClass.isAssignableFrom(AddStoryViewModel::class.java)){
            return AddStoryViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}

