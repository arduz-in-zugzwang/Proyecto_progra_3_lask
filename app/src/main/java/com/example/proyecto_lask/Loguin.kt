package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class Loguin : AppCompatActivity() {
    private lateinit var etUsuario: EditText
    private lateinit var etContra: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var botonRegistrate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loguin)
        etUsuario = findViewById(R.id.etUsuario)
        etContra = findViewById(R.id.etContra)
        botonRegistrate = findViewById(R.id.botonRegistrate)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)

        botonRegistrate.setOnClickListener {

            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        btnIniciarSesion.setOnClickListener {

            val usuario = etUsuario.text.toString().trim()
            val contra = etContra.text.toString().trim()

            if (usuario.isEmpty()) {
                etUsuario.error = "Ingrese un usuario"
                return@setOnClickListener
            }

            if (contra.isEmpty()) {
                etContra.error = "Ingrese una contraseña"
                return@setOnClickListener
            }

            // Aquí irá la llamada a la API
        }


//        btnIniciarSesion.setOnClickListener {
//            // TODO: aquí va tu validación de usuario y contraseña
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish() // para que no pueda volver al login con el botón "atrás"
//        }
    }
}