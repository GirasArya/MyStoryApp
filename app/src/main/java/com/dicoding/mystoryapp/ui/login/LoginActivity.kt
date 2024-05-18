package com.dicoding.mystoryapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystoryapp.ui.main.MainActivity
import com.dicoding.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.mystoryapp.data.remote.response.ErrorResponse
import com.dicoding.mystoryapp.data.local.datastore.UserPreference
import com.dicoding.mystoryapp.factory.ViewModelFactory
import com.dicoding.mystoryapp.data.local.datastore.datastore
import com.dicoding.mystoryapp.domain.model.User
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {
    private lateinit var userPreference: UserPreference
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBarLogin.visibility = View.GONE

        loginViewModel = obtainViewModel(this)
        userPreference = UserPreference(applicationContext.datastore)
        val session = loginViewModel.getSession().value


        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginName.text.toString()
            val password = binding.edLoginPassword.text.toString()
            binding.progressBarLogin.visibility = View.VISIBLE
            lifecycleScope.launch {
               try {
                   val message = loginViewModel.loginUser(email, password)
                   Log.d(message.toString(), "message : ")
                   loginViewModel.loginSession()
               } catch (e : HttpException){
                   val jsonInString = e.response()?.errorBody()?.string()
                   val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                   val errorMessage = errorBody.message
                   Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
               } finally {
                   binding.progressBarLogin.visibility = View.GONE
               }
            }
        }

        loginViewModel.loginResponse.observe(this){
            response ->
            if (response.error == false) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Login failed: ${response.message}", Toast.LENGTH_SHORT).show()
            }

            saveSession(
                User(
                response.loginResult?.name.toString(),
                AUTH_KEY + response.loginResult?.token.toString(),
                true)
            )
        }

        if (session != null && session.isLoggedIn) {
            moveToMainActivity()
            return
        }

    }

    private fun moveToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveSession(user: User) {
        loginViewModel.saveSession(user)
    }

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(LoginViewModel::class.java)
    }
    companion object {
        private const val AUTH_KEY = "Bearer "
    }

}