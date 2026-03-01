package com.example.petbuddyproject.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petbuddyproject.Data.Expense
import com.example.petbuddyproject.Data.Pet
import com.example.petbuddyproject.R
import com.google.firebase.firestore.FirebaseFirestore

class ExpenseAdapter(val expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "ExpenseAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.expense_list, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ExpenseAdapter.ViewHolder, position: Int) {
        val expense = expenses[position]

        when (expense.type.lowercase()) {
            "food" -> holder.imageCate?.setImageResource(R.drawable.pet_bowl)
            "vaccine" -> holder.imageCate?.setImageResource(R.drawable.vaccine)
            "toy" -> holder.imageCate?.setImageResource(R.drawable.toy)
            "other" -> holder.imageCate?.setImageResource(R.drawable.other)
            else -> holder.imageCate?.setImageResource(R.drawable.baseline_pets_24)
        }


        Log.d(TAG, "Has petIds: ${expense.petIds.size} pets")
        holder.typeText?.text = expense.type
        holder.moneyShow?.text = "$${expense.price}"

        loadPetsForExpense(holder.petRecyclerView, expense.petIds)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    private fun loadPetsForExpense(recyclerView: RecyclerView?, petIds: List<String>?) {
        if (recyclerView == null) return

        if (petIds.isNullOrEmpty()) {
            recyclerView.visibility = View.GONE
            return
        }

        recyclerView.visibility = View.VISIBLE

        db.collection("Pet")
            .whereIn("petId", petIds)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.toObjects(Pet::class.java)

                val petAdapter = AddPetAdapter(pets)
                recyclerView.layoutManager = LinearLayoutManager(
                    recyclerView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                recyclerView.adapter = petAdapter
            }
            .addOnFailureListener { e ->
                recyclerView.visibility = View.GONE
            }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageCate: ImageView? = itemView.findViewById(R.id.imageCategory)
        var typeText: TextView? = itemView.findViewById(R.id.typeExpense)
        var moneyShow: TextView? = itemView.findViewById(R.id.amountExpense)
        var petRecyclerView: RecyclerView? = itemView.findViewById(R.id.showExpensePetSelect)
    }
}