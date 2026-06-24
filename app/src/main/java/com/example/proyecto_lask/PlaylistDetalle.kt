package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistDetalle : AppCompatActivity() {

    private lateinit var rvCanciones: RecyclerView
    private lateinit var tvTitulo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detalle)

        tvTitulo = findViewById(R.id.tvNuevos)
        rvCanciones = findViewById(R.id.rvCancionesPlaylist)

        val idPlaylist =
            intent.getIntExtra("id_playlist", -1)

        val nombrePlaylist =
            intent.getStringExtra("nombre_playlist") ?: ""

        tvTitulo.text = nombrePlaylist

        if (idPlaylist != -1) {
            cargarCancionesPlaylist(idPlaylist)
        }
    }

    private fun cargarCancionesPlaylist(idPlaylist: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respPlaylistCanciones =
                    RetrofitClient.create()
                        .getPlaylistCanciones()

                val respCanciones =
                    RetrofitClient.create()
                        .getCanciones()
                withContext(Dispatchers.Main) {
                    if (
                        respPlaylistCanciones.isSuccessful &&
                        respCanciones.isSuccessful
                    ) {

                        val relaciones =
                            respPlaylistCanciones.body()?.data
                                ?: emptyList()

                        val canciones =
                            respCanciones.body()?.data
                                ?: emptyList()

                        val idsCanciones =
                            relaciones
                                .filter {
                                    it.id_playlist == idPlaylist
                                }
                                .map {
                                    it.id_cancion
                                }

                        val cancionesPlaylist =
                            canciones.filter {
                                idsCanciones.contains(it.id)
                            }

                        rvCanciones.layoutManager =
                            LinearLayoutManager(
                                this@PlaylistDetalle
                            )

                        rvCanciones.adapter =
                            SongAdapter(cancionesPlaylist) { cancion ->

                                val intent = Intent(this@PlaylistDetalle, DetailSong::class.java)

                                intent.putExtra("id_album", cancion.id_album)

                                intent.putExtra("id_cancion", cancion.id)
                                startActivity(intent)
                            }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PlaylistDetalle,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}