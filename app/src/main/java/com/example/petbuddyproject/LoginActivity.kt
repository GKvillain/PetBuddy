package com.example.petbuddyproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var userEmail: EditText? = null
    var userPass: EditText? = null
    var btnLogin: Button? = null
    var signUp: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()

        btnLogin?.setOnClickListener {
            val email = userEmail?.text.toString().trim() { it <= ' '}
            val password = userPass?.text.toString().trim() { it <= ' '}


        }

        signUp?.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }


    }

    private fun init(){
        userEmail = findViewById(R.id.editTextEmail)
        userPass = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.buttonLogin)
        signUp = findViewById(R.id.signUp)
    }
}