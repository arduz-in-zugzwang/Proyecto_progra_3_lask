package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaylistAdapter(
    private val playlists: List<com.example.proyecto_lask.playlists.Data>,
    private val onClick: (com.example.proyecto_lask.playlists.Data) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        val tvNombre: TextView =
            view.findViewById(R.id.tvNombrePlaylist)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_playlist,
                    parent,
                    false
                )

        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {

        val playlist = playlists[position]

        holder.tvNombre.text =
            playlist.nombre_playlist

        holder.itemView.setOnClickListener {
            onClick(playlist)
        }
    }

    override fun getItemCount() =
        playlists.size
}