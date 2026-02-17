package com.example.petbuddyproject.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petbuddyproject.ExpenseActivity
import com.example.petbuddyproject.FeedingActivity
import com.example.petbuddyproject.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.exp

class MainActivity : AppCompatActivity() {
    var feedBtn: Button? = null
    var expenseBtn: Button? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val docRef: CollectionReference = db.collection("Notebook")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
//        val note = mutableMapOf<String, Any>()
//        note["name"] = "Lookkaew"
//        note["password"] = "5555555567"
//
//        docRef.add(note)

        init()

        feedBtn?.setOnClickListener {
            startActivity(Intent(this, FeedingActivity::class.java))
        }

        expenseBtn?.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }
    }

    private fun init(){
        feedBtn = findViewById(R.id.feedBtn)
        expenseBtn = findViewById(R.id.expenseBtn)
    }
}