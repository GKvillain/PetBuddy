//package com.example.newscheduleact
package com.example.petbuddyproject.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newscheduleact.databinding.ActivityAddScheduleBinding
import com.example.newscheduleact.databinding.ActivityScheduleBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.toString

class AddScheduleActivity : AppCompatActivity() {
    private var startDateMillis: Long? = null
    private var endDateMillis: Long? = null

    //current date time
    private var date : LocalDate = LocalDate.now()
    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
    private lateinit var binding : ActivityAddScheduleBinding

    //firebase
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "Schedule"
    private val scheduleRef : CollectionReference = db.collection(collectionName)

    //test
    val currentUserId = "U06"
    val currentPetId = "P01"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateDateUI()

        binding.btnAllDay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layoutDateTime.visibility = View.GONE
                startTime = LocalTime.MIDNIGHT
                endTime = LocalTime.MIDNIGHT
            } else {
                binding.layoutDateTime.visibility = View.VISIBLE
            }
        }

        binding.fBtnConfirmAddSchedule.setOnClickListener {
            saveScheduleToFirebase()
        }

        binding.btnStartDatePicker.setOnClickListener {
            showDatePicker(startDateMillis) { date ->
                startDateMillis = date
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                binding.edtStartDate.setText(date.format(dateFormatter))
            }
        }

        binding.btnEndDatePicker.setOnClickListener {
            showDatePicker(endDateMillis) { date ->
                val millis = date
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                if (startDateMillis != null && millis < startDateMillis!!) {
                    Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show()
                    return@showDatePicker
                }

                endDateMillis = millis
                binding.edtEndDate.setText(date.format(dateFormatter))
            }
        }

        binding.btnStartTimePicker.setOnClickListener {
            showTimePicker(
                initialTime = startTime ?: LocalTime.now()
            ) { selectedTime ->
                startTime = selectedTime
                binding.edtStartTime.setText(
                    selectedTime.format(timeFormatter)
                )

                // ถ้า endTime น้อยกว่า startTime → ปรับอัตโนมัติ
                if (endTime != null && endTime!!.isBefore(startTime)) {
                    endTime = startTime!!.plusHours(1)
                    binding.edtEndTime.setText(
                        endTime!!.format(timeFormatter)
                    )
                }
            }
        }
        binding.btnEndTimePicker.setOnClickListener {
            showTimePicker(
                initialTime = endTime ?: startTime?.plusHours(1) ?: LocalTime.now()
            ) { selectedTime ->

                if (startTime != null && selectedTime.isBefore(startTime)) {
                    Toast.makeText(
                        this,
                        "End time must be after start time",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@showTimePicker
                }

                endTime = selectedTime
                binding.edtEndTime.setText(
                    selectedTime.format(timeFormatter)
                )
            }
        }

    }


    private fun updateDateUI() {
        //get from intent
        startDateMillis = intent.getLongExtra("startDateMillis", -1L)
            .takeIf { it != -1L }

        endDateMillis = intent.getLongExtra("endDateMillis", -1L)
            .takeIf { it != -1L }
        if(startDateMillis != null){
            startDateMillis?.let {
                val date = Instant.ofEpochMilli(it)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                binding.edtStartDate.setText(date.format(dateFormatter))
            }
        } else {
            binding.edtStartDate.setText(date.format(dateFormatter))
        }
        //binding.edtStartTime.setText(time.format(timeFormatter))
        if(endDateMillis != null){
            endDateMillis?.let {
                val date = Instant.ofEpochMilli(it)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                binding.edtEndDate.setText(date.format(dateFormatter))
            }
        } else {
            binding.edtEndDate.setText(date.format(dateFormatter))
        }

        //binding.edtEndTime.setText(endTime.format(timeFormatter))


        //set default date and time
        val now = LocalTime.now().withSecond(0).withNano(0)

        startTime = now
        endTime = now.plusHours(1)

        binding.edtStartTime.setText(startTime!!.format(timeFormatter))
        binding.edtEndTime.setText(endTime!!.format(timeFormatter))
    }
    private fun combineDateTime(
        dateMillis: Long,
        time: LocalTime
    ): Long {
        val date = Instant.ofEpochMilli(dateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return date
            .atTime(time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
    private fun saveScheduleToFirebase() {

        val title = binding.edtTitle.text.toString().trim()
        val place = binding.edtPlace.text.toString().trim()
        val note = binding.edtNote.text.toString().trim()
        val isAllDay = binding.btnAllDay.isChecked
        val startMillis = if (isAllDay) {
            startDateMillis!!
        } else {
            combineDateTime(startDateMillis!!, startTime!!)
        }

        val endMillis = if (isAllDay) {
            endDateMillis ?: startDateMillis!!
        } else {
            combineDateTime(
                endDateMillis ?: startDateMillis!!,
                endTime!!
            )
        }

        val startTimestamp = millisToTimestamp(startMillis)
        val endTimestamp = millisToTimestamp(endMillis)

        if (title.isEmpty() || startDateMillis == null) {
            Toast.makeText(this, "Title or Date missing", Toast.LENGTH_SHORT).show()
            return
        }

        /*
        val startTimestamp = millisToTimestamp(startDateMillis!!)
        val endTimestamp = millisToTimestamp(
            endDateMillis ?: startDateMillis!!
        )*/

        val scheduleId = scheduleRef.document().id

        val schedule = Schedule(
            scheduleId = scheduleId,
            title = title,
            startTime = startTimestamp,
            endTime = endTimestamp,
            tag = EventTag(
                tagId = "default",
                tagName = "General",
                tagColor = 0xFFBDBDBD.toInt()
            ),
            isDone = false,
            isAllDay = isAllDay,
            place = place,
            note = note,
            userId = currentUserId,
            petId = listOf(currentPetId)
        )

        scheduleRef
            .document(scheduleId)
            .set(schedule)
            .addOnSuccessListener {
                Toast.makeText(this, "Schedule saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun millisToTimestamp(millis: Long): Timestamp {
        return Timestamp(Date(millis))
    }

    private fun showDatePicker(
        currentMillis: Long?,
        onDateSelected: (LocalDate) -> Unit
    ) {
        val initialDate = currentMillis?.let {
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } ?: LocalDate.now()

        DatePickerDialog(
            this,
            { _, y, m, d ->
                onDateSelected(LocalDate.of(y, m + 1, d))
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).show()
    }

    private fun showTimePicker(
        initialTime: LocalTime,
        onTimeSelected: (LocalTime) -> Unit
    ) {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                onTimeSelected(LocalTime.of(hour, minute))
            },
            initialTime.hour,
            initialTime.minute,
            true
        ).show()
    }

}

