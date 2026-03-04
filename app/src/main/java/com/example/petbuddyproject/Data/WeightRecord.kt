package com.example.petbuddyproject.Data

import com.google.firebase.Timestamp

data class WeightRecord(
    var weightId: String = "",                 // document id จาก Firestore
    var datetime: Timestamp? = null,      // วันเวลาที่บันทึก
    var weight: Double = 0.0,             // น้ำหนัก
    var userId: String = "",              // ผู้ใช้
    var petId: String = ""                // สัตว์เลี้ยง
)