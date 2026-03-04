//package com.example.newscheduleact
package com.example.petbuddyproject
import ru.cleverpumpkin.calendar.CalendarDate

interface ScheduleActionListener {
    fun onScheduleClick(position: Int)
    fun handleDateSelection(date: ru.cleverpumpkin.calendar.CalendarDate)
}