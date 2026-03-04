//package com.example.newweightact.activity
package com.example.petbuddyproject.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petbuddyproject.R
import com.example.petbuddyproject.SortType
import com.example.petbuddyproject.Adapter.WeightAdapter
import com.example.petbuddyproject.databinding.ActivityWeightBinding
import com.example.petbuddyproject.Data.WeightRecord
import com.example.petbuddyproject.WeightActionListener
import com.example.petbuddyproject.WeightUiItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

const val RECORD_START_INDEX = 2
class WeightActivity : AppCompatActivity(), WeightActionListener{
    //Test
    val currentUserId = "U06"
    val currentPetId = "P01"

    //firebase
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val collectionName = "Weight_Records"
    val weightRecRef : CollectionReference = db.collection(collectionName)
    private var listenerRegistration: ListenerRegistration? = null
    //Record
    val allRecords = mutableListOf<WeightUiItem.Record>()

    private lateinit var binding : ActivityWeightBinding
    private lateinit var adapter : WeightAdapter
    private val uiItems = mutableListOf<WeightUiItem>()

    //record item
    private val recordItems = mutableListOf<WeightUiItem.Record>()

    private var currentSort = SortType.NEWEST
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        //binding
        binding = ActivityWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initRecycler()
        observeWeightRecordsRealtime()
        //loadAllWeightRecords()

        binding.btnWeightBackToHome.setOnClickListener {
            val intent = Intent(this, HealthActivity::class.java)
            startActivity(intent)
        }


    }
    override fun onPickDate(position: Int) {
        val item = uiItems[position] as WeightUiItem.Input

        DatePickerDialog(
            this,
            {_,year,month,day ->
                item.date = LocalDate.of(year, month + 1 , day)
                adapter.notifyItemChanged(position)
            },
            item.date.year,
            item.date.monthValue - 1,
            item.date.dayOfMonth
        ).show()
    }

    override fun onPickTime(position: Int) {
        val item = uiItems[position] as WeightUiItem.Input
        TimePickerDialog(
            this,
            {_,hour,min ->
                item.time = LocalTime.of(hour,min)
                adapter.notifyItemChanged(position)
            },
            item.time.hour,
            item.time.minute,
            true
        ).show()
    }


    override fun onSave(position: Int,weightValue : Double) {
        val input = uiItems[position] as WeightUiItem.Input

        //Combine date and time to timestamp
        val localDateTime = LocalDateTime.of(input.date,input.time)
        val timestamp = Timestamp(
            Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        )
        input.weight = weightValue
        val record = WeightRecord(
            datetime = timestamp,
            weight = input.weight,
            userId = currentUserId,
            petId = currentPetId
        )
        addWeightRecord(record)
    }

    override fun onSortSelected(type: SortType) {
        currentSort = type

        val removedCount = uiItems.size - RECORD_START_INDEX
        if (removedCount > 0) {
            uiItems.subList(RECORD_START_INDEX, uiItems.size).clear()
            adapter.notifyItemRangeRemoved(RECORD_START_INDEX, removedCount)
        }

        recordItems.clear()
        startRealtimeListener(type)


    }
    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
    private fun initRecycler(){
        uiItems.clear()
        uiItems.add(WeightUiItem.Input())
        uiItems.add(WeightUiItem.RecHeader)

        adapter = WeightAdapter(uiItems,this)

        binding.rvWeight.apply {
            layoutManager = LinearLayoutManager(this@WeightActivity)
            adapter = this@WeightActivity.adapter
        }

    }
    private fun onRecordAdded(dc: DocumentChange) {
        val record = dc.document.toObject(WeightRecord::class.java)
        val (date, time) = separateDateTime(record.datetime)

        val uiRecord = WeightUiItem.Record(
            id = record.weightId,
            weight = record.weight,
            date = date,
            time = time
        )

        val index = dc.newIndex

        recordItems.add(index, uiRecord)
        uiItems.add(index + RECORD_START_INDEX, uiRecord)

        adapter.notifyItemInserted(index + RECORD_START_INDEX)
    }

    private fun onRecordModified(dc: DocumentChange) {
        val record = dc.document.toObject(WeightRecord::class.java)
        val (date, time) = separateDateTime(record.datetime)

        val oldIndex = dc.oldIndex
        val newIndex = dc.newIndex

        val updated = WeightUiItem.Record(
            id = record.weightId,
            weight = record.weight,
            date = date,
            time = time
        )

        if (oldIndex == newIndex) {
            // update อย่างเดียว
            recordItems[oldIndex] = updated
            uiItems[oldIndex + RECORD_START_INDEX] = updated
            adapter.notifyItemChanged(oldIndex + RECORD_START_INDEX)
        } else {
            // datetime เปลี่ยน → item ขยับ
            recordItems.removeAt(oldIndex)
            recordItems.add(newIndex, updated)

            uiItems.removeAt(oldIndex + RECORD_START_INDEX)
            uiItems.add(newIndex + RECORD_START_INDEX, updated)

            adapter.notifyItemMoved(
                oldIndex + RECORD_START_INDEX,
                newIndex + RECORD_START_INDEX
            )
        }
    }

    private fun onRecordRemoved(dc: DocumentChange) {
        val index = dc.oldIndex

        recordItems.removeAt(index)
        uiItems.removeAt(index + RECORD_START_INDEX)

        adapter.notifyItemRemoved(index + RECORD_START_INDEX)
    }

    private fun addWeightRecord(record : WeightRecord) {

        val doc = weightRecRef.document()
        val newRecord = record.copy(weightId = doc.id)

        doc.set(newRecord)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Weight saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

                resetInput()
            }
            .addOnFailureListener {
                Log.e("WeightActivity", "Add record failed", it)
                Toast.makeText(
                    this,
                    "Failed to save weight. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun resetInput() {
        val input = uiItems[0] as WeightUiItem.Input

        input.weight = 0.0
        input.date = LocalDate.now()
        input.time = LocalTime.now()

        adapter.notifyItemChanged(0)
    }
    private fun observeWeightRecordsRealtime() {
        listenerRegistration = weightRecRef
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("petId", currentPetId)
            .orderBy("datetime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->

                if (error != null || snapshots == null) {
                    Log.e("WeightActivity", "Realtime error", error)
                    return@addSnapshotListener
                }

                for (dc in snapshots.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> onRecordAdded(dc)
                        DocumentChange.Type.MODIFIED -> onRecordModified(dc)
                        DocumentChange.Type.REMOVED -> onRecordRemoved(dc)
                    }
                }
            }
    }

    private fun startRealtimeListener(sortType: SortType) {
        listenerRegistration?.remove()

        val direction = when (sortType) {
            SortType.NEWEST -> Query.Direction.DESCENDING
            SortType.OLDEST -> Query.Direction.ASCENDING
        }

        listenerRegistration = weightRecRef
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("petId", currentPetId)
            .orderBy("datetime", direction)
            .addSnapshotListener { snapshots, error ->

                if (error != null || snapshots == null) return@addSnapshotListener

                for (dc in snapshots.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> onRecordAdded(dc)
                        DocumentChange.Type.MODIFIED -> onRecordModified(dc)
                        DocumentChange.Type.REMOVED -> onRecordRemoved(dc)
                    }
                }
            }
    }

    private fun separateDateTime(timestamp: Timestamp?): Pair<LocalDate, LocalTime> {
        val dateTime = timestamp!!.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val date = dateTime.toLocalDate()
        val time = dateTime.toLocalTime()
        return Pair(date,time)
    }
}
