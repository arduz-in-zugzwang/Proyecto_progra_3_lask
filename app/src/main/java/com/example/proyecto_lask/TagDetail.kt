package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TagDetail : AppCompatActivity() {

    private lateinit var tvNombreTag: TextView
    private lateinit var rvCancionesTags: RecyclerView
    private lateinit var loguito : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_detail)

        tvNombreTag = findViewById(R.id.tvNombreTag)
        rvCancionesTags = findViewById(R.id.rvCancionesTags)

        loguito=findViewById(R.id.loguito)

        loguito.setOnClickListener {
            finish()
        }

        val idTag = intent.getIntExtra("id_tag", -1)
        val nombreTag = intent.getStringExtra("nombre_tag")

        tvNombreTag.text = nombreTag

        cargarCanciones(idTag)
    }

    private fun cargarCanciones(idTag: Int) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create()
                        .getCancionesPorTag(idTag)

                if (respuesta.isSuccessful) {

                    val canciones =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {

                        rvCancionesTags.layoutManager =
                            LinearLayoutManager(this@TagDetail)

                        rvCancionesTags.adapter =
                            SongAdapter(canciones) { cancion ->

                                val intent = Intent(
                                    this@TagDetail,
                                    DetailSong::class.java
                                )

                                intent.putExtra(
                                    "id_album",
                                    cancion.id_album
                                )

                                startActivity(intent)
                            }
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        this@TagDetail,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}