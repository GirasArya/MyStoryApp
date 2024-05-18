package com.dicoding.mystoryapp.domain.model

data class User(
    val username : String,
    val token : String,
    val isLoggedIn : Boolean
)
