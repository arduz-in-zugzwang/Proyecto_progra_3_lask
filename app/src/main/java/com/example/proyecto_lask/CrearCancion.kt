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

class CrearCancion : AppCompatActivity() {

    private lateinit var btnPortada: ImageButton
    private lateinit var etNombreCancion: EditText
    private lateinit var btnAudio: Button
    private lateinit var btnSubirCancion: Button
    private lateinit var listaTags: LinearLayout

    private var portadaBase64: String = ""
    private var audioUri: Uri? = null
    private var audioPathLink: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cancion)
        initViews()
        setupListeners()
        cargarTags()
    }

    private fun initViews() {
        btnPortada      = findViewById(R.id.btnPortada)
        etNombreCancion = findViewById(R.id.etNombreCancion)
        btnAudio        = findViewById(R.id.btnAudio)
        btnSubirCancion = findViewById(R.id.btnSubirCancion)
        listaTags       = findViewById(R.id.listaTags)

        btnPortada.scaleType = ImageView.ScaleType.CENTER_CROP
        btnPortada.adjustViewBounds = false
    }

    private fun setupListeners() {
        btnPortada.setOnClickListener { abrirGaleria() }
        btnAudio.setOnClickListener { abrirAudio() }
        btnSubirCancion.setOnClickListener { subirCancion() }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, 100)
    }

    private fun abrirAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "audio/*" }
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo de audio"), 200)
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
            val uri = data?.data ?: return
            audioUri = uri
            audioPathLink = obtenerNombreArchivo(uri)
            btnAudio.text = audioPathLink
        }
    }

    private fun obtenerNombreArchivo(uri: Uri): String {
        return try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                if (idx >= 0) cursor.getString(idx) else uri.lastPathSegment ?: "audio"
            } ?: uri.lastPathSegment ?: "audio"
        } catch (e: Exception) {
            uri.lastPathSegment ?: "audio"
        }
    }

    private fun convertirBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }

    // Carga los tags disponibles desde la API y muestra checkboxes
    private fun cargarTags() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create()
                val response = api.getTags()
                if (response.isSuccessful) {
                    val tags = response.body()?.data ?: return@launch
                    listaTags.removeAllViews()
                    tags.forEach { tag ->
                        val cb = CheckBox(this@CrearCancion).apply {
                            text = tag.nombre_tag
                            setTextColor(0xFF333333.toInt())
                            textSize = 12f
                            setTag(tag.id)
                        }
                        listaTags.addView(cb)
                    }
                }
            } catch (e: Exception) {
                // Si falla cargar tags, no bloquear el flujo
            }
        }
    }

    private fun subirCancion() {
        val nombre = etNombreCancion.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Escribe el nombre de la canción", Toast.LENGTH_SHORT).show()
            return
        }
        if (portadaBase64.isEmpty()) {
            Toast.makeText(this, "Selecciona una portada", Toast.LENGTH_SHORT).show()
            return
        }
        if (audioUri == null) {
            Toast.makeText(this, "Selecciona un archivo de audio", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("sesion_lask", MODE_PRIVATE)
        val idArtista = prefs.getInt("artista_id", -1)
        val idAlbum   = prefs.getInt("id_album", -1)  // Si viene desde un álbum específico

        if (idArtista == -1) {
            Toast.makeText(this, "No se encontró el perfil de artista", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubirCancion.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create()
                val response = api.createCancion(
                    id_album        = if (idAlbum != -1) idAlbum else 0,
                    id_artista      = idArtista,
                    nombre_cancion  = nombre,
                    portada_cancion = portadaBase64,
                    path_link       = audioPathLink
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearCancion, "¡Canción subida correctamente!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "sin detalle"
                    Toast.makeText(this@CrearCancion, "Error ${response.code()}: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CrearCancion, "Fallo de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnSubirCancion.isEnabled = true
            }
        }
    }
}