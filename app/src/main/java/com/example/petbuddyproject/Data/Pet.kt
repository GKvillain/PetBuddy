package com.example.petbuddyproject.Data

import com.google.firebase.Timestamp

data class Pet(
    val petId: String = "",
    val userId: String = "",
    val petName: String = "",
    val gender: String = "",
    val breed: String = "",
    val petTypeId: String = "",
    val birthDate: Timestamp? = null
)
