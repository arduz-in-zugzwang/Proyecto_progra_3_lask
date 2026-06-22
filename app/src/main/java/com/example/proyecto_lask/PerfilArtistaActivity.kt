package com.example.proyecto_lask

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilArtistaActivity : AppCompatActivity() {

    private lateinit var ivAvatar: ImageView
    private lateinit var tvNombreArtistico: TextView
    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvBio: TextView
    private lateinit var rvCanciones: RecyclerView
    private lateinit var rvAlbumes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_artista)

        ivAvatar         = findViewById(R.id.ivAvatarArtista)
        tvNombreArtistico = findViewById(R.id.tvNombreArtistico)
        tvNombreUsuario  = findViewById(R.id.tvNombreArtista)
        tvBio            = findViewById(R.id.tvBioArtista)
        rvCanciones      = findViewById(R.id.rvCancionesArtista)
        rvAlbumes        = findViewById(R.id.rvAlbumesArtista)

        val idUsuario = intent.getIntExtra("id_usuario", -1)
        if (idUsuario == -1) { finish(); return }

        cargarTodo(idUsuario)
    }

    private fun cargarTodo(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Datos del usuario
                val respUsuario = RetrofitClient.create().getUser(idUsuario)
                // 2. Lista de artistas para encontrar nombre artístico e id_artista
                val respArtistas = RetrofitClient.create().getArtistas()
                // 3. Todas las canciones y álbumes para filtrar
                val respCanciones = RetrofitClient.create().getCanciones()
                val respAlbumes   = RetrofitClient.create().getAlbumes()

                withContext(Dispatchers.Main) {
                    // --- Usuario ---
                    if (respUsuario.isSuccessful) {
                        val usuario = respUsuario.body()
                        tvNombreUsuario.text = usuario?.name ?: ""
                        tvBio.text = usuario?.bio?.toString()
                            ?.takeIf { it.isNotEmpty() } ?: "Sin descripción"

                        val pfp = usuario?.pfp?.toString()
                        if (!pfp.isNullOrEmpty()) {
                            val bytes  = Base64.decode(pfp, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            Glide.with(this@PerfilArtistaActivity)
                                .load(bitmap).circleCrop()
                                .placeholder(R.drawable.artistadefault)
                                .into(ivAvatar)
                        } else {
                            ivAvatar.setImageResource(R.drawable.artistadefault)
                        }
                    }

                    // --- Nombre artístico ---
                    val artista = respArtistas.body()?.data
                        ?.firstOrNull { it.id_usuario == idUsuario }

                    if (artista != null) {
                        tvNombreArtistico.text    = artista.nombre_artistico
                        tvNombreArtistico.visibility = android.view.View.VISIBLE
                    }

                    // --- Canciones del artista ---
                    val idArtista = artista?.id ?: -1
                    if (respCanciones.isSuccessful && idArtista != -1) {
                        val canciones = respCanciones.body()?.data
                            ?.filter { it.id_artista == idArtista } ?: emptyList()

                        rvCanciones.layoutManager = LinearLayoutManager(this@PerfilArtistaActivity)
                        rvCanciones.adapter = SongAdapter(canciones) { cancion ->
                            val intent = android.content.Intent(
                                this@PerfilArtistaActivity, DetailSong::class.java)
                            intent.putExtra("id_album", cancion.id_album)
                            intent.putExtra("id_cancion", cancion.id)
                            startActivity(intent)
                        }
                    }

                    // --- Álbumes del artista ---
                    if (respAlbumes.isSuccessful && idArtista != -1) {
                        val albumes = respAlbumes.body()?.data
                            ?.filter { it.id_artista == idArtista } ?: emptyList()

                        rvAlbumes.layoutManager = LinearLayoutManager(
                            this@PerfilArtistaActivity,
                            LinearLayoutManager.HORIZONTAL, false)
                        rvAlbumes.adapter = AlbumAdapter(albumes) { album ->
                            val intent = android.content.Intent(
                                this@PerfilArtistaActivity, AlbumDetail::class.java)
                            intent.putExtra("id_album", album.id)
                            startActivity(intent)
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