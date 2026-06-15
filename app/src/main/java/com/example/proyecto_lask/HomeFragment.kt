package com.example.proyecto_lask

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment(R.layout.fragment_home) {
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
        val rvCanciones = view.findViewById<RecyclerView>(R.id.rvCanciones)

        val canciones = listOf(
            Song(
                nombre = "Borro Cassette",
                artista = "Maluma",
                portadaResId = R.drawable.malumabeibi,
                audioResId = R.raw.borrro_cassette
            ),
            Song(
                nombre = "Mami Silicon",
                artista = "Colibritany",
                portadaResId = R.drawable.mamisilicon,
                audioResId = R.raw.mami_silicon
            ),
            Song(
                nombre = "Nose ",
                artista = "insertar texto",
                portadaResId = R.drawable.portadadefault,
                audioResId = R.raw.mami_silicon
            )
            // Agrega aquí más canciones con el mismo formato:
            // Song("Nombre", "Artista", R.drawable.tu_portada, R.raw.tu_audio)
        )

        rvCanciones.layoutManager = LinearLayoutManager(requireContext())
        rvCanciones.adapter = SongAdapter(canciones)

        // Tags: por ahora son de prueba, luego vendrán de la API
        // (los tags únicos de las canciones que el usuario escuchó)
        val tags = listOf("Rock Latino", "Kpop", "Música para dormir", "Relax Beat", "Indie Lofi")
        mostrarTags(view, tags)

        // Lista de artistas favoritos
        val rvArtistas = view.findViewById<RecyclerView>(R.id.rvArtistas)

        val artistas = listOf(
            Artist("Stray Kids", R.drawable.artistadefault),
            Artist("The Smiths", R.drawable.artistadefault),
            Artist("Jovani Vasquez", R.drawable.artistadefault)
            // Reemplaza R.drawable.artistadefault por la imagen real de cada artista
        )

        rvArtistas.layoutManager = LinearLayoutManager(requireContext())
        rvArtistas.adapter = ArtistAdapter(artistas)
    }

    private fun mostrarTags(view: View, tags: List<String>) {
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupTags)
        chipGroup.removeAllViews()

        tags.forEachIndexed { index, tag ->
            val chip = Chip(requireContext())
            chip.text = tag
            chip.isCheckable = false
            chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), coloresTags[index % coloresTags.size])
            )
            chipGroup.addView(chip)
        }
    }
}