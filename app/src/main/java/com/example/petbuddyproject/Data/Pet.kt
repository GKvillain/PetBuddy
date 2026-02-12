package com.example.petbuddyproject.Data

import com.google.firebase.Timestamp

data class Pet(
    val petId: String = "",
    val petName: String = "",
    val gender: String = "",
    val breed: String = "",
    val birthDate: Timestamp? = null
)
