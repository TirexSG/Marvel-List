package com.tirex.marvelsuperheros

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter.EXTRA_ID
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelapi.Character
import com.example.marvelapi.SuperHeroDataResponse
import com.tirex.marvelsuperheros.databinding.ActivitySuperheroBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

class SuperHeroActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperheroBinding
    private lateinit var adapter: SuperHeroAdapter
    private var emptySuperheroList: MutableList<Character> = arrayListOf()
    private lateinit var retrofit: Retrofit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperheroBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        retrofit = getRetrofit()
        fetchCharacters()

    }

    private fun initUI() {
        binding.svSuperHero.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchByName(query.orEmpty())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.svSuperHero.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchByName(newText.orEmpty())
                return true
            }
        }

        )

        adapter = SuperHeroAdapter { id -> navigateToDetail(id) }
        binding.rvSuperHero.setHasFixedSize(true)
        binding.rvSuperHero.layoutManager = LinearLayoutManager(this)
        binding.rvSuperHero.adapter = adapter

    }

    private fun searchByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val authParams = getAuthParams()
            val apiService = getRetrofit().create(ApiService::class.java)

            val myResponse: Response<SuperHeroDataResponse> = apiService.searchCharacters(
                authParams["ts"]!!,
                authParams["apikey"]!!,
                authParams["hash"]!!,
                query
            )

            if (myResponse.isSuccessful) {
                Log.i("tirex", "funciona :)")
                val response: SuperHeroDataResponse? = myResponse.body()
                val superheroesList: MutableList<Character> =
                    response?.data?.results ?: emptySuperheroList

                runOnUiThread {
                    adapter.updateList(superheroesList)

                }
            } else {
                Log.i("tirex", "no funciona :(")
                runOnUiThread {
                    adapter.updateList(emptySuperheroList)
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


    private fun fetchCharacters() {
        binding.progressBarActivity.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ts =
                    System.currentTimeMillis().toString()
                val publicKey = "9f678ed2ea347aeb3543134b849af3b5"
                val privateKey = "9475cc58abf82aa91d80143ebd98e32a6e53ea8f"
                val hash = generateMarvelHash(ts, privateKey, publicKey)

                val myResponse: Response<SuperHeroDataResponse> =
                    getRetrofit().create(ApiService::class.java)
                        .getCharacters(ts, publicKey, hash)

                if (myResponse.isSuccessful) {
                    val response: SuperHeroDataResponse? = myResponse.body()
                    val superherosList: MutableList<Character> =
                        response?.data?.results?.toMutableList() ?: mutableListOf()

                    val filteredSuperherosList: MutableList<Character> =
                        superherosList.filter { character ->
                            val imagePath = character.image?.path
                            val imageExtension = character.image?.extension
                            val imageUrl = if (imagePath != null && imageExtension != null) {
                                "${
                                    imagePath.replace(
                                        "http",
                                        "https"
                                    )
                                }/portrait_xlarge.$imageExtension"
                            } else {
                                null
                            }
                            imageUrl != null && !imageUrl.contains("image_not_available")
                        }.toMutableList()

                    withContext(Dispatchers.Main) {
                        adapter.updateList(filteredSuperherosList)
                        binding.progressBarActivity.isVisible = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        adapter.updateList(emptySuperheroList)
                        binding.progressBarActivity.isVisible = false
                    }
                }
            } catch (e: Exception) {
                Log.e("Character", "Exception: ${e.message}")
            }
        }
    }

    private fun navigateToDetail(id: Int) {
        val intent = Intent(this, SuperHeroDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(SuperHeroDetailsActivity.EXTRA_ID, id)
        intent.putExtras(bundle)
        startActivity(intent)
        Log.d("ID", "Este es el ID: $id")
    }

}