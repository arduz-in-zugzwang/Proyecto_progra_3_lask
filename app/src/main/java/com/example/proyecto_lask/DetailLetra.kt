package com.example.proyecto_lask

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DetailLetra : AppCompatActivity() {

    private lateinit var btnBack: ImageButton

    private lateinit var tvNombreCancion: TextView
    private lateinit var tvLetraNormal: TextView
    private lateinit var tvLetraFonetica: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_letra)

        btnBack = findViewById(R.id.btnBack)

        tvNombreCancion = findViewById(R.id.tvNombreCancion)
        tvLetraNormal = findViewById(R.id.tvLetraNormal)
        tvLetraFonetica = findViewById(R.id.tvLetraFonetica)

        btnBack.setOnClickListener {
            finish()
        }

        val idCancion =
            intent.getIntExtra(
                "id_cancion",
                -1
            )

        val nombreCancion =
            intent.getStringExtra(
                "nombre_cancion"
            ) ?: "Canción"

        tvNombreCancion.text = nombreCancion

        cargarLetra(idCancion)
    }

    private fun cargarLetra(idCancion: Int) {

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .create()
                        .getLetras()

                if (response.isSuccessful) {

                    val letra =
                        response.body()
                            ?.data
                            ?.find {
                                it.id_cancion == idCancion
                            }

                    if (letra != null) {

                        tvLetraNormal.text =
                            if (letra.letra_cancion.isBlank())
                                "No hay letra registrada."
                            else
                                letra.letra_cancion

                        tvLetraFonetica.text =
                            if (letra.texto_fonetico.isBlank())
                                "No hay letra fonética registrada."
                            else
                                letra.texto_fonetico

                    } else {

                        tvLetraNormal.text =
                            "No hay letra registrada."

                        tvLetraFonetica.text =
                            "No hay letra fonética registrada."
                    }

                } else {

                    Toast.makeText(
                        this@DetailLetra,
                        "Error al cargar letra",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@DetailLetra,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}