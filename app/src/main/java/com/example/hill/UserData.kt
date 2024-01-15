package com.example.hill

data class UserData(
    val id: String? = null, // Use mobile number as ID
    val username: String? = null,
    val mobilenumber: String? = null,
    val password: String? = null

)

data class UserDetails(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val gender: String? = null
)