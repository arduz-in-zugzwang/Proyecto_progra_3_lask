package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_lask.artistas.Data


class ArtistAdapter(
    private val artistas: List<Data>,
    private val onClick: (Data) -> Unit = {}  // lambda opcional
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreArtista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artista = artistas[position]
        holder.tvNombre.text = artista.nombre_artistico

        // Click en el item → llama al lambda
        holder.itemView.setOnClickListener {
            onClick(artista)
        }
    }

    override fun getItemCount(): Int = artistas.size
}
