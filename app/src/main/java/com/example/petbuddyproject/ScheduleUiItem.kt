//package com.example.newscheduleact
package com.example.petbuddyproject
import com.google.firebase.Timestamp

sealed class ScheduleUiItem {
    object Calendar : ScheduleUiItem()
    object UpcomingHeader : ScheduleUiItem()
    data class MonthHeader(
        val year : Int,
        val month : Int,
    ) : ScheduleUiItem()
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

        ) : ScheduleUiItem()
}