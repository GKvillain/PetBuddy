package com.example.petbuddyproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private var userEmail: EditText? = null
    private var userPass: EditText? = null
    private var userConfPass: EditText? = null
    private var btnReg: Button? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val KEY_EMAIL = "email"
    private val KEY_PASS = "password"
    private val KEY_CPASS = "confirm password"
    private var userCount = 1

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

        btnReg?.setOnClickListener {
            save()
        }
    }

    private fun init(){
        userEmail = findViewById(R.id.editTextTextEmailAddress)
        userPass = findViewById(R.id.editTextTextPassword)
        userConfPass = findViewById(R.id.editConfPass)
        btnReg = findViewById(R.id.buttonReg)
    }

    fun save(){
        userCount++
        val email = userEmail?.text.toString()
        val password = userPass?.text.toString()
        val confPas = userConfPass?.text.toString()

        val userInform = mutableMapOf<String, Any>()
        userInform[KEY_EMAIL] = email
        userInform[KEY_PASS] = password
        userInform[KEY_CPASS] = confPas

        db.collection("User").document(userCount.toString()).set(userInform)
            .addOnSuccessListener {
                Toast.makeText(this,"Successful",Toast.LENGTH_LONG).show()
            }
    }
}