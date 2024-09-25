package com.tirex.marvelsuperheros

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.marvelapi.SuperHeroDataResponse
import com.squareup.picasso.Picasso
import com.tirex.marvelsuperheros.databinding.ActivitySuperHeroDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.security.MessageDigest

class SuperHeroDetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private lateinit var binding: ActivitySuperHeroDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperHeroDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id: Int = intent.getIntExtra(EXTRA_ID, -1)
        Log.d("ID", "Este es el ID recibido: $id")
        if (id != -1) {
            getSuperheroInformation(id)
        } else {
            Log.e("ID", "ID recibido es inválido")

        }

        getSuperheroInformation(id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars())
                controller.hide(WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        }


    }

    private fun getAuthParams(): Map<String, String> {
        val ts = System.currentTimeMillis().toString()
        val publicKey = "9f678ed2ea347aeb3543134b849af3b5"
        val privateKey = "9475cc58abf82aa91d80143ebd98e32a6e53ea8f"
        val hash = generateMarvelHash(ts, privateKey, publicKey)

        return mapOf(
            "ts" to ts,
            "apikey" to publicKey,
            "hash" to hash
        )
    }

    private fun generateMarvelHash(ts: String, privateKey: String, publicKey: String): String {
        val input = ts + privateKey + publicKey
        val md = MessageDigest.getInstance("MD5")
        return md.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    private fun createUI(superHeroDetailsResponse: SuperHeroDetailsResponse) {
        val results = superHeroDetailsResponse.data?.results
        val nameText = results?.firstOrNull()?.name ?: "Name not found"
        val comicItemName =
            results?.firstOrNull()?.comics?.items?.firstOrNull()?.name ?: "Comic not found"
        var descriptionText = results?.firstOrNull()?.description ?: "The description of this hero isn't available"
        if (descriptionText == "") {
            binding.tvSuperHeroDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            descriptionText = "The description of this hero isn't available"
        }
        binding.tvSuperHeroName.text = nameText
        binding.tvSuperHeroDescription.text = descriptionText
        binding.tvComicName.text = comicItemName

        val imagePath = results?.firstOrNull()?.image?.path
        val imageExtension = results?.firstOrNull()?.image?.extension

        val fullImagePath = if (imagePath != null && imageExtension != null) {
            "${imagePath.replace("http", "https")}/portrait_xlarge.$imageExtension"
        } else {
            null
        }
        Log.d("Image URL", "URL de la imagen: $fullImagePath")

        if (fullImagePath != null && !fullImagePath.contains("image_not_available")) {
            Glide.with(binding.ivSuperHeroDetail.context)
                .load(fullImagePath)
                .error(R.drawable.placeholder_image)
                .into(binding.ivSuperHeroDetail)
        }
        Log.e("Image URL", "URL de la imagen es nula o incorrecta")

    }

    private fun getSuperheroInformation(id: Int) {
        binding.ivSuperHeroDetail.isVisible = false
        binding.cvSuperHeroDetails.isVisible = false
        binding.cvSuperHeroImage.isVisible = false
        binding.main.isVisible = false
        binding.progressBarDetails.isVisible = true
        Log.d("SuperHeroInfo", "Fetching details for ID: $id")
        CoroutineScope(Dispatchers.IO).launch {
            val authParams = getAuthParams()
            try {
                val response: Response<SuperHeroDetailsResponse> =
                    getRetrofit().create(ApiService::class.java).getSuperHeroDetails(
                        id,
                        authParams["ts"]!!,
                        authParams["apikey"]!!,
                        authParams["hash"]!!
                    )
                Log.d("tirex", "Este es el superherodetailresponse: $response")

                if (response.isSuccessful) {
                    Log.i("tirex", "Details fetched successfully")
                    val superheroDetail = response.body()
                    withContext(Dispatchers.Main) {
                        if (superheroDetail != null) {
                            createUI(superheroDetail)
                            binding.progressBarDetails.isVisible = false
                            binding.ivSuperHeroDetail.isVisible = true
                            binding.cvSuperHeroDetails.isVisible = true
                            binding.cvSuperHeroImage.isVisible = true
                            binding.main.isVisible = true
                        }

                    }
                } else {
                    Log.i("tirex", "Failed to fetch details")

                    withContext(Dispatchers.Main) {
                        showErrorToast("Error al cargar los detalles")
                    }
                }
            } catch (e: Exception) {
                Log.e("tirex", "Exception occurred", e)
                withContext(Dispatchers.Main) {
                    showErrorToast("Error al cargar los detalles")
                }
            }
        }
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gateway.marvel.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}