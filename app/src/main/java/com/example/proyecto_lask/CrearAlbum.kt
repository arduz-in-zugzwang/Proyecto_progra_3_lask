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
        etNombre            = findViewById(R.id.etNombreAlbum)
        etDescripcion       = findViewById(R.id.etDescripcionAlbum)
        btnPortada          = findViewById(R.id.cambiarPortada)
        btnInsertarCanciones = findViewById(R.id.btnInsertarCanciones)
        btnPublicar         = findViewById(R.id.btnPublicarAlbum)
        listaCanciones      = findViewById(R.id.listaCanciones)

        // Que la imagen de portada llene el botón correctamente
        btnPortada.scaleType = ImageView.ScaleType.CENTER_CROP
        btnPortada.adjustViewBounds = false
    }

    private fun setupListeners() {
        btnPortada.setOnClickListener { abrirGaleria() }
        btnInsertarCanciones.setOnClickListener { abrirMusica() }
        btnPublicar.setOnClickListener { crearAlbum() }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, 100)
    }

    private fun abrirMusica() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "Selecciona canciones"), 200)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                btnPortada.setImageBitmap(bitmap)
                btnPortada.scaleType = ImageView.ScaleType.CENTER_CROP
                portadaBase64 = convertirBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == 200 && resultCode == RESULT_OK) {
            canciones.clear()
            data?.clipData?.let { clip ->
                for (i in 0 until clip.itemCount) canciones.add(clip.getItemAt(i).uri)
            } ?: data?.data?.let { canciones.add(it) }
            mostrarCanciones()
        }
    }

    private fun mostrarCanciones() {
        listaCanciones.removeAllViews()

        // Mantener el label original
        val label = TextView(this).apply {
            text = "Canciones agregadas"
            setTextColor(0xFFAAAAAA.toInt())
            textSize = 11f
        }
        listaCanciones.addView(label)

        canciones.forEach { uri ->
            val nombre = obtenerNombreCancion(uri)
            val tv = TextView(this).apply {
                text = nombre
                setTextColor(0xFF333333.toInt())
                textSize = 12f
                setPadding(0, 6, 0, 6)
            }
            listaCanciones.addView(tv)
        }
    }

    private fun obtenerNombreCancion(uri: Uri): String {
        return try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                if (idx >= 0) cursor.getString(idx) else uri.lastPathSegment ?: "canción"
            } ?: uri.lastPathSegment ?: "canción"
        } catch (e: Exception) {
            uri.lastPathSegment ?: "canción"
        }
    }

    private fun convertirBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }

    private fun crearAlbum() {
        val nombre = etNombre.text.toString().trim()
        val desc   = etDescripcion.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Escribe el nombre del álbum", Toast.LENGTH_SHORT).show()
            return
        }
        if (desc.isEmpty()) {
            Toast.makeText(this, "Escribe una descripción", Toast.LENGTH_SHORT).show()
            return
        }
        if (portadaBase64.isEmpty()) {
            Toast.makeText(this, "Selecciona una portada", Toast.LENGTH_SHORT).show()
            return
        }

        // Leer id_artista desde SharedPreferences (guardado al hacer login)
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        val idArtista = prefs.getInt("id_artista", -1)
        if (idArtista == -1) {
            Toast.makeText(this, "No se encontró el perfil de artista", Toast.LENGTH_SHORT).show()
            return
        }

        btnPublicar.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create()
                val response = api.createAlbum(
                    nombre_album      = nombre,
                    descripcion_album = desc,
                    portada_album     = portadaBase64,
                    id_artista        = idArtista
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearAlbum, "¡Álbum creado correctamente!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "sin detalle"
                    Toast.makeText(this@CrearAlbum, "Error ${response.code()}: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CrearAlbum, "Fallo de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnPublicar.isEnabled = true
            }
        }
    }
}