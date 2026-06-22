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

    private var imageUri: Uri? = null
    private var portadaBase64: String = ""

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnPublicar: Button
    private lateinit var btnPortada: ImageButton
    private lateinit var btnInsertarCanciones: Button
    private lateinit var listaCanciones: LinearLayout

    private val cancionesSeleccionadas = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_album)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etNombre = findViewById(R.id.etNombreAlbum)
        etDescripcion = findViewById(R.id.etDescripcionAlbum)
        btnPublicar = findViewById(R.id.btnPublicarAlbum)
        btnPortada = findViewById(R.id.cambiarPortada)
        btnInsertarCanciones = findViewById(R.id.btnInsertarCanciones)

        // IMPORTANTE: este ID debe existir en tu XML
        listaCanciones = findViewById(R.id.listaCanciones)
    }

    private fun setupListeners() {

        // PORTADA
        btnPortada.setOnClickListener {
            abrirGaleria()
        }

        // CANCIONES
        btnInsertarCanciones.setOnClickListener {
            abrirMusica()
        }

        // CREAR ÁLBUM
        btnPublicar.setOnClickListener {
            crearAlbum()
        }
    }

    // -------------------------
    // GALERÍA PORTADA
    // -------------------------
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    // -------------------------
    // MÚSICA MP3
    // -------------------------
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

            imageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            btnPortada.setImageBitmap(bitmap)
            portadaBase64 = convertirABase64(bitmap)
        }

        // CANCIONES
        if (requestCode == 200 && resultCode == RESULT_OK) {

            cancionesSeleccionadas.clear()

            if (data?.clipData != null) {

                val count = data.clipData!!.itemCount

                for (i in 0 until count) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    cancionesSeleccionadas.add(uri)
                }

            } else if (data?.data != null) {
                cancionesSeleccionadas.add(data.data!!)
            }

            mostrarCanciones()
        }
    }

    // -------------------------
    // MOSTRAR CANCIONES EN UI
    // -------------------------
    private fun mostrarCanciones() {

        listaCanciones.removeAllViews()

        for (uri in cancionesSeleccionadas) {

            val textView = TextView(this)

            val nombre = uri.lastPathSegment ?: "Canción"

            textView.text = nombre
            textView.setPadding(0, 8, 0, 8)

            listaCanciones.addView(textView)
        }
    }

    // -------------------------
    // CONVERTIR IMAGEN
    // -------------------------
    private fun convertirABase64(bitmap: Bitmap): String {

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // -------------------------
    // CREAR ÁLBUM
    // -------------------------
    private fun crearAlbum() {

        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "Ingresa nombre"
            return
        }

        if (descripcion.isEmpty()) {
            etDescripcion.error = "Ingresa descripción"
            return
        }

        val idArtista = 1

        lifecycleScope.launch {
            try {

                val api = RetrofitClient.create()

                val response = api.createAlbum(
                    nombre_album = nombre,
                    descripcion_album = descripcion,
                    portada_album = portadaBase64,
                    id_artista = idArtista
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearAlbum, "Álbum creado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CrearAlbum, "Error al crear álbum", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@CrearAlbum, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}