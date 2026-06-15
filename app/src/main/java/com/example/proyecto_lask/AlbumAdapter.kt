package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlbumAdapter(private val albumes: List<Album>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPortada: ImageView = view.findViewById(R.id.ivPortadaAlbum)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlbum)
        val tvArtista: TextView = view.findViewById(R.id.tvArtistaAlbum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumes[position]
        holder.tvNombre.text = album.nombre
        holder.tvArtista.text = album.artista
        holder.ivPortada.setImageResource(album.portadaResId)
    }

    override fun getItemCount(): Int = albumes.size
}