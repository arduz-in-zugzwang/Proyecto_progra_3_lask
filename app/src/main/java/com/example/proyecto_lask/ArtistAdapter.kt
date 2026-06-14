package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArtistAdapter(private val artistas: List<Artist>) :
    RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreArtista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artista = artistas[position]
        holder.tvNombre.text = artista.nombre
        holder.ivAvatar.setImageResource(artista.avatarResId)
    }

    override fun getItemCount(): Int = artistas.size
}
