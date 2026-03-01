package com.example.petbuddyproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.petbuddyproject.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petbuddyproject.Data.Pet
import de.hdodenhof.circleimageview.CircleImageView

class AddPetAdapter(val pets: List<Pet>): RecyclerView.Adapter<AddPetAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddPetAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.show_pet,parent,false
        ))
    }

    override fun onBindViewHolder(holder: AddPetAdapter.ViewHolder, position: Int) {

        val pet = pets[position]


        Glide.with(holder.showPetImg!!.context)
            .load(pet.pathURL)
            .into(holder.showPetImg!!)
    }

    override fun getItemCount(): Int {
        return pets.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var showPetImg: CircleImageView? = itemView.findViewById(R.id.showPetImg)
    }
}