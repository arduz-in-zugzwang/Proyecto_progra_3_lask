package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_lask.comentarios.DataComentario

class ComentarioAdapter(
    private val lista: List<DataComentario>,
    private val nombres: Map<String, String> // id_usuario -> nombre
) : RecyclerView.Adapter<ComentarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvComentarioUsuario)
        val tvTexto: TextView   = view.findViewById(R.id.tvComentarioTexto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val c = lista[position]
        holder.tvUsuario.text = nombres[c.id_usuario] ?: "Usuario"
        holder.tvTexto.text   = c.texto
    }

    override fun getItemCount() = lista.size
}