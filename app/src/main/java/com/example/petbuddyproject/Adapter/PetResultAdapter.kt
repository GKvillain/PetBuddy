package com.example.petbuddyproject.Adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petbuddyproject.Data.Pet
import com.example.petbuddyproject.R

class PetResultAdapter(private val items: List<Pet>) :
    RecyclerView.Adapter<PetResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.pet_profile, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pet = items[position]

        holder.nameTextView.text = pet.petName
        holder.breedTextVew.text = pet.breed
        val bitmap = BitmapFactory.decodeFile(pet.pathURL)
        holder.imageIdView.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.namePet)
        val imageIdView: ImageView = itemView.findViewById(R.id.imagePet)
        val breedTextVew: TextView = itemView.findViewById(R.id.breedPet)
    }
}
