//package com.example.newscheduleact
package com.example.petbuddyproject.Data
import com.google.firebase.Timestamp

data class Schedule(
    val scheduleId: String = "",
    val title: String = "",
    val startTime: Timestamp? = null,
    val endTime : Timestamp? = null,

    val tag : EventTag = EventTag(),

    val isDone : Boolean = false,
    val isAllDay : Boolean = false,

    val place : String = "",
    val note : String = "",

    val userId : String = "",
    val petId : List<String> = emptyList()


)
