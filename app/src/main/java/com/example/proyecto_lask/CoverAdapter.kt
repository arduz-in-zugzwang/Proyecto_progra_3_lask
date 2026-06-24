package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter genérico para mostrar una lista horizontal de portadas
 * (se usa tanto para Álbumes como para Canciones en la búsqueda).
 */
class CoverAdapter(private val portadas: List<Int>) :
    RecyclerView.Adapter<CoverAdapter.CoverViewHolder>() {

    class CoverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPortada: ImageView = view.findViewById(R.id.ivPortadaCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CoverViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cover, parent, false)
        return CoverViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        holder.ivPortada.setImageResource(portadas[position])
    }

    override fun getItemCount(): Int = portadas.size
}
