package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_lask.artistas.Data
import android.graphics.BitmapFactory
import android.util.Base64


class ArtistAdapter(
    private val artistas: List<Data>,
    private val onClick: (Data) -> Unit = {}  // lambda opcional
) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreArtista)
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artista = artistas[position]
        holder.tvNombre.text = artista.nombre_artistico
        if (!artista.pfp.isNullOrEmpty()) {

            try {
                val bytes = Base64.decode(artista.pfp, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.ivAvatar.setImageBitmap(bitmap)

            } catch (e: Exception) { holder.ivAvatar.setImageResource(R.drawable.artistadefault)
            }

        } else {
            holder.ivAvatar.setImageResource(R.drawable.artistadefault)
        }
        holder.itemView.setOnClickListener { onClick(artista) }
    }

    override fun getItemCount(): Int = artistas.size
}
