package com.tirex.marvelsuperheros

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelapi.Character
import com.example.marvelapi.Image
import com.squareup.picasso.Picasso
import com.tirex.marvelsuperheros.databinding.ItemSuperheroBinding

class SuperHeroViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSuperheroBinding.bind(view)


    fun bind(superHeroItemResponse: Character, onItemSelected: (Int) -> Unit) {
        binding.tvSuperHero.text = superHeroItemResponse.name
        binding.root.setOnClickListener{ onItemSelected(superHeroItemResponse.id) }
        val imagePath = superHeroItemResponse.image?.path
        val imageExtension = superHeroItemResponse.image?.extension

        val fullImagePath = if (imagePath != null && imageExtension != null) {
            "${imagePath.replace("http", "https")}/portrait_xlarge.$imageExtension"
        } else {
            null
        }
        Log.d("Image URL", "URL de la imagen: $fullImagePath")

        if (fullImagePath != null && !fullImagePath.contains("image_not_available")) {
            Glide.with(binding.ivSuperHero.context)
                .load(fullImagePath)
                .error(R.drawable.placeholder_image)
                .into(binding.ivSuperHero)
            }
            Log.e("Image URL", "URL de la imagen es nula o incorrecta")

        }

    }


