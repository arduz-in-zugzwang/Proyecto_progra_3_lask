package com.example.proyecto_lask

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

// Datos temporales de una canción antes de subir
data class CancionTemporal(
    val nombre: String,
    val portadaBase64: String,
    val pathLink: String,
    val audioUri: String,
    val tags: ArrayList<Int>
)

class CrearAlbum : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnPortada: ImageButton
    private lateinit var btnInsertarCanciones: ImageButton
    private lateinit var btnPublicar: Button
    private lateinit var listaCanciones: LinearLayout
    private lateinit var progressBar: ProgressBar

    private var portadaBase64: String = ""
    // Lista de canciones acumuladas desde CrearCancion
    private val canciones = mutableListOf<CancionTemporal>()

    companion object {
        const val REQUEST_CREAR_CANCION = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_album)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        etNombre             = findViewById(R.id.etNombreAlbum)
        etDescripcion        = findViewById(R.id.etDescripcionAlbum)
        btnPortada           = findViewById(R.id.cambiarPortada)
        btnInsertarCanciones = findViewById(R.id.btnInsertarCanciones)
        btnPublicar          = findViewById(R.id.btnPublicarAlbum)
        listaCanciones       = findViewById(R.id.listaCanciones)
        progressBar          = findViewById(R.id.progressBar)

        btnPortada.scaleType = ImageView.ScaleType.CENTER_CROP
        btnPortada.adjustViewBounds = false
    }

    private fun setupListeners() {
        btnPortada.setOnClickListener { abrirGaleria() }

        btnInsertarCanciones.setOnClickListener {
            // Abre CrearCancion pasando el id_artista
            val prefs     = getSharedPreferences("sesion_lask", MODE_PRIVATE)
            val idArtista = prefs.getInt("artista_id", -1)
            if (idArtista == -1) {
                Toast.makeText(this, "No se encontró el perfil de artista", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CrearCancion::class.java)
            intent.putExtra("id_artista", idArtista)
            startActivityForResult(intent, REQUEST_CREAR_CANCION)
        }

        btnPublicar.setOnClickListener { publicarAlbum() }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, 100)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Portada del álbum
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

        // Canción devuelta desde CrearCancion
        if (requestCode == REQUEST_CREAR_CANCION && resultCode == RESULT_OK) {
            val nombre   = data?.getStringExtra("nombre_cancion") ?: return
            val portada  = data.getStringExtra("portada_cancion") ?: ""
            val pathLink = data.getStringExtra("path_link") ?: ""
            val audioUri = data.getStringExtra("audio_uri") ?: ""
            val tags = data.getIntegerArrayListExtra("tags") ?: arrayListOf()


            canciones.add(CancionTemporal(nombre, portada, pathLink, audioUri,tags))
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

        canciones.forEachIndexed { index, cancion ->
            val tv = TextView(this).apply {
                text = "${index + 1}. ${cancion.nombre}"
                setTextColor(0xFF333333.toInt())
                textSize = 12f
                setPadding(0, 6, 0, 6)
            }
            listaCanciones.addView(tv)
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

    private fun String.toRequestBodyText(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun publicarAlbum() {
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
        if (canciones.isEmpty()) {
            Toast.makeText(this, "Agrega al menos una canción", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs     = getSharedPreferences("sesion_lask", MODE_PRIVATE)
        val idArtista = prefs.getInt("artista_id", -1)
        if (idArtista == -1) {
            Toast.makeText(this, "No se encontró el perfil de artista", Toast.LENGTH_SHORT).show()
            return
        }

        btnPublicar.isEnabled    = false
        progressBar.visibility   = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create()

                // 1. Crear el álbum
                val albumResponse = api.createAlbum(
                    nombre_album      = nombre,
                    descripcion_album = desc,
                    portada_album     = portadaBase64,
                    id_artista        = idArtista
                )

                if (!albumResponse.isSuccessful) {
                    val err = albumResponse.errorBody()?.string() ?: "sin detalle"
                    Toast.makeText(this@CrearAlbum, "Error al crear álbum ${albumResponse.code()}: $err", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val idAlbum = albumResponse.body()?.id ?: run {
                    Toast.makeText(this@CrearAlbum, "No se obtuvo el ID del álbum", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2. Subir cada canción con el id_album recién creado
                var cancionesOk = 0
                var cancionesFallidas = 0

                canciones.forEach { cancion ->

                    val uri = Uri.parse(cancion.audioUri)

                    val inputStream = contentResolver.openInputStream(uri)
                        ?: throw Exception("No se pudo abrir el audio")

                    val tempFile = File.createTempFile("audio", ".mp3", cacheDir)

                    tempFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                    }

                    val audioBody = tempFile.asRequestBody(
                        "audio/mpeg".toMediaTypeOrNull()
                    )

                    val audioPart = MultipartBody.Part.createFormData(
                        "audio",
                        tempFile.name,
                        audioBody
                    )

                    val cancionResponse = api.createCancion(
                        id_album = idAlbum.toString().toRequestBodyText(),
                        id_artista = idArtista.toString().toRequestBodyText(),
                        nombre_cancion = cancion.nombre.toRequestBodyText(),
                        portada_cancion = cancion.portadaBase64.toRequestBodyText(),
                        audio = audioPart
                    )

                    if (cancionResponse.isSuccessful)
                        cancionesOk++
                    else
                        cancionesFallidas++
                }

                // 3. Resultado final
                val msg = if (cancionesFallidas == 0) {
                    "¡Álbum creado con $cancionesOk canción(es)!"
                } else {
                    "Álbum creado. $cancionesOk canción(es) subidas, $cancionesFallidas fallaron."
                }
                Toast.makeText(this@CrearAlbum, msg, Toast.LENGTH_LONG).show()
                finish()

            } catch (e: Exception) {
                e.printStackTrace()

                Toast.makeText(
                    this@CrearAlbum,
                    e.toString(),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnPublicar.isEnabled  = true
                progressBar.visibility = View.GONE
            }
        }
    }
}