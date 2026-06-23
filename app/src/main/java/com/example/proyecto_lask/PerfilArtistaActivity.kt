package com.example.proyecto_lask

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
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
    private lateinit var loguito: ImageView
    private lateinit var rvPlaylistsArtista: RecyclerView

    private lateinit var rvComentarios: RecyclerView
    private lateinit var etComentario: EditText
    private lateinit var btnEnviarComentario: Button
    private var idArtistaActual: Int = -1
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
        rvComentarios       = findViewById(R.id.rvComentarios)
        etComentario        = findViewById(R.id.etComentario)
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario)
        loguito= findViewById(R.id.loguito)

        loguito.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val idUsuario = intent.getIntExtra("id_usuario", -1)
        if (idUsuario == -1) { finish(); return }

        val prefs = getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
        val miUserId = prefs.getInt("user_id", -1)

        rvPlaylistsArtista = findViewById(R.id.rvPlaylistsArtista)

        btnEnviarComentario.setOnClickListener {
            val texto = etComentario.text.toString().trim()
            if (texto.isEmpty() || idArtistaActual == -1 || miUserId == -1) return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resp = RetrofitClient.create().createComentario(
                        id_artista = idArtistaActual,
                        id_usuario = miUserId,
                        texto      = texto
                    )
                    withContext(Dispatchers.Main) {
                        if (resp.isSuccessful) {
                            etComentario.setText("")
                            cargarComentarios(idArtistaActual) // recargar
                            Toast.makeText(this@PerfilArtistaActivity,
                                "Mensaje enviado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) { /* silencioso */ }
            }
        }
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
                        idArtistaActual = artista.id  // <- agregar
                        tvNombreArtistico.text    = artista.nombre_artistico
                        tvNombreArtistico.visibility = android.view.View.VISIBLE
                        cargarComentarios(artista.id) // <- agregar
                        cargarPlaylistsPublicas(idUsuario)

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
    private fun cargarComentarios(idArtista: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respComentarios = RetrofitClient.create().getComentarios()
                val respUsuarios    = RetrofitClient.create().getUsers()

                withContext(Dispatchers.Main) {
                    // Mapa id -> nombre
                    val nombres = respUsuarios.body()?.data
                        ?.associate { it.id.toString() to it.name } ?: emptyMap()

                    val comentarios = respComentarios.body()?.data
                        ?.filter { it.id_artista == idArtista.toString() }
                        ?.reversed() ?: emptyList()

                    rvComentarios.layoutManager = LinearLayoutManager(this@PerfilArtistaActivity)
                    rvComentarios.adapter = ComentarioAdapter(comentarios, nombres)
                }
            } catch (e: Exception) { /* silencioso */ }
        }
    }

    private fun cargarPlaylistsPublicas(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    RetrofitClient.create().getPlaylists()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val playlists = response.body()?.data ?: emptyList()
                        val publicas =
                            playlists.filter {
                                it.id_usuario == idUsuario && it.privacidad_playlist == 0 }
                        rvPlaylistsArtista.layoutManager =
                            LinearLayoutManager(
                                this@PerfilArtistaActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

                        rvPlaylistsArtista.adapter =
                            PlaylistAdapter(publicas) { playlist ->
                                val intent = Intent(
                                    this@PerfilArtistaActivity,
                                    PlaylistDetalle::class.java
                                )
                                intent.putExtra("id_playlist", playlist.id)
                                intent.putExtra("nombre_playlist", playlist.nombre_playlist)
                                startActivity(intent)
                            }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@PerfilArtistaActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}