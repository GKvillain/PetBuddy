package com.example.petbuddyproject.Activity

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
import com.example.petbuddyproject.Activity.MainActivity
import com.example.petbuddyproject.R
import com.example.petbuddyproject.Activity.Register
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var userEmail: EditText? = null
    private var userPass: EditText? = null
    private var btnLogin: Button? = null
    private var signUp: TextView? = null
    private var resetPass: TextView? = null

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

        mAuth = FirebaseAuth.getInstance()

//        if (mAuth!!.currentUser != null){
//            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            finish()
//        }

        btnLogin?.setOnClickListener {
            val email = userEmail?.text.toString().trim() { it <= ' '}
            val password = userPass?.text.toString().trim() { it <= ' '}

            if (email.isEmpty()) {
                Toast.makeText(this,"Please enter your email address.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this,"Please enter your password.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (password.length < 18) {
                        userPass?.error = "Please check your password."
                    } else {
                        Toast.makeText(this,"Authentication Failed: " + task.exception!!.message,
                            Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this,"Sign in successfully!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        signUp?.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        resetPass?.setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }


    }

    private fun init(){
        userEmail = findViewById(R.id.editTextEmail)
        userPass = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.buttonLogin)
        signUp = findViewById(R.id.signUp)
        resetPass = findViewById(R.id.resetPass)
    }
}