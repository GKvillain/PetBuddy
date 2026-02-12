package com.example.petbuddyproject.Data

import com.google.firebase.Timestamp

data class User(
    val userId: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Timestamp? = null,
    val gender: String = "",
    val photoUrl: String = "",
    val countryId: String = "",
    val createAccount: Boolean = false,

    val petIds: List<String> = emptyList(),
    val weightIds: List<String> = emptyList(),
    val expenseIds: List<String> = emptyList(),
    val scheduleIds: List<String> = emptyList(),
    val feedingIds: List<String> = emptyList()
)
