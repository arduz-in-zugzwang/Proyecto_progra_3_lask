package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

                        rvCanciones.adapter =
                            SongAdapter(canciones.take(5))
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
                            tags.map { it.nombre_tag }
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
        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getArtistas()

                if (respuesta.isSuccessful) {

                    val artistas =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {

                        rvArtistas.layoutManager =
                            LinearLayoutManager(requireContext())

                        rvArtistas.adapter = ArtistAdapter(artistas.take(5)) { artista ->
                            val intent = Intent(requireContext(), PerfilArtistaActivity::class.java)
                            intent.putExtra("id_usuario", artista.id_usuario)
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
                            AlbumAdapter(albumes.take(5))
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

    private fun mostrarTags(view: View, tags: List<String>) {
        chipGroupTags = view.findViewById(R.id.chipGroupTags)
        chipGroupTags.removeAllViews()

        tags.forEachIndexed { index, tag ->
            val chip = Chip(requireContext())
            chip.text = tag
            chip.isCheckable = false
            chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), coloresTags[index % coloresTags.size])
            )
            chipGroupTags.addView(chip)
        }
    }
}