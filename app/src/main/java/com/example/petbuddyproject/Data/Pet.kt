package com.example.petbuddyproject.Data

import com.google.firebase.Timestamp

data class Pet(
    val petId: String = "",
    val userId: String = "",
    val petName: String = "",
    val sex: String = "",
    val breed: String = "",
    val petTypeId: String = "",
    val birthDate: Timestamp? = null,
    val pathURL: String = "",

    val weightIds: List<String> = emptyList(),
    val expenseIds: List<String> = emptyList(),
    val scheduleIds: List<String> = emptyList(),
    val feedingIds: List<String> = emptyList()
)
