package com.example.petbuddyproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petbuddyproject.Adapter.ChoosePetAdapter
import com.example.petbuddyproject.Adapter.PetResultAdapter
import com.example.petbuddyproject.Data.Pet
import com.example.petbuddyproject.Data.User
import com.example.petbuddyproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChoosePet : AppCompatActivity() {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityMainBinding
    var displayProfile: RecyclerView? = null
    var arrowGo: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_pet)
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
                            val myAdapter = ChoosePetAdapter(pets)
                            displayProfile?.layoutManager = LinearLayoutManager(this)
                            displayProfile?.adapter = myAdapter
                        }
                    }
                }
            }
        }

        val mode = intent.getStringExtra(MODE_KEY)

        arrowGo?.setOnClickListener {

            val adapter = displayProfile?.adapter as? ChoosePetAdapter
            val selectedPetId = adapter?.getSelectedPets()?.map {it.petId}?: emptyList()

            val target = when (mode) {
                MODE_EXPENSE -> ExpenseActivity::class.java
                MODE_FEEDING -> FeedingActivity::class.java
                else -> return@setOnClickListener
            }

            Log.d("DEBUG", "mode = $mode")

            startActivity(Intent(this, target).apply {putStringArrayListExtra("selectedCount", ArrayList(selectedPetId)) }
            )
        }


    }

    private fun init(){
        displayProfile = findViewById(R.id.displayProfile)
        arrowGo = findViewById(R.id.arrowGo)
    }

    companion object {
        const val MODE_KEY = "mode"
        const val MODE_EXPENSE = "expense"
        const val MODE_FEEDING = "feeding"
    }
}