package com.example.proyecto_lask

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var rvCanciones: RecyclerView
    private lateinit var rvArtistas: RecyclerView
    private lateinit var rvAlbumes: RecyclerView
    private lateinit var chipGroupTags: ChipGroup
    private val coloresTags = listOf(
        R.color.tag_amarillo_claro,
        R.color.tag_amarillo,
        R.color.tag_verde,
        R.color.tag_morado,
        R.color.tag_celeste,
        R.color.tag_rosado,
        R.color.tag_naranja,
        R.color.tag_turquesa
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lista de canciones
        rvCanciones = view.findViewById(R.id.rvCanciones)
        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getCanciones()

                if (respuesta.isSuccessful) {

                    val canciones =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {
                        rvCanciones.layoutManager =
                            LinearLayoutManager(requireContext())

                        val cancionesHome = canciones.take(5)

                        rvCanciones.adapter =
                            SongAdapter(cancionesHome) { cancion ->

                                val posicion =
                                    cancionesHome.indexOf(cancion)

                                val intent = Intent(
                                    requireContext(),
                                    DetailSong::class.java
                                )

                                intent.putExtra(
                                    "id_album",
                                    cancion.id_album
                                )

                                intent.putExtra(
                                    "id_cancion",
                                    cancion.id
                                )

                                startActivity(intent)
                            }
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Tags: por ahora son de prueba, luego vendrán de la API
        // (los tags únicos de las canciones que el usuario escuchó)
        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getTags()

                if (respuesta.isSuccessful) {

                    val tags =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {

                        mostrarTags(
                            view,
                            tags
                        )
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Lista de artistas favoritos
        rvArtistas = view.findViewById(R.id.rvArtistas)
        val tvVerMasArtistas = view.findViewById<TextView>(R.id.tvVerMasArtistas)

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getArtistas()

                if (respuesta.isSuccessful) {

                    val artistas =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {
                        if (artistas.isEmpty()) {
                            // Opcional: mostrar un mensaje si no hay artistas
                            // Toast.makeText(requireContext(), "No hay artistas nuevos", Toast.LENGTH_SHORT).show()
                        }

                        rvArtistas.layoutManager =
                            LinearLayoutManager(requireContext())

                        // Obtener el ID del usuario logueado para comparar
                        val prefs = requireContext().getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
                        val loggedUserId = prefs.getInt("user_id", -1)

                        // Mostramos inicialmente solo 3
                        rvArtistas.adapter = ArtistAdapter(artistas.take(3)) { artista ->
                            if (artista.id_usuario == loggedUserId) {
                                // Si es mi propio perfil, ir al fragmento de perfil
                                findNavController().navigate(R.id.profileFragment)
                            } else {
                                // Si es otro artista, ir a su actividad de perfil
                                val intent = Intent(requireContext(), PerfilArtistaActivity::class.java)
                                intent.putExtra("id_usuario", artista.id_usuario)
                                startActivity(intent)
                            }
                        }

                        // Al darle a "Ver más", abrimos la nueva pantalla con todos los artistas
                        tvVerMasArtistas.setOnClickListener {
                            val intent = Intent(requireContext(), VerTodosArtistasActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        rvAlbumes = view.findViewById(R.id.rvAlbumes)
        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getAlbumes()

                if (respuesta.isSuccessful) {

                    val albumes =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {

                        rvAlbumes.layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

                        rvAlbumes.adapter =
                            AlbumAdapter(albumes.take(5)){ album ->

                                val intent = Intent(
                                    requireContext(),
                                    AlbumDetail::class.java
                                )

                                intent.putExtra("id_album", album.id)

                                startActivity(intent)
                            }
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    private fun mostrarTags(view: View, tags: List<com.example.proyecto_lask.tags.Data>
    ) {
        chipGroupTags = view.findViewById(R.id.chipGroupTags)
        chipGroupTags.removeAllViews()

        tags.forEachIndexed { index, tag ->
            val chip = Chip(requireContext())
            chip.text = tag.nombre_tag
            chip.isCheckable = false
            chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), coloresTags[index % coloresTags.size])
            )
            chip.setOnClickListener {

                val intent = Intent(
                    requireContext(),
                    TagDetail::class.java
                )

                intent.putExtra("id_tag", tag.id)
                intent.putExtra("nombre_tag", tag.nombre_tag)

                startActivity(intent)
            }
            chipGroupTags.addView(chip)
        }
    }
}