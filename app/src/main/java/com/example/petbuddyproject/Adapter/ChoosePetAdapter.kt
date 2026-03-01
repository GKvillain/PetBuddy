package com.example.petbuddyproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petbuddyproject.Data.Pet
import com.example.petbuddyproject.R

class ChoosePetAdapter(private val items: MutableList<Pet>) :
    RecyclerView.Adapter<ChoosePetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.choose_pet_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pet = items[position]

        holder.nameTextView.text = pet.petName
        holder.breedTextView.text = pet.breed

        Glide.with(holder.imageView.context)
            .load(pet.pathURL)
            .into(holder.imageView)

        holder.checkBox.setOnCheckedChangeListener(null)

        holder.checkBox.isChecked = pet.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            pet.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int = items.size

    fun getSelectedPets(): List<Pet> {
        return items.filter { it.isSelected }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.chnamePet)
        val imageView: ImageView = itemView.findViewById(R.id.chimagePet)
        val breedTextView: TextView = itemView.findViewById(R.id.chbreedPet)
        val checkBox: CheckBox = itemView.findViewById(R.id.chPet)
    }
}
