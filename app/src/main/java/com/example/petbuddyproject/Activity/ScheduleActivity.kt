//package com.example.newscheduleact
package com.example.petbuddyproject.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newscheduleact.databinding.ActivityScheduleBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class ScheduleActivity : AppCompatActivity() , ScheduleActionListener {

    private lateinit var binding: ActivityScheduleBinding
    private lateinit var adapter: ScheduleAdapter
    private val uiItems = mutableListOf<ScheduleUiItem>()

    //firebase
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "Schedule"
    private val scheduleRef : CollectionReference = db.collection(collectionName)
    private var listenerRegistration: ListenerRegistration? = null

    //test
    val currentUserId = "U06"
    val currentPetId = "P01"

    //calendar
    //private var selectedDates = mutableListOf<LocalDate>()
    private var selectionMode = DateSelectionMode.MULTI
    private var startDateMillis: Long? = null
    private var endDateMillis: Long? = null
    //private var dateSelection: DateSelection = DateSelection.None
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //binding
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecycler()
        observeScheduleRealtime()

        binding.fBtnAddSchedule.setOnClickListener {
            openAddSchedule()
        }
        /*
        val calendarView = findViewById<CalendarView>(R.id.calendar_view)
        // ตัวอย่างวันที่เริ่มต้น
        val calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.JANUARY, 1)
        val initialDate = CalendarDate(calendar.time)

        // กำหนดช่วงวันที่
        calendar.set(2024, Calendar.JANUARY, 1)
        val minDate = CalendarDate(calendar.time)
        calendar.set(2026, Calendar.DECEMBER, 31)
        val maxDate = CalendarDate(calendar.time)

        // ตั้งค่าปฏิทิน
        calendarView.setupCalendar(
            initialDate = initialDate,
            minDate = minDate,
            maxDate = maxDate,
            selectionMode = CalendarView.SelectionMode.RANGE
        )

        // ตั้ง Action เมื่อคลิก
        calendarView.onDateClickListener = { date ->
            Toast.makeText(this, "Clicked: $date", Toast.LENGTH_SHORT).show()
        }

        // ตั้ง Action เมื่อกดค้าง
        calendarView.onDateLongClickListener = { date ->
            Toast.makeText(this, "Long Clicked: $date", Toast.LENGTH_SHORT).show()
        }
*/

    }

    private fun initRecycler() {
        uiItems.clear()
        uiItems.add(ScheduleUiItem.Calendar)
        uiItems.add(ScheduleUiItem.UpcomingHeader)

        adapter = ScheduleAdapter(
            uiItems,
            this,
            onDateSelected = { date -> handleDateSelection(date)
            }
        )

        binding.rvSchedule.apply {
            layoutManager = LinearLayoutManager(this@ScheduleActivity)
            adapter = this@ScheduleActivity.adapter
        }
    }

    private fun openAddSchedule(){
        val intent = Intent(this, AddScheduleActivity::class.java)
        startDateMillis?.let {intent.putExtra("startDateMillis",it)}
        endDateMillis?.let {intent.putExtra("endDateMillis", it)}
        startActivity(intent)
    }

    private fun observeScheduleRealtime() {
        listenerRegistration = scheduleRef
            .orderBy("startTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->

                if (error != null || snapshots == null) return@addSnapshotListener

                rebuildUiItems(snapshots.documents)
            }
    }

    private fun rebuildUiItems(docs: List<DocumentSnapshot>) {
        uiItems.clear()
        uiItems.add(ScheduleUiItem.Calendar)
        uiItems.add(ScheduleUiItem.UpcomingHeader)

        val grouped = docs
            .mapNotNull { it.toObject(ScheduleUiItem.Schedule::class.java) }
            .groupBy {
                it.startTime!!
                    .toDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .withDayOfMonth(1)
            }

        grouped.forEach { (monthDate, schedules) ->
            uiItems.add(
                ScheduleUiItem.MonthHeader(
                    year = monthDate.year,
                    month = monthDate.monthValue
                )
            )
            uiItems.addAll(schedules)
        }

        adapter.notifyDataSetChanged()
    }
    override fun onScheduleClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun handleDateSelection(date: CalendarDate) {
        val millis = date.timeInMillis
        when (selectionMode) {
            DateSelectionMode.SINGLE -> {
                startDateMillis = millis
                endDateMillis = millis
            }

            DateSelectionMode.MULTI -> {
                if (startDateMillis == null) {
                    startDateMillis = millis
                } else {
                    endDateMillis = millis
                    normalizeRange()
                }
            }
        }
        println("START = $startDateMillis , END = $endDateMillis")
        //updateDatePreview()
    }
    /*
    private fun updateDatePreview() {
        if (selectedDates.isEmpty()) {
            startDate = null
            endDate= null
            return
        }

        val sorted = selectedDates.sorted()
        startDate = sorted.first()
        endDate = sorted.last()
    }*/

    private fun normalizeRange() {
        if (startDateMillis != null && endDateMillis != null) {
            if (startDateMillis!! > endDateMillis!!) {
                val temp = startDateMillis
                startDateMillis = endDateMillis
                endDateMillis = temp
            }
        }
    }

}