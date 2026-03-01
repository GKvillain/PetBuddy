package com.example.petbuddyproject

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petbuddyproject.Adapter.AddPetAdapter
import com.example.petbuddyproject.Adapter.PetResultAdapter
import com.example.petbuddyproject.Data.Pet
import com.example.petbuddyproject.Data.User
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddExpense : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var currencyToggle: MaterialButtonToggleGroup? = null
    var food: ImageView? = null
    var vaccine: ImageView? =null
    var toy: ImageView? =null
    var other: ImageView? = null
    var currentExpId: String? = null
    var inputMoney: EditText? = null
    var btnSave: ImageView? = null
    var showPetChoose: RecyclerView? = null
    var etDate: EditText? = null

    // Variables to store selections
    private var selectedCurrency: String = "THB" // Default
    private var selectedType: String = ""
    private var selectedDate: Timestamp? = null
    private lateinit var selectedPetIds: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()

        selectedPetIds = intent.getStringArrayListExtra("selectedCount") ?: emptyList()

        if (selectedPetIds.isEmpty()) {
            Toast.makeText(this, "No pets selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadSelectedPets(selectedPetIds)

        setupClickListeners()
    }

    private fun init() {
        currencyToggle = findViewById(R.id.currencyToggle)
        food = findViewById(R.id.petBowl)
        vaccine = findViewById(R.id.vaccine)
        toy = findViewById(R.id.toy)
        other = findViewById(R.id.other)
        inputMoney = findViewById(R.id.inputMoney)
        btnSave = findViewById(R.id.finishBtn)
        showPetChoose = findViewById(R.id.showPetChoose)
        etDate = findViewById(R.id.expenseDate)
    }

    private fun loadSelectedPets(petIds: List<String>) {
        val pets = mutableListOf<Pet>()

        db.collection("Pet")
            .whereIn("petId", petIds)
            .get()
            .addOnSuccessListener { result ->
                pets.clear()
                pets.addAll(result.toObjects(Pet::class.java))

                if (pets.isNotEmpty()) {
                    val myAdapter = AddPetAdapter(pets)
                    showPetChoose?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    showPetChoose?.adapter = myAdapter
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading pets: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupClickListeners() {
        currencyToggle?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                selectedCurrency = when (checkedId) {
                    R.id.btnUSD -> "USD"
                    R.id.btnTHB -> "THB"
                    else -> "THB"
                }
            }
        }

        // Date selection
        etDate?.setOnClickListener {
            pickDate { timestamp ->
                selectedDate = timestamp
            }
        }

        food?.setOnClickListener {
            selectedType = "Food"
            resetTypeSelection()
            food?.alpha = 1.0f
        }

        vaccine?.setOnClickListener {
            selectedType = "Vaccine"
            resetTypeSelection()
            vaccine?.alpha = 1.0f
        }

        toy?.setOnClickListener {
            selectedType = "Toy"
            resetTypeSelection()
            toy?.alpha = 1.0f
        }

        other?.setOnClickListener {
            selectedType = "Other"
            resetTypeSelection()
            other?.alpha = 1.0f
        }

        btnSave?.setOnClickListener {
            validateAndSave()
        }
    }

    private fun resetTypeSelection() {
        food?.alpha = 0.5f
        vaccine?.alpha = 0.5f
        toy?.alpha = 0.5f
        other?.alpha = 0.5f
    }

    private fun validateAndSave() {
        val priceText = inputMoney?.text.toString()
        if (priceText.isEmpty()) {
            inputMoney?.error = "Please enter amount"
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null) {
            inputMoney?.error = "Invalid amount"
            return
        }

        if (selectedType.isEmpty()) {
            Toast.makeText(this, "Please select expense type", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show()
            return
        }

        saveDataToFirebase(
            petIds = selectedPetIds,
            date = selectedDate!!,
            price = price,
            type = selectedType,
            currency = selectedCurrency
        )
    }

    private fun saveDataToFirebase(
        petIds: List<String>,
        date: Timestamp,
        price: Double,
        type: String,
        currency: String
    ) {
        val userId = mAuth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert currency if needed
        var finalPrice = price
        if (currency == "USD") {
            finalPrice = price * 31.12 // Convert USD to THB
        }

        // Create ONE expense document for ALL selected pets
        val expRef = db.collection("Expense").document()

        val expenseData = mapOf(
            "expenseId" to expRef.id,
            "userId" to userId,
            "petIds" to petIds,  // This saves as an ARRAY of pet IDs
            "date" to date,
            "price" to finalPrice,
            "type" to type,
            "currency" to currency
        )

        // Show loading indicator
        btnSave?.isEnabled = false

        expRef.set(expenseData)
            .addOnSuccessListener {
                Toast.makeText(this, "Expense saved successfully for ${petIds.size} pet(s)", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                btnSave?.isEnabled = true
                Toast.makeText(this, "Error saving expense: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddExpense", "Error saving expense", e)
            }
    }

    private fun pickDate(
        onDateSelected: (Timestamp) -> Unit
    ) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val timestamp = Timestamp(selectedCalendar.time)

                // Display date
                etDate?.setText("$day/${month + 1}/$year")

                onDateSelected(timestamp)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }
}