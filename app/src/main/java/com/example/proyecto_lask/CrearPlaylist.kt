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
    private val cancionesSeleccionadas = mutableListOf<Int>()

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
        btnInsertarCanciones.setOnClickListener {
            cargarCancionesServidor() }

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

    private fun cargarCancionesServidor() {

        lifecycleScope.launch {
            try {
                val response =
                    RetrofitClient.create().getCanciones()
                if (response.isSuccessful) {
                    val canciones =
                        response.body()?.data ?: emptyList()
                    listaCanciones.removeAllViews()
                    canciones.forEach { cancion ->
                        val cb = CheckBox(this@CrearPlaylist)
                        cb.text = "${cancion.nombre_cancion} - ${cancion.nombre_artistico}"
                        cb.setOnCheckedChangeListener { _, checked ->
                            if (checked) {
                                cancionesSeleccionadas.add(cancion.id)
                            } else {
                                cancionesSeleccionadas.remove(cancion.id)
                            }
                        }
                        listaCanciones.addView(cb)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CrearPlaylist,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, 100)
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
                    nombre_playlist = nombre,
                    id_usuario = idUsuario,
                    privacidad_playlist = if (esPrivada) 1 else 0
                )

                if (response.isSuccessful) {

                    val idPlaylist = response.body()?.id ?: run {

                        Toast.makeText(
                            this@CrearPlaylist,
                            "No se obtuvo el ID de la playlist",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@launch
                    }

                    for (idCancion in cancionesSeleccionadas) {

                        api.createPlaylistCancion(
                            idPlaylist,
                            idCancion
                        )
                    }

                    Toast.makeText(
                        this@CrearPlaylist,
                        "¡Playlist creada con ${cancionesSeleccionadas.size} canciones!",
                        Toast.LENGTH_LONG
                    ).show()

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