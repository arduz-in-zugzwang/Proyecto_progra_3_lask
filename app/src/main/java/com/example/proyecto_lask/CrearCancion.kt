package com.example.proyecto_lask

import android.app.Activity
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
    private lateinit var btnAgregar: Button
    private lateinit var listaTags: LinearLayout

    private var portadaBase64: String = ""
    private var audioUri: Uri? = null
    private var audioPathLink: String = ""
    private var idArtista: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cancion)

        // Recibir id_artista desde CrearAlbum
        idArtista = intent.getIntExtra("id_artista", -1)

        initViews()
        setupListeners()
        cargarTags()
    }

    private fun initViews() {
        btnPortada      = findViewById(R.id.btnPortada)
        etNombreCancion = findViewById(R.id.etNombreCancion)
        btnAudio        = findViewById(R.id.btnAudio)
        btnAgregar      = findViewById(R.id.btnSubirCancion)
        listaTags       = findViewById(R.id.listaTags)

        btnPortada.scaleType = ImageView.ScaleType.CENTER_CROP
        btnPortada.adjustViewBounds = false

        // Cambiar texto del botón — ahora agrega, no sube
        btnAgregar.text = "Agregar canción"
    }

    private fun setupListeners() {
        btnPortada.setOnClickListener { abrirGaleria() }
        btnAudio.setOnClickListener { abrirAudio() }
        btnAgregar.setOnClickListener { agregarCancion() }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, 100)
    }

    private fun abrirAudio() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, 200)
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
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
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
        val maxSize = 500
        val scale   = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height, 1f)
        val resized = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else bitmap

        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 60, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    private fun cargarTags() {
        lifecycleScope.launch {
            try {
                val api      = RetrofitClient.create()
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
                // No bloquear si falla cargar tags
            }
        }
    }

    private fun agregarCancion() {
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
        val tagsSeleccionados = arrayListOf<Int>()

        for (i in 0 until listaTags.childCount) {

            val cb = listaTags.getChildAt(i) as CheckBox

            if (cb.isChecked) {
                tagsSeleccionados.add(cb.tag as Int)
            }
        }

        // Devolver datos a CrearAlbum sin subir a la API todavía
        val result = Intent().apply {
            putExtra("nombre_cancion",  nombre)
            putExtra("portada_cancion", portadaBase64)
            putExtra("path_link",       audioPathLink)
            putExtra("audio_uri", audioUri?.toString())
            putIntegerArrayListExtra("tags", tagsSeleccionados)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}