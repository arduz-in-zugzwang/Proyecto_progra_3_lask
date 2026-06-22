package com.example.proyecto_lask

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment(R.layout.fragment_search) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Álbumes (horizontal: portada + nombre + artista) ---
        val rvAlbumes = view.findViewById<RecyclerView>(R.id.rvAlbumes)

        val albumes = listOf(
            Album("Nombre álbum 1", "Artista 1", R.drawable.portadadefault),
            Album("Nombre álbum 2", "Artista 2", R.drawable.portadadefault),
            Album("Nombre álbum 3", "Artista 3", R.drawable.portadadefault),
            Album("Nombre álbum 4", "Artista 4", R.drawable.portadadefault)
            // Reemplaza por tus álbumes reales: Album("Nombre", "Artista", R.drawable.tu_portada)
        )

        rvAlbumes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        rvAlbumes.adapter = AlbumAdapter(albumes)

        // --- Canciones (vertical: todas las disponibles, reutiliza item_song) ---
        val rvCanciones = view.findViewById<RecyclerView>(R.id.rvCancionesBuscar)

        val canciones = listOf(
            Song(
                nombre = "Borro Cassette",
                artista = "Maluma",
                portadaResId = R.drawable.malumabeibi,
                audioResId = R.raw.borrro_cassette
            )
            // Agrega aquí todas las canciones disponibles:
            // Song("Nombre", "Artista", R.drawable.tu_portada, R.raw.tu_audio)
        )

        rvCanciones.layoutManager = LinearLayoutManager(requireContext())
//        rvCanciones.adapter = SongAdapter(canciones)
    }
}