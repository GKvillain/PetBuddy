package com.example.petbuddyproject

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.petbuddyproject.Adapter.PetResultAdapter
import com.example.petbuddyproject.Data.Pet
import com.example.petbuddyproject.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petbuddyproject.Activity.CreatePetProfile
import com.example.petbuddyproject.Activity.MainActivity
import kotlinx.coroutines.MainScope


class PetResult : AppCompatActivity() {
    private var showProfile: RecyclerView? = null
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var goArrow: ImageView? = null
    private var backArrow: ImageView? = null
    private var addPet: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()

        val userId = mAuth.uid.toString()
        val pets = mutableListOf<Pet>()

        db.collection("User").document(userId).get().addOnSuccessListener {
            documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)

            if (user != null && user.petIds != null){
            val petIds = user.petIds

            for (pet in petIds) {
                db.collection("Pet").document(pet).get().addOnSuccessListener { document ->
                    val petProfile = document.toObject(Pet::class.java)
                    if (petProfile != null) {
                        pets.add(petProfile)
                    }

                    if (pets.size == petIds.size) {
                        val myAdapter = PetResultAdapter(pets)
                        showProfile?.layoutManager = LinearLayoutManager(this)
                        showProfile?.adapter = myAdapter
                    }
                }
            }
            }
        }

        goArrow?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        backArrow?.setOnClickListener {
            startActivity(Intent(this, CreatePetProfile::class.java))
        }

        addPet?.setOnClickListener {
            startActivity(Intent(this, CreatePetProfile::class.java))
        }

    }

    private fun init(){
        showProfile = findViewById(R.id.showProfile)
        goArrow = findViewById(R.id.arrowGo)
        backArrow = findViewById(R.id.arrowBack)
        addPet = findViewById(R.id.addPet)
    }


}