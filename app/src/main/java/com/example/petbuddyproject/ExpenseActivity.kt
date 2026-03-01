package com.example.petbuddyproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petbuddyproject.Adapter.ExpenseAdapter
import com.example.petbuddyproject.Data.Expense
import com.example.petbuddyproject.Data.Pet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    private lateinit var showRecords: RecyclerView
    private lateinit var myAdapter: ExpenseAdapter

    private val expenseList = mutableListOf<Expense>()
    private val petMap = mutableMapOf<String, Pet>()
    private var addPet: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        showRecords = findViewById(R.id.showRecords)
        addPet = findViewById(R.id.addPet)

        myAdapter = ExpenseAdapter(expenseList)

        showRecords.layoutManager = LinearLayoutManager(this)
        showRecords.adapter = myAdapter

        loadPetsThenExpenses()

        val choosePet = intent.getStringArrayListExtra("selectedCount") ?: return
        addPet?.setOnClickListener { startActivity( Intent(this, AddExpense::class.java).apply { putStringArrayListExtra("selectedCount", choosePet) } ) }
    }

    private fun loadPetsThenExpenses() {
        val userId = mAuth.uid ?: return

        db.collection("Pet")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { petResult ->

                petMap.clear()

                for (doc in petResult) {
                    val pet = doc.toObject(Pet::class.java)
                    petMap[pet.petId] = pet
                }

                loadExpenses()
            }
    }

    private fun loadExpenses() {
        val userId = mAuth.uid ?: return

        db.collection("Expense")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->

                expenseList.clear()

                for (document in result) {
                    val expense = document.toObject(Expense::class.java)
                    expenseList.add(expense)
                }

                myAdapter.notifyDataSetChanged()
            }
    }
}