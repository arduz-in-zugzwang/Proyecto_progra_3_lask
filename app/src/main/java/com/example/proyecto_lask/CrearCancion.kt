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

    private lateinit var etNombre: EditText
    private lateinit var btnPortada: ImageButton
    private lateinit var btnAudio: Button
    private lateinit var btnSubir: Button

    private var portadaBase64: String = ""
    private var audioUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cancion)

        etNombre = findViewById(R.id.etNombreCancion)
        btnPortada = findViewById(R.id.btnPortada)
        btnAudio = findViewById(R.id.btnAudio)
        btnSubir = findViewById(R.id.btnSubirCancion)

        btnPortada.setOnClickListener { abrirImagen() }
        btnAudio.setOnClickListener { abrirAudio() }
        btnSubir.setOnClickListener { subirCancion() }
    }

    // IMAGEN
    private fun abrirImagen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    // AUDIO
    private fun abrirAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val uri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            btnPortada.setImageBitmap(bitmap)
            portadaBase64 = convertir(bitmap)
        }

        if (requestCode == 200) {
            audioUri = data?.data
            Toast.makeText(this, "MP3 seleccionado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertir(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }

    private fun subirCancion() {

        val nombre = etNombre.text.toString().trim()

        if (nombre.isEmpty() || audioUri == null) {
            Toast.makeText(this, "Completa todo", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {

                val api = RetrofitClient.create()

                val response = api.createCancion(
                    id_album = 1, // 👈 luego lo pasas por intent
                    id_artista = 1,
                    nombre_cancion = nombre,
                    portada_cancion = portadaBase64,
                    path_link = audioUri.toString()
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@CrearCancion, "Subida OK", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CrearCancion, "Error", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@CrearCancion, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}