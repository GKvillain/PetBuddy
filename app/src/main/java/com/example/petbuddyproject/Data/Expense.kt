package com.example.petbuddyproject.Data

data class Expense(
    var expenseId: String = "",
    var userId: String = "",
    var type: String = "",
    var price: Double = 0.0,
    var date: Any = "",
    var petIds: List<String> = emptyList(),  // Changed to List<String>
    var notes: String = "",
    var currency: String = "THB"
)