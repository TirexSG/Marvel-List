package com.tirex.marvelsuperheros

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelapi.Character

class SuperHeroAdapter(

    private var superheroList: List<Character> = emptyList(),
    private val onItemSelected: (Int) -> Unit
) :
    RecyclerView.Adapter<SuperHeroViewHolder>() {

    fun updateList(superheroList: List<Character>) {
        this.superheroList = superheroList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperHeroViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SuperHeroViewHolder(layoutInflater.inflate(R.layout.item_superhero, parent, false))
    }

    override fun getItemCount(): Int {
        return superheroList.size
    }

    override fun onBindViewHolder(holder: SuperHeroViewHolder, position: Int) {
        holder.bind(superheroList[position], onItemSelected)
    }
}