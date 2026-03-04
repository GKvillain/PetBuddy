package com.example.petbuddyproject

import com.example.petbuddyproject.Data.WeightRecord
import java.time.LocalDate
import java.time.LocalTime

// sealed class ใช้แทน type ของ item ใน RecyclerView
sealed class WeightUiItem {

    data class Input (
        var weight: Double = 0.0,
        var date: LocalDate = LocalDate.now(),
        var time: LocalTime = LocalTime.now()
    ): WeightUiItem()        // บันทึกน้ำหนักประจำวัน
    object Chart : WeightUiItem()        // กราฟ
    object RecHeader : WeightUiItem()
    data class Record(
        val id : String,
        var weight: Double ,
        var date: LocalDate,
        var time: LocalTime      // รายการย้อนหลัง
    ) : WeightUiItem()
}