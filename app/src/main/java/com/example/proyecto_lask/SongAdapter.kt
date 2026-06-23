package com.example.proyecto_lask

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_lask.canciones.DataX
import android.util.Base64


class SongAdapter(
    private val songs: List<DataX>,
    private val onClick: (DataX) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreCancion)
        val tvArtista: TextView = view.findViewById(R.id.tvArtista)
        val ivPortada: ImageView = view.findViewById(R.id.ivPortada)
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
        if (song.portada_cancion.isNotEmpty()) {

            try {
                val bytes = Base64.decode(song.portada_cancion, Base64.DEFAULT)

                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                holder.ivPortada.setImageBitmap(bitmap)

            } catch (e: Exception) {

                holder.ivPortada.setImageResource(R.drawable.portadadefault)
            }

        } else {
            holder.ivPortada.setImageResource(R.drawable.portadadefault)
        }
    }

    override fun getItemCount(): Int = songs.size
}