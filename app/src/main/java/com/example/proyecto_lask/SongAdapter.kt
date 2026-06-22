package com.example.proyecto_lask

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_lask.canciones.DataX

class SongAdapter(
    private val songs: List<DataX>,
    private val onClick: (DataX) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreCancion)
        val tvArtista: TextView = view.findViewById(R.id.tvArtista)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)

        return SongViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SongViewHolder,
        position: Int
    ) {

        val song = songs[position]

        holder.tvNombre.text = song.nombre_cancion
        holder.tvArtista.text = song.nombre_artistico
        holder.itemView.setOnClickListener { onClick(song) }
    }

    override fun getItemCount(): Int = songs.size
}