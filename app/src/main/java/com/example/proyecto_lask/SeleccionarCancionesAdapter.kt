package com.example.proyecto_lask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_lask.canciones.CancionSeleccionada

class SeleccionarCancionesAdapter(
    private val canciones: List<CancionSeleccionada>
) : RecyclerView.Adapter<SeleccionarCancionesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox =
            view.findViewById(R.id.cbCancion)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_cancion_playlist,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = canciones[position]

        holder.checkBox.text =
            item.cancion.nombre_cancion

        holder.checkBox.isChecked =
            item.seleccionada

        holder.checkBox.setOnCheckedChangeListener {
                _,
                checked ->

            item.seleccionada = checked
        }
    }

    override fun getItemCount() =
        canciones.size
}