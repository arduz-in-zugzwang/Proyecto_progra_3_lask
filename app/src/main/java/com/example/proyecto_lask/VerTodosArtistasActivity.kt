package com.example.proyecto_lask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerTodosArtistasActivity : AppCompatActivity() {

    private lateinit var rvTodosArtistas: RecyclerView
    private lateinit var btnVolver: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_todos_artistas)

        btnVolver = findViewById(R.id.btnVolver)
        rvTodosArtistas = findViewById(R.id.rvTodosArtistas)

        btnVolver.setOnClickListener {
            finish()
        }

        cargarArtistas()
    }

    private fun cargarArtistas() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.create().getArtistas()
                if (respuesta.isSuccessful) {
                    val artistas = respuesta.body()?.data ?: emptyList()
                    
                    withContext(Dispatchers.Main) {
                        val prefs = getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
                        val loggedUserId = prefs.getInt("user_id", -1)

                        rvTodosArtistas.layoutManager = LinearLayoutManager(this@VerTodosArtistasActivity)
                        rvTodosArtistas.adapter = ArtistAdapter(artistas) { artista ->
                            if (artista.id_usuario == loggedUserId) {
                                // En este caso, como estamos en una Activity fuera del NavController de MainActivity,
                                // tal vez sea mejor simplemente terminar y decirle a MainActivity que navegue,
                                // o abrir una nueva instancia si es aceptable.
                                // Pero por simplicidad, podemos usar un Intent o terminar.
                                // Sin embargo, Profile es un Fragment. 
                                // Una forma limpia es volver a MainActivity con un flag.
                                val intent = Intent(this@VerTodosArtistasActivity, MainActivity::class.java)
                                intent.putExtra("NAVIGATE_TO", "profile")
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this@VerTodosArtistasActivity, PerfilArtistaActivity::class.java)
                                intent.putExtra("id_usuario", artista.id_usuario)
                                startActivity(intent)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@VerTodosArtistasActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}