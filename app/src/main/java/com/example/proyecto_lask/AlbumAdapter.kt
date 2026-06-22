package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.proyecto_lask.albumes.Data

class AlbumAdapter(
    private val albumes: List<Data>
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvNombre: TextView =
            view.findViewById(R.id.tvNombreAlbum)

        val tvArtista: TextView =
            view.findViewById(R.id.tvArtistaAlbum)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)

        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AlbumViewHolder,
        position: Int
    ) {

        val album = albumes[position]

        holder.tvNombre.text =
            album.nombre_album

        holder.tvArtista.text =
            "Artista ID: ${album.id_artista}"
    }

    override fun getItemCount(): Int =
        albumes.size
}