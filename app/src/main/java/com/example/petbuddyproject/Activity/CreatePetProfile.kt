package com.example.petbuddyproject.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import java.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petbuddyproject.Data.Pet
import com.example.petbuddyproject.PetResult
import com.example.petbuddyproject.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.sql.Time
import java.util.Date
import java.util.UUID

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
    var profilePet: ImageView? = null
    private var currentPetId:String? = null
    private var pathImage: String? = null
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let {
                saveProfileImage(it)
            }
        }

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

        val petRef = db.collection("Pet").document()
        currentPetId = petRef.id

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
            val breed = breedDropdown.text.toString().trim()
            val pet = petDropdown.text.toString().trim()
            val sex = when (radioGroup!!.checkedRadioButtonId) {
                R.id.radioFemale -> "Female"
                R.id.radioMale -> "Male"
                else -> "Other"
            }


            saveUserToFirestore(usernamePet,sex,dateOfRabies,timeOfRabies,dateOfDhpp,timeOfDhpp,dateOfDa2pp,timeOfDa2pp,selectedDate,breed,pet)
        }

        profilePet?.setOnClickListener {
            pickImage.launch("image/*")
        }

    }

    private fun saveUserToFirestore(usernamePet: String, sex: String, dateOfRabies: Timestamp?, timeOfRabies: Timestamp?, dateOfDhpp: Timestamp?
                                    , timeOfDhpp: Timestamp?, dateOfDa2pp: Timestamp?,timeOfDa2pp: Timestamp?,selectedDate: Timestamp?,breed: String,pet: String){

        val userId = mAuth.uid.toString()
        val petRef = db.collection("Pet").document(currentPetId!!)

        val petData = mapOf(
            "petId" to petRef.id,
            "userId" to userId,
            "petName" to usernamePet,
            "sex" to sex,
            "breed" to breed,
            "birthDate" to selectedDate,
            "pathURL" to pathImage
        )

        petRef.set(petData).addOnSuccessListener {
            db.collection("User").document(userId).update("petIds", FieldValue.arrayUnion(petRef.id))

            startActivity(Intent(this, PetResult::class.java))
            finish()
        }



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
        btnCreate = findViewById(R.id.crePetBtn)
        layoutRabies = findViewById(R.id.layoutRabies)
        layoutDa2pp = findViewById(R.id.layoutDA2PP)
        layoutDhpp = findViewById(R.id.layoutDHPP)
        radioGroup = findViewById(R.id.radioGroupSex)
        profilePet = findViewById(R.id.imageProfilePet)
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

    fun saveProfileImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)

            val fileName = "profile_image${UUID.randomUUID()}.jpg"
            val file = File(filesDir, fileName)

            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            pathImage = file.absolutePath
            saveImagePath(pathImage!!)

            loadProfileImage()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveImagePath(path: String) {
        val key = "profile_path_$currentPetId"
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putString(key, path).apply()
    }

    fun loadProfileImage() {
        val key = "profile_path_$currentPetId"
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val path = prefs.getString(key, null)

        if (path != null) {
            val bitmap = BitmapFactory.decodeFile(path)
            profilePet?.setImageBitmap(bitmap)
        }
    }


}