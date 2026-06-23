package com.example.proyecto_lask

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.proyecto_lask.albumes.Data
import android.util.Base64


class AlbumAdapter(

    private val albumes: List<Data>,
    private val onClick: (Data) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlbum)

        val ivPortadaAlbum: ImageView = view.findViewById(R.id.ivPortadaAlbum)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)

        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int
    ) {

        val album = albumes[position]

        holder.tvNombre.text = album.nombre_album
        if (album.portada_album.isNotEmpty()) {

            try {

                val bytes = Base64.decode(album.portada_album, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.ivPortadaAlbum.setImageBitmap(bitmap)

            } catch (e: Exception) {
                holder.ivPortadaAlbum.setImageResource(R.drawable.portadadefault)
            }

        } else {
            holder.ivPortadaAlbum.setImageResource(R.drawable.portadadefault)
        }
        holder.itemView.setOnClickListener { onClick(album) }
    }

    override fun getItemCount(): Int =
        albumes.size
}