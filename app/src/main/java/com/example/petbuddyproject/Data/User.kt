package com.example.petbuddyproject.Data

import com.google.firebase.Timestamp

data class User(
    val userUID: String,
    val userId: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val birthDate: Timestamp,
    val gender: Char,
    val countryId: Int,
    val photoUrl: String,
    val petId: Int,
    val weightId: Int,
    val expenseId: Int,
    val scheduleId: Int,
    val feedingId: Int
)
