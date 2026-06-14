package com.example.proyecto_lask

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCanciones = view.findViewById<RecyclerView>(R.id.rvCanciones)

        val canciones = listOf(
            Song(
                nombre = "Borro Cassette",
                artista = "Maluma",
                portadaResId = R.drawable.malumabeibi,
                audioResId = R.raw.borro_cassette
            )
            // Agrega aquí más canciones con el mismo formato:
            // Song("Nombre", "Artista", R.drawable.tu_portada, R.raw.tu_audio)
        )

        rvCanciones.layoutManager = LinearLayoutManager(requireContext())
        rvCanciones.adapter = SongAdapter(canciones)
    }
}