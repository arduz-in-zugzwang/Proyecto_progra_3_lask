package com.example.proyecto_lask

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CrearAlbum : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnPortada: ImageButton
    private lateinit var btnInsertarCanciones: Button
    private lateinit var btnPublicar: Button
    private lateinit var listaCanciones: LinearLayout

    private var portadaBase64: String = ""

    private val canciones = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_album)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etNombre = findViewById(R.id.etNombreAlbum)
        etDescripcion = findViewById(R.id.etDescripcionAlbum)
        btnPortada = findViewById(R.id.cambiarPortada)
        btnInsertarCanciones = findViewById(R.id.btnInsertarCanciones)
        btnPublicar = findViewById(R.id.btnPublicarAlbum)
        listaCanciones = findViewById(R.id.listaCanciones)
    }

    private fun setupListeners() {

        btnPortada.setOnClickListener {
            abrirGaleria()
        }

        btnInsertarCanciones.setOnClickListener {
            abrirMusica()
        }

        btnPublicar.setOnClickListener {
            crearAlbum()
        }
    }

    // PORTADA
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    // MUSICA
    private fun abrirMusica() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Selecciona canciones"), 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // PORTADA
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            btnPortada.setImageBitmap(bitmap)
            portadaBase64 = convertir(bitmap)
        }

        // CANCIONES
        if (requestCode == 200 && resultCode == RESULT_OK) {

            canciones.clear()

            data?.clipData?.let {
                for (i in 0 until it.itemCount) {
                    canciones.add(it.getItemAt(i).uri)
                }
            } ?: data?.data?.let {
                canciones.add(it)
            }

            mostrarCanciones()
        }
    }

    private fun mostrarCanciones() {
        listaCanciones.removeAllViews()

        canciones.forEach {
            val tv = TextView(this)
            tv.text = it.lastPathSegment ?: "canción"
            tv.setPadding(0, 8, 0, 8)
            listaCanciones.addView(tv)
        }
    }

    private fun convertir(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }

    private fun crearAlbum() {

        val nombre = etNombre.text.toString()
        val desc = etDescripcion.text.toString()

        if (nombre.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {

                val api = RetrofitClient.create()

                val response = api.createAlbum(
                    nombre_album = nombre,
                    descripcion_album = desc,
                    portada_album = portadaBase64,
                    id_artista = 1
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearAlbum, "Álbum creado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CrearAlbum, "Error", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@CrearAlbum, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}