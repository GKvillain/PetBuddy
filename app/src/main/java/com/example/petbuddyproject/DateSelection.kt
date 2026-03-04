//package com.example.newscheduleact
package com.example.petbuddyproject
import java.time.LocalDate

sealed class DateSelection {
    object None : DateSelection()
    data class Single(val date: LocalDate) : DateSelection()
    data class Range(val start: LocalDate, val end: LocalDate) : DateSelection()
}