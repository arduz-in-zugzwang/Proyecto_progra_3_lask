package com.example.proyecto_lask

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_lask.canciones.DataX as CancionData
import kotlinx.coroutines.launch

class AlbumDetail : AppCompatActivity() {

    private lateinit var imgPortada: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var listaCanciones: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)
        initViews()

        val idAlbum = intent.getIntExtra("id_album", -1)
        if (idAlbum == -1) {
            Toast.makeText(this, "Álbum no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarAlbum(idAlbum)
    }

    private fun initViews() {
        imgPortada     = findViewById(R.id.imgPortadaAlbum)
        tvNombre       = findViewById(R.id.tvNombreAlbum)
        tvDescripcion  = findViewById(R.id.tvDescripcionAlbum)
        listaCanciones = findViewById(R.id.listaCanciones)
        progressBar    = findViewById(R.id.progressBar)
    }

    private fun cargarAlbum(idAlbum: Int) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create()

                val albumResponse     = api.getAlbum(idAlbum)
                val cancionesResponse = api.getCanciones()

                if (albumResponse.isSuccessful) {
                    val album = albumResponse.body()!!

                    tvNombre.text      = album.nombre_album
                    tvDescripcion.text = album.descripcion_album

                    if (album.portada_album.isNotEmpty()) {
                        try {
                            val bytes  = Base64.decode(album.portada_album, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            imgPortada.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            imgPortada.setImageResource(R.drawable.borde_redondeado)
                        }
                    }
                } else {
                    Toast.makeText(this@AlbumDetail, "Error cargando álbum ${albumResponse.code()}", Toast.LENGTH_SHORT).show()
                }

                if (cancionesResponse.isSuccessful) {
                    val todasCanciones = cancionesResponse.body()?.data ?: emptyList()
                    val cancionesAlbum = todasCanciones.filter { it.id_album == idAlbum }
                    mostrarCanciones(cancionesAlbum)
                }

            } catch (e: Exception) {
                Toast.makeText(this@AlbumDetail, "Fallo de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun mostrarCanciones(canciones: List<CancionData>) {
        listaCanciones.removeAllViews()

        if (canciones.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Este álbum no tiene canciones aún"
                setTextColor(0xFF888888.toInt())
                textSize = 13f
                setPadding(0, 8, 0, 8)
            }
            listaCanciones.addView(tv)
            return
        }

        canciones.forEachIndexed { index, cancion ->
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 10, 0, 10)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                isClickable = true
                isFocusable = true
                setBackgroundResource(
                    android.R.drawable.list_selector_background
                )
            }


            // Número de pista
            val tvNumero = TextView(this).apply {
                text = "${index + 1}."
                setTextColor(0xFF888888.toInt())
                textSize = 13f
                setPadding(0, 0, 10, 0)
                minWidth = 28
            }

            // Portada miniatura
            val imgMini = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(40.dp, 40.dp).also {
                    it.marginEnd = 10.dp
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setBackgroundResource(R.drawable.borde_redondeado)
                clipToOutline = true

                if (cancion.portada_cancion.isNotEmpty()) {
                    try {
                        val bytes  = Base64.decode(cancion.portada_cancion, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        setImageBitmap(bitmap)
                    } catch (e: Exception) { /* sin portada */ }
                }
            }

            // Nombre canción
            val tvNombreCancion = TextView(this).apply {
                text = cancion.nombre_cancion
                setTextColor(0xFF000000.toInt())
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            fila.addView(tvNumero)
            fila.addView(imgMini)
            fila.addView(tvNombreCancion)
            fila.setOnClickListener {

                val intent = Intent(
                    this@AlbumDetail,
                    DetailSong::class.java
                )

                intent.putExtra(
                    "id_album",
                    cancion.id_album
                )

                intent.putExtra(
                    "id_cancion",
                    cancion.id
                )

                startActivity(intent)
            }
            listaCanciones.addView(fila)

            // Separador entre canciones
            if (index < canciones.size - 1) {
                val sep = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                    )
                    setBackgroundColor(0x22000000)
                }
                listaCanciones.addView(sep)
            }
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}