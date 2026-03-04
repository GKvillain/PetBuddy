package com.example.petbuddyproject

import com.example.petbuddyproject.SortType
import com.example.petbuddyproject.Adapter.WeightAdapter
import com.example.petbuddyproject.WeightUiItem


interface WeightActionListener {
    fun onPickDate(position: Int)
    fun onPickTime(position: Int)
    //fun onWeightChanged(position: Int, value: Double)
    fun onSave(position: Int, weightValue : Double)
    fun onSortSelected(type : SortType)
    fun onDestroy()
}