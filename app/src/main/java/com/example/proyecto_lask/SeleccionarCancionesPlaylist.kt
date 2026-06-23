package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_lask.canciones.CancionSeleccionada
import kotlinx.coroutines.launch

class SeleccionarCancionesPlaylistActivity :
    AppCompatActivity() {

    private lateinit var rvCanciones: RecyclerView
    private lateinit var btnGuardar: Button

    private val canciones =
        mutableListOf<CancionSeleccionada>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_seleccionar_canciones_playlist
        )

        rvCanciones =
            findViewById(R.id.rvCanciones)

        btnGuardar =
            findViewById(R.id.btnGuardar)

        cargarCanciones()

        btnGuardar.setOnClickListener {

            val ids =
                canciones
                    .filter { it.seleccionada }
                    .map { it.cancion.id }

            val intent = Intent()

            intent.putIntegerArrayListExtra(
                "canciones",
                ArrayList(ids)
            )

            setResult(RESULT_OK, intent)

            finish()
        }
    }

    private fun cargarCanciones() {

        lifecycleScope.launch {

            val respuesta =
                RetrofitClient.create()
                    .getCanciones()

            if (respuesta.isSuccessful) {

                val lista =
                    respuesta.body()?.data
                        ?: emptyList()

                canciones.clear()

                canciones.addAll(
                    lista.map {
                        CancionSeleccionada(it)
                    }
                )

                rvCanciones.layoutManager =
                    LinearLayoutManager(this@SeleccionarCancionesPlaylistActivity)

                rvCanciones.adapter =
                    SeleccionarCancionesAdapter(canciones)
            }
        }
    }
}