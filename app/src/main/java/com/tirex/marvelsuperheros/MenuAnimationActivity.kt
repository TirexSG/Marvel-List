package com.tirex.marvelsuperheros

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MenuAnimationActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_animation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            mediaPlayer = MediaPlayer.create(this@MenuAnimationActivity, R.raw.transition_sound)
            mediaPlayer.start()
            delay(1900) // Pausa la coroutine durante 2 segundos
            // Iniciar la actividad principal después de 2 segundos
            val intent = Intent(this@MenuAnimationActivity, SuperHeroActivity::class.java)
            startActivity(intent)
            finish() // Finaliza la SplashActivity para que no quede en la pila
        }

    }
}