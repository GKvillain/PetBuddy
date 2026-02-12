package com.example.petbuddyproject.Activity

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petbuddyproject.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePetProfile : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var petUser: EditText? = null
    var checkFemale : RadioButton? = null
    var checkMale: RadioButton? = null
    var petWeight: EditText? = null
    var vacRabies: CheckBox? = null
    var rabiesDate : EditText? = null
    var rabiesTime : EditText? = null
    var vacDa2pp: CheckBox? = null
    var da2ppDate: EditText? = null
    var da2ppTime: EditText? = null
    var vacDhpp: CheckBox? = null
    var dhppDate: EditText? = null
    var dhppTime: EditText? = null
    var btnCreate: Button? = null
    var layoutRabies: LinearLayout? = null
    var layoutDa2pp: LinearLayout? = null
    var layoutDhpp: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_pet_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()

        val pets = resources.getStringArray(R.array.pet)
        val arrayAdapter = ArrayAdapter(this, R.layout.country_list_item,pets)
        val petDropdown = findViewById<AutoCompleteTextView>(R.id.petDropdown)
        petDropdown.setAdapter(arrayAdapter)

        val breeds = resources.getStringArray(R.array.dog_breeds)
        val arrayAdapterBreed = ArrayAdapter(this,R.layout.country_list_item,breeds)
        val breedDropdown = findViewById<AutoCompleteTextView>(R.id.breedDropdown)
        breedDropdown.setAdapter(arrayAdapterBreed)

        val etDate = findViewById<EditText>(R.id.petDate)
        var selectedDate: Timestamp? = null

        etDate.setOnClickListener {

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val selectedCalendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, selectedYear)
                        set(Calendar.MONTH, selectedMonth)
                        set(Calendar.DAY_OF_MONTH,selectedDay)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    selectedDate = Timestamp(selectedCalendar.time)

                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etDate.setText(date)
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        vacRabies?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                layoutRabies!!.visibility = View.VISIBLE
            } else {
                layoutRabies!!.visibility = View.GONE
            }
        }

    }

    private fun saveUserToFirestore(username:String,FName: String,LName: String,Country: String,Date: Timestamp,gender: String){

        val uid = mAuth.uid.toString()
        val userToFirebase = mapOf(
            "username" to username,
            "firstName" to FName,
            "lastName" to LName,
            "country" to Country,
            "birthDate" to Date,
            "gender" to gender,
            "createAccount" to true
        )
//        val userToFirebase = hashMapOf(
//            "uid" to uid,
//            "email" to email
//        )

//        db.collection("User").document(uid).set(userToFirebase)
        db.collection("User").document(uid).update(userToFirebase)
    }

    private fun init(){
        petUser = findViewById(R.id.crePUser)
        checkFemale = findViewById(R.id.radioFemale)
        checkMale = findViewById(R.id.radioMale)
        petWeight = findViewById(R.id.crePWeightI)
        vacRabies = findViewById(R.id.cbRabies)
        rabiesDate = findViewById(R.id.etRabiesDate)
        rabiesTime = findViewById(R.id.etRabiesTime)
        vacDa2pp = findViewById(R.id.cbDA2PP)
        da2ppDate = findViewById(R.id.etDA2PPDate)
        da2ppTime = findViewById(R.id.etDA2PPTime)
        vacDhpp = findViewById(R.id.cbDHPP)
        dhppDate = findViewById(R.id.etDHPPDate)
        dhppTime = findViewById(R.id.etDHPPTime)
        btnCreate = findViewById(R.id.creBtn)
        layoutRabies = findViewById(R.id.layoutRabies)
        layoutDa2pp = findViewById(R.id.layoutDA2PP)
        layoutDhpp = findViewById(R.id.layoutDHPP)
    }

}