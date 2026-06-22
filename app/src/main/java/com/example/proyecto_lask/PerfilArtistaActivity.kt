package com.example.proyecto_lask

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilArtistaActivity : AppCompatActivity() {

    private lateinit var ivAvatar: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvBio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_artista)

        ivAvatar = findViewById(R.id.ivAvatarArtista)
        tvNombre = findViewById(R.id.tvNombreArtista)
        tvBio    = findViewById(R.id.tvBioArtista)

        // Recibe el id_usuario del artista
        val idUsuario = intent.getIntExtra("id_usuario", -1)
        if (idUsuario == -1) { finish(); return }

        cargarPerfil(idUsuario)
    }

    private fun cargarPerfil(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.create().getUser(idUsuario)
                withContext(Dispatchers.Main) {
                    if (respuesta.isSuccessful) {
                        val usuario = respuesta.body()
                        tvNombre.text = usuario?.name ?: ""
                        tvBio.text = usuario?.bio?.toString()
                            ?.takeIf { it.isNotEmpty() }
                            ?: "Sin descripción"

                        // Cargar foto — si no tiene, mostrar artistadefault
                        val pfp = usuario?.pfp?.toString()
                        if (!pfp.isNullOrEmpty()) {
                            val bytes = Base64.decode(pfp, Base64.DEFAULT)
                            val bitmap = BitmapFactory
                                .decodeByteArray(bytes, 0, bytes.size)
                            Glide.with(this@PerfilArtistaActivity)
                                .load(bitmap)
                                .circleCrop()
                                .placeholder(R.drawable.artistadefault)
                                .into(ivAvatar)
                        } else {
                            ivAvatar.setImageResource(R.drawable.artistadefault)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilArtistaActivity,
                        "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}