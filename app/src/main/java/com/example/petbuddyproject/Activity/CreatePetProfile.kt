package com.example.petbuddyproject.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petbuddyproject.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Time
import java.util.Date

class CreatePetProfile : AppCompatActivity() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var petUser: EditText? = null
    var radioGroup: RadioGroup? = null
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
            pickDate(etDate) {
                timestamp -> selectedDate = timestamp
            }
        }

        var dateOfRabies: Timestamp? = null;
        var timeOfRabies: Timestamp? = null;
        var dateOfDhpp: Timestamp? = null
        var timeOfDhpp: Timestamp? = null
        var dateOfDa2pp: Timestamp? = null
        var timeOfDa2pp: Timestamp? = null




        vacRabies?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                layoutRabies!!.visibility = View.VISIBLE
            } else {
                layoutRabies!!.visibility = View.GONE
            }
        }

        rabiesDate?.setOnClickListener {
            pickDate(rabiesDate!!) {
                timestamp -> dateOfRabies = timestamp
            }
        }

        rabiesTime?.setOnClickListener {
            pickTime(rabiesTime!!) { hour, minute ->

                val calendar = Calendar.getInstance().apply {
                    time = timeOfRabies?.toDate() ?: Date()
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }

                timeOfRabies = Timestamp(calendar.time)
            }
        }

        vacDhpp?.setOnCheckedChangeListener { _,isChecked ->
            if (isChecked) {
                layoutDhpp!!.visibility = View.VISIBLE
            }else{
                layoutDhpp!!.visibility = View.GONE
            }
        }

        dhppDate?.setOnClickListener {
            pickDate(dhppDate!!){
                timestamp -> dateOfDhpp = timestamp
            }
        }

        dhppTime?.setOnClickListener {
            pickTime(dhppTime!!) { hour,minute ->
                val calendar = Calendar.getInstance().apply {
                    time = timeOfDhpp?.toDate() ?: Date()
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                timeOfDhpp = Timestamp(calendar.time)
            }
        }

        vacDa2pp?.setOnCheckedChangeListener { _,  isChecked ->
            if (isChecked){
                layoutDa2pp!!.visibility = View.VISIBLE
            } else {
                layoutDa2pp!!.visibility = View.GONE
            }
        }

        da2ppDate?.setOnClickListener {
            pickDate(da2ppDate!!){
                timestamp -> dateOfDa2pp = timestamp
            }
        }

        da2ppTime?.setOnClickListener {
            pickTime(da2ppTime!!) {hour, minute ->
                val calendar = Calendar.getInstance().apply {
                    time = timeOfDa2pp?.toDate() ?: Date()
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                timeOfDa2pp = Timestamp(calendar.time)
            }
        }

        btnCreate?.setOnClickListener {
            val usernamePet = petUser!!.text.toString().trim()
            val weightPet = petWeight!!.text.toString().trim()
            val sex = when (radioGroup!!.checkedRadioButtonId) {
                R.id.radioFemale -> "Female"
                R.id.radioMale -> "Male"
                else -> "Other"
            }

        }

    }

    private fun saveUserToFirestore(usernamePet: String, ){

        val uid = mAuth.uid.toString()
        val userToFirebase = mapOf(

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
        radioGroup = findViewById(R.id.radioGroupSex)
    }

    private fun pickDate(
        etDate: EditText,
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

                etDate.setText("$day/${month + 1}/$year")

                onDateSelected(timestamp)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }


    private fun pickTime(
        etTime: EditText,
        onTimeSelected: (Int, Int) -> Unit
    ) {

        val calendar = Calendar.getInstance()

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour, minute ->

                val formattedTime = String.format("%02d:%02d", hour, minute)
                etTime.setText(formattedTime)

                onTimeSelected(hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.show()
    }


}