package com.dicoding.mystoryapp.domain.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.mystoryapp.data.local.datastore.UserPreference
import com.dicoding.mystoryapp.data.remote.api.ApiService
import com.dicoding.mystoryapp.data.remote.response.AddStoryResponse
import com.dicoding.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.mystoryapp.data.remote.response.LoginResponse
import com.dicoding.mystoryapp.data.remote.response.RegisterResponse
import com.dicoding.mystoryapp.data.remote.response.StoryResponse
import com.dicoding.mystoryapp.domain.model.User
import com.dicoding.mystoryapp.util.PagingSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

class UserRepository constructor(
    private val apiService: ApiService,
    private val pref: UserPreference
) {
    private val _addStories = MutableLiveData<AddStoryResponse>()
    val addStories: LiveData<AddStoryResponse> = _addStories

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = androidx.paging.PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                PagingSource<Any, Any?>(apiService, pref)
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(token: String): StoryResponse {
        return apiService.getStoriesWithLocation(token)
    }


    fun getSession(): LiveData<User> {
        return pref.getToken().asLiveData()
    }

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody) {
        val client = apiService.addStories(token, image, description)
        client.enqueue(object : retrofit2.Callback<AddStoryResponse> {
            override fun onResponse(
                call: retrofit2.Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                try {
                    if (response.isSuccessful && response.body() != null) {
                        _addStories.value = response.body()
                    } else {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        val error = jsonObject?.getBoolean("error")
                        val message = jsonObject?.getString("message")
                        _addStories.value = AddStoryResponse(error, message)
                        Log.e(
                            "uploadStory",
                            "onResponse: ${response.message()}, ${response.code()} $message"
                        )
                    }
                } catch (e: HttpException) {
                    Log.e("uploadStory", "onResponse: ${e.message()}")
                }


            }

            override fun onFailure(call: retrofit2.Call<AddStoryResponse>, t: Throwable) {
                when (t) {
                    is UnknownHostException -> {
                        Log.e("UnknownHostException", "onFailure: ${t.message.toString()}")
                    }

                    else -> {
                        Log.e("postRegister", "onFailure: ${t.message.toString()}")
                    }
                }
            }
        })
    }

    suspend fun saveSession(user: User) {
        return pref.saveUserToken(user)
    }

    suspend fun login() {
        return pref.isLogin()
    }

    suspend fun logout() {
        return pref.clearUserToken()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(
                    apiService,
                    userPreference
                ).also { instance = it }
            }
        }
    }
}
