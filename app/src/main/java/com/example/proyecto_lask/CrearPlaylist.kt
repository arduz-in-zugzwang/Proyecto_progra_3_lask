package com.example.proyecto_lask

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import android.util.Base64

class CrearPlaylist : AppCompatActivity() {

    private lateinit var btnPortada: ImageButton
    private lateinit var etNombre: EditText
    private lateinit var btnInsertarCanciones: Button
    private lateinit var listaCanciones: LinearLayout
    private lateinit var btnPublica: Button
    private lateinit var btnPrivada: Button
    private lateinit var btnCrear: Button

    // true = pública, false = privada (por defecto pública)
    private var esPrivada: Boolean = false
    private val canciones = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_playlist)
        initViews()
        setupListeners()
        actualizarBotonesVisibilidad()
    }

    private fun initViews() {
        btnPortada           = findViewById(R.id.cambiarPortada)
        etNombre             = findViewById(R.id.etNombrePlaylist)
        btnInsertarCanciones = findViewById(R.id.btnInsertarCanciones)
        listaCanciones       = findViewById(R.id.listaCanciones)
        btnPublica           = findViewById(R.id.btnPublica)
        btnPrivada           = findViewById(R.id.btnPrivada)
        btnCrear             = findViewById(R.id.btnCrearPlaylist)

        btnPortada.scaleType = ImageView.ScaleType.CENTER_CROP
        btnPortada.adjustViewBounds = false
    }

    private fun setupListeners() {
        btnPortada.setOnClickListener { abrirGaleria() }
        btnInsertarCanciones.setOnClickListener { abrirMusica() }

        btnPublica.setOnClickListener {
            esPrivada = false
            actualizarBotonesVisibilidad()
        }
        btnPrivada.setOnClickListener {
            esPrivada = true
            actualizarBotonesVisibilidad()
        }

        btnCrear.setOnClickListener { crearPlaylist() }
    }

    // Resalta el botón seleccionado de visibilidad
    private fun actualizarBotonesVisibilidad() {
        if (esPrivada) {
            btnPrivada.backgroundTintList  = android.content.res.ColorStateList.valueOf(0xFF547daf.toInt())
            btnPrivada.setTextColor(0xFFFFFFFF.toInt())
            btnPublica.backgroundTintList  = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
            btnPublica.setTextColor(0xFF888888.toInt())
        } else {
            btnPublica.backgroundTintList  = android.content.res.ColorStateList.valueOf(0xFF547daf.toInt())
            btnPublica.setTextColor(0xFFFFFFFF.toInt())
            btnPrivada.backgroundTintList  = android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
            btnPrivada.setTextColor(0xFF888888.toInt())
        }
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
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
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

        val label = TextView(this).apply {
            text = "Canciones agregadas"
            setTextColor(0xFFAAAAAA.toInt())
            textSize = 11f
        }
        listaCanciones.addView(label)

        canciones.forEach { uri ->
            val nombre = obtenerNombre(uri)
            val tv = TextView(this).apply {
                text = nombre
                setTextColor(0xFF333333.toInt())
                textSize = 12f
                setPadding(0, 6, 0, 6)
            }
            listaCanciones.addView(tv)
        }
    }

    private fun obtenerNombre(uri: Uri): String {
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

    private fun crearPlaylist() {
        val nombre = etNombre.text.toString().trim()

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Escribe el nombre de la playlist", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs    = getSharedPreferences("sesion_lask", MODE_PRIVATE)
        val idUsuario = prefs.getInt("user_id", -1)

        if (idUsuario == -1) {
            Toast.makeText(this, "No se encontró la sesión", Toast.LENGTH_SHORT).show()
            return
        }

        btnCrear.isEnabled = false

        lifecycleScope.launch {
            try {
                val api      = RetrofitClient.create()
                val response = api.createPlaylist(
                    nombre_playlist      = nombre,
                    id_usuario           = idUsuario,
                    privacidad_playlist  = esPrivada
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearPlaylist, "¡Playlist creada!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "sin detalle"
                    Toast.makeText(this@CrearPlaylist, "Error ${response.code()}: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CrearPlaylist, "Fallo de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnCrear.isEnabled = true
            }
        }
    }
}