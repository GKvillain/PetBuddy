package com.example.petbuddyproject.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petbuddyproject.Data.User
import com.example.petbuddyproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private var regisEmail: EditText? = null
    private var regisPass: EditText? = null
    private var regisCPass: EditText? = null

    private var btnReg: Button? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth? = null
    private val TAG: String = "Register Activity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        init()
        mAuth = FirebaseAuth.getInstance()


        btnReg?.setOnClickListener {
            val email = regisEmail?.text.toString().trim()
            val password = regisPass?.text.toString().trim()

            if (email.isEmpty()){
                Toast.makeText(this,"Please enter your email address.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (password.isEmpty()){
                Toast.makeText(this,"Please enter your password.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            mAuth!!.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (password.length < 6) {
                        regisPass?.error = "Please check your password."
                    } else {
                        Toast.makeText(this,"Authentication Failed: " + task.exception!!.message,
                            Toast.LENGTH_LONG).show()
                    }
                } else {
                    saveUserToFirestore(task.result.user!!.uid,task.result.user!!.email ?: "")
                    Toast.makeText(this,"Create account successfully!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, CreateProfile::class.java))
                    finish()
                }
            }


        }
    }

    private fun init(){
        regisEmail = findViewById(R.id.editTextTextEmailAddress)
        regisPass = findViewById(R.id.editTextTextPassword)
        regisCPass = findViewById(R.id.editConfPass)
        btnReg = findViewById(R.id.buttonReg)
    }

    private fun saveUserToFirestore(uid: String,email: String){

        val userToFirebase = hashMapOf(
            "userId" to uid,
            "email" to email
        )

        db.collection("User").document(uid).set(userToFirebase)

    }

}