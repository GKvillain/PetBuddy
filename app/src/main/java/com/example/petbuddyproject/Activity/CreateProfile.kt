package com.example.petbuddyproject.Activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petbuddyproject.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.sql.Time

class CreateProfile : AppCompatActivity() {

    var creUser: EditText? = null
    var creFname : EditText? = null
    var creLname: EditText? = null
//    var countryDropdown: MaterialAutoCompleteTextView? = null
    var checkFemale: RadioButton? = null
    var checkMale: RadioButton? = null
    var checkOther: RadioButton? = null
    var creBtn: Button? = null
    var radioGroup: RadioGroup? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var changeProfile: ImageView? = null
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                saveProfileImage(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()

        val countrys = resources.getStringArray(R.array.country)
        val arrayAdapter = ArrayAdapter(this, R.layout.country_list_item,countrys)
        val countryDropdown = findViewById<AutoCompleteTextView>(R.id.countryDropdown)
        countryDropdown.setAdapter(arrayAdapter)

        val etDate = findViewById<EditText>(R.id.etDate)
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
        creBtn!!.setOnClickListener {
            val username = creUser!!.text.toString().trim()
            val firstName = creFname!!.text.toString().trim()
            val lastName = creLname!!.text.toString().trim()
            val country = countryDropdown.text.toString().trim()
            val sex = when (radioGroup!!.checkedRadioButtonId) {
                R.id.radioFemale -> "Female"
                R.id.radioMale -> "Male"
                else -> "Other"
            }

            saveUserToFirestore(username,firstName,lastName,country,selectedDate!!,sex)

            startActivity(Intent(this, CreatePetProfile::class.java))
        }


        changeProfile?.setOnClickListener {
            pickImage.launch("image/*")
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
        db.collection("User").document(uid).update(userToFirebase)
    }

    fun saveProfileImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)

            val fileName = "profile_image.jpg"
            val file = File(filesDir, fileName)

            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            saveImagePath(file.absolutePath)

            loadProfileImage()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveImagePath(path: String) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putString("profile_path", path).apply()
    }


    fun loadProfileImage() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val path = prefs.getString("profile_path", null)

        if (path != null) {
            val bitmap = BitmapFactory.decodeFile(path)
            changeProfile?.setImageBitmap(bitmap)
        }
    }





    private fun init(){
        creUser = findViewById(R.id.creUser)
        creFname = findViewById(R.id.creFname)
        creLname = findViewById(R.id.creLname)
        checkFemale = findViewById(R.id.radioFemale)
        checkMale = findViewById(R.id.radioMale)
        checkOther = findViewById(R.id.radioOther)
        creBtn = findViewById(R.id.creBtn)
        radioGroup = findViewById(R.id.radioGroupSex)
        changeProfile = findViewById(R.id.imageProfile)
    }
}