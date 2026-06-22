package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Loguin : AppCompatActivity() {
    private lateinit var etUsuario: EditText
    private lateinit var etContra: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var botonRegistrate: Button
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loguin)

        etUsuario = findViewById(R.id.etUsuario)
        etContra = findViewById(R.id.etContra)
        botonRegistrate = findViewById(R.id.botonRegistrate)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        logo = findViewById(R.id.logo)

        logo.setOnClickListener {
            startActivity(Intent(this, Bienvenido::class.java))
            finish()
        }

        botonRegistrate.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val respuesta = RetrofitClient.create().login(usuario, contra)

                    withContext(Dispatchers.Main) {
                        if (respuesta.isSuccessful) {
                            val usuarioLogueado = respuesta.body()

                            // Guardar sesión en SharedPreferences
                            getSharedPreferences("sesion_lask", MODE_PRIVATE)
                                .edit()
                                .putInt("user_id", usuarioLogueado?.id ?: -1)
                                .putString("user_name", usuarioLogueado?.name ?: "")
                                .putInt("user_id_pais", usuarioLogueado?.id_pais ?: 1)
                                .putInt("user_id_rol", usuarioLogueado?.id_rol ?: 1)
                                .apply()

                            Toast.makeText(
                                this@Loguin,
                                "Bienvenido ${usuarioLogueado?.name}",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@Loguin, MainActivity::class.java))
                            finish()

                        } else {
                            Toast.makeText(
                                this@Loguin,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Loguin, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}