package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Loguin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loguin)

        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        btnIniciarSesion.setOnClickListener {
            // TODO: aquí va tu validación de usuario y contraseña
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // para que no pueda volver al login con el botón "atrás"
        }
    }
}