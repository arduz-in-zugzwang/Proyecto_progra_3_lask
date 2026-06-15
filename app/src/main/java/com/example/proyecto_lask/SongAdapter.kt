package com.example.proyecto_lask

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private val songs: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var posicionSonando: Int = -1

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPortada: ImageView = view.findViewById(R.id.ivPortada)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreCancion)
        val tvArtista: TextView = view.findViewById(R.id.tvArtista)
        val ivPlay: ImageView = view.findViewById(R.id.ivPlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val song = songs[position]
        holder.tvNombre.text = song.nombre
        holder.tvArtista.text = song.artista
        holder.ivPortada.setImageResource(song.portadaResId)

        val estaSonando = posicionSonando == position
        holder.ivPlay.setImageResource(
            if (estaSonando) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )

        holder.ivPlay.setOnClickListener {
            val context = holder.itemView.context

            if (estaSonando) {
                // Si ya está sonando esta canción, la detenemos
                detenerReproduccion()
                notifyItemChanged(position)
            } else {
                val anterior = posicionSonando

                // Si había otra canción sonando, la detenemos primero
                detenerReproduccion()

                mediaPlayer = MediaPlayer.create(context, song.audioResId)
                mediaPlayer?.start()
                posicionSonando = position

                if (anterior != -1) notifyItemChanged(anterior)
                notifyItemChanged(position)

                mediaPlayer?.setOnCompletionListener {
                    posicionSonando = -1
                    notifyItemChanged(position)
                }
            }
        }
    }

    private fun detenerReproduccion() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        posicionSonando = -1
    }

    override fun getItemCount(): Int = songs.size
}