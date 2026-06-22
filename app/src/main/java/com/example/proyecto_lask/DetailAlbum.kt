package com.example.proyecto_lask

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.util.Base64

class DetailAlbumActivity : AppCompatActivity() {

    private lateinit var imgPortada: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var listaCanciones: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_album)

        initViews()
        cargarDatos()
    }

    private fun initViews() {
        imgPortada = findViewById(R.id.imgPortadaAlbum)
        tvNombre = findViewById(R.id.tvNombreAlbum)
        tvDescripcion = findViewById(R.id.tvDescripcionAlbum)
        listaCanciones = findViewById(R.id.listaCanciones)
    }

    private fun cargarDatos() {

        val nombre = intent.getStringExtra("nombre") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val portadaBase64 = intent.getStringExtra("portada") ?: ""
        val canciones = intent.getStringArrayListExtra("canciones") ?: arrayListOf()

        tvNombre.text = nombre
        tvDescripcion.text = descripcion

        // portada
        if (portadaBase64.isNotEmpty()) {
            val bytes = Base64.decode(portadaBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imgPortada.setImageBitmap(bitmap)
        }

        // canciones
        listaCanciones.removeAllViews()

        for (cancion in canciones) {
            val tv = TextView(this)
            tv.text = cancion
            tv.setPadding(0, 10, 0, 10)
            listaCanciones.addView(tv)
        }
    }
}