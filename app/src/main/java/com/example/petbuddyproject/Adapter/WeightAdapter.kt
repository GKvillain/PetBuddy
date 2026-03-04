//package com.example.newweightact.adapter
package com.example.petbuddyproject.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.petbuddyproject.SortType
import com.example.petbuddyproject.databinding.ItemWeightInputBinding
import com.example.petbuddyproject.databinding.ItemWeightRecHeaderBinding
import com.example.petbuddyproject.databinding.ItemWeightRecordBinding
import com.example.petbuddyproject.listener.WeightActionListener
import com.example.petbuddyproject.model.WeightUiItem
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
class WeightAdapter(
    private val items: MutableList<WeightUiItem>,
    private val listener : WeightActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    companion object {
        const val TYPE_INPUT = 0
        const val TYPE_RECORD_HEADER = 1
        const val TYPE_RECORD = 2
    }



    override fun getItemViewType(position: Int): Int {
        return when (items[position]){
            is WeightUiItem.Input -> TYPE_INPUT
            is WeightUiItem.RecHeader -> TYPE_RECORD_HEADER
            is WeightUiItem.Record -> TYPE_RECORD
            else -> throw IllegalArgumentException("Unknown item at $position")
        }
    }
    //ViewHolder Class
    class InputViewHolder(
        private val binding: ItemWeightInputBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(item : WeightUiItem.Input, position: Int,listener: WeightActionListener){

            //Display Default Value

            binding.txtDate.text = item.date.format(dateFormatter)
            binding.txtTime.text = item.time.format(timeFormatter)

            if (item.weight == 0.0) {
                binding.edtWeight.text?.clear()
            } else {
                binding.edtWeight.setText(item.weight.toString())
            }

            //Select Date
            binding.btnWeightDatePicker.setOnClickListener {
                listener.onPickDate(position)
            }

            //Select Time
            binding.btnWeightTimePicker.setOnClickListener {
                listener.onPickTime(position)
            }

            //Save Record
            binding.btnWeightAction.setOnClickListener {
                var weightValue = binding.edtWeight.text?.trim().toString()
                if (weightValue.isBlank()){
                    binding.edtWeight.error = "weight can not be empty"
                    return@setOnClickListener
                }
                listener.onSave(position,weightValue.toDouble())
            }
        }

    }

    class RecordViewHolder(
        private val binding : ItemWeightRecordBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(item : WeightUiItem.Record){
            binding.txtWeightDate.text = item.date.format(dateFormatter)
            binding.txtWeightValue.text = item.weight.toString()
            binding.txtWeightTime.text = item.time.format(timeFormatter)
        }

    }

    class WeightRecHeaderViewHolder(
        private val binding: ItemWeightRecHeaderBinding,
        private val listener: WeightActionListener
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind (){
            binding.btnSortRecord.setOnClickListener { showSortMenu() }
        }
        private fun showSortMenu(){
            val popup = android.widget.PopupMenu(binding.root.context, binding.btnSortRecord)
            val menuList = listOf(
                "Date Record (Newest First)",
                "Date Record (Oldest First)")
            for (menu in menuList){
                popup.menu.add(menu)
            }

            popup.setOnMenuItemClickListener {
                when (it.title) {
                    menuList[0] -> listener.onSortSelected(SortType.NEWEST)
                    menuList[1] -> listener.onSortSelected(SortType.OLDEST)
                }
                true
            }
            popup.show()
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_INPUT -> {
                val binding = ItemWeightInputBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                InputViewHolder(binding)
            }
            TYPE_RECORD_HEADER -> {
                val binding = ItemWeightRecHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                WeightRecHeaderViewHolder(binding,listener)
            }
            TYPE_RECORD -> {
                val binding = ItemWeightRecordBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                RecordViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is InputViewHolder -> holder.bind(items[position] as WeightUiItem.Input,position,listener)
            is WeightRecHeaderViewHolder -> holder.bind()
            is RecordViewHolder -> holder.bind(items[position] as WeightUiItem.Record)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}


