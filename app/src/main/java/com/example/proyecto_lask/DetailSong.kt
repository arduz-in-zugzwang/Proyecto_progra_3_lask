package com.example.proyecto_lask

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyecto_lask.canciones.DataX as CancionData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class DetailSong : AppCompatActivity() {

    // Views
    private lateinit var imgAlbum: CircleImageView
    private lateinit var tvArtista: TextView
    private lateinit var songName: TextView
    private lateinit var tvNombreAlbum: TextView
    private lateinit var tvTagSong: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var tvTiempoActual: TextView
    private lateinit var tvDuracion: TextView
    private lateinit var playBtn: ImageButton
    private lateinit var backSong: ImageButton
    private lateinit var nextSong: ImageButton
    private lateinit var btnVerLetra: Button
    private lateinit var logouito: ImageView

    // Reproductor
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    // Lista de canciones y posición actual
    private var listaCanciones: MutableList<CancionData> = mutableListOf()
    private var posicionActual: Int = 0

    // Animación de rotación
    private val rotacion by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotation)
    }
    private var animacionActiva = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_song)
        initViews()
        btnVerLetra.setOnClickListener {

            if (listaCanciones.isEmpty()) return@setOnClickListener

            val cancion = listaCanciones[posicionActual]

            val intent =
                Intent(
                    this,
                    DetailLetra::class.java
                )

            intent.putExtra(
                "id_cancion",
                cancion.id
            )

            intent.putExtra(
                "nombre_cancion",
                cancion.nombre_cancion
            )

            startActivity(intent)
        }
        logouito=findViewById(R.id.loguito)

        logouito.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Recibir datos desde el Intent
        val idAlbum    = intent.getIntExtra("id_album", -1)

        if (idAlbum == -1) {
            Toast.makeText(this, "Error al abrir canción", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarCancionesDelAlbum(idAlbum)
    }

    private fun initViews() {
        imgAlbum       = findViewById(R.id.imgAlbum)
        tvArtista      = findViewById(R.id.tvArtista)
        songName       = findViewById(R.id.songName)
        tvNombreAlbum  = findViewById(R.id.tvNombreAlbum)
        tvTagSong      = findViewById(R.id.tvTagSong)
        seekBar        = findViewById(R.id.seekBar)
        tvTiempoActual = findViewById(R.id.tvTiempoActual)
        tvDuracion     = findViewById(R.id.tvDuracion)
        playBtn        = findViewById(R.id.playBtn)
        backSong       = findViewById(R.id.backSong)
        nextSong       = findViewById(R.id.nextSong)
        btnVerLetra = findViewById(R.id.btnVerLetra)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
    }

    // ---------------- CARGA DE DATOS ----------------

    private fun cargarCancionesDelAlbum(idAlbum: Int) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create()
                val response = api.getCanciones()
                if (response.isSuccessful) {
                    val todas = response.body()?.data ?: emptyList()
                    listaCanciones = todas.filter { it.id_album == idAlbum }.toMutableList()
//                    esto agrgue
                    val idCancion =
                        intent.getIntExtra(
                            "id_cancion",
                            -1
                        )

                    posicionActual =
                        listaCanciones.indexOfFirst {
                            it.id == idCancion
                        }

                    if (posicionActual == -1) {
                        posicionActual = 0
                    }
//                    hasta aqui

                    if (listaCanciones.isEmpty()) {
                        Toast.makeText(this@DetailSong, "No hay canciones en este álbum", Toast.LENGTH_SHORT).show()
                        finish()
                        return@launch
                    }

                    // Cargar también el nombre del álbum
                    val albumResponse = api.getAlbum(idAlbum)
                    if (albumResponse.isSuccessful) {
                        tvNombreAlbum.text = albumResponse.body()?.nombre_album ?: ""
                    }

                    mostrarCancion(posicionActual)
                    reproducir()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailSong, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarCancion(pos: Int) {
        val cancion = listaCanciones[pos]
        songName.text = cancion.nombre_cancion

        // Mostrar tag si existe
        if (!cancion.nombre_tag.isNullOrEmpty()) {
            tvTagSong.text = cancion.nombre_tag
            tvTagSong.visibility = android.view.View.VISIBLE
        } else {
            // Intentar obtener del intent si vinimos de un tag específico
            val idTagOrigen = intent.getIntExtra("id_tag_origen", -1)
            if (idTagOrigen != -1) {
                obtenerNombreTag(idTagOrigen)
            } else {
                tvTagSong.visibility = android.view.View.GONE
            }
        }

        // Portada en Base64
        if (cancion.portada_cancion.isNotEmpty()) {
            try {
                val bytes  = Base64.decode(cancion.portada_cancion, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgAlbum.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imgAlbum.setImageResource(R.drawable.album_cover)
            }
        } else {
            imgAlbum.setImageResource(R.drawable.album_cover)
        }

        // Artista (si lo tienes guardado en sesión)
        val prefs = getSharedPreferences("sesion_lask", MODE_PRIVATE)
        tvArtista.text = prefs.getString("user_name", "") ?: ""
    }

    private fun obtenerNombreTag(idTag: Int) {
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.create().getTag(idTag)
                if (resp.isSuccessful) {
                    val tag = resp.body()
                    if (tag != null) {
                        tvTagSong.text = tag.nombre_tag
                        tvTagSong.visibility = android.view.View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                // silencioso
            }
        }
    }

    // REPRODUCTOR

    private fun reproducir() {
        val cancion = listaCanciones[posicionActual]
            // aqui ojo con la IP
        val url = "http://192.168.1.9/lask_bd/public/" + cancion.path_link

        if (url.isEmpty()) {
            Toast.makeText(this, "Esta canción no tiene archivo de audio", Toast.LENGTH_SHORT).show()
            return
        }

        // Detener y liberar el player anterior
        detenerYLiberar()

        try {
            mediaPlayer = MediaPlayer().apply {



                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    seekBar.max = mp.duration
                    tvDuracion.text = formatearTiempo(mp.duration)
                    iniciarActualizacionSeekBar()
                    iniciarRotacion()
                    actualizarIconoPlay(true)

                    setOnCompletionListener {
                        // Al terminar pasa a la siguiente automáticamente
                        siguienteCancion()
                    }
                }
                setOnErrorListener { _, what, extra ->

                    Toast.makeText(
                        this@DetailSong,
                        "Error MediaPlayer what=$what extra=$extra",
                        Toast.LENGTH_LONG
                    ).show()
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()

            Toast.makeText(
                this,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ---------------- BOTONES ----------------

    fun PlayButton(view: android.view.View) {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            detenerRotacion()
            actualizarIconoPlay(false)
        } else {
            mp.start()
            iniciarRotacion()
            actualizarIconoPlay(true)
        }
    }

    fun NextSong(view: android.view.View) {
        siguienteCancion()
    }

    fun BackSong(view: android.view.View) {
        // Si llevamos más de 3 seg, vuelve al inicio de la canción; si no, canción anterior
        val mp = mediaPlayer
        if (mp != null && mp.currentPosition > 3000) {
            mp.seekTo(0)
        } else {
            if (posicionActual > 0) {
                posicionActual--
            } else {
                posicionActual = listaCanciones.size - 1
            }
            mostrarCancion(posicionActual)
            reproducir()
        }
    }

    private fun siguienteCancion() {
        if (posicionActual < listaCanciones.size - 1) {
            posicionActual++
        } else {
            posicionActual = 0
        }
        mostrarCancion(posicionActual)
        reproducir()
    }

    // ---------------- ANIMACIÓN ----------------

    private fun iniciarRotacion() {
        if (!animacionActiva) {
            imgAlbum.startAnimation(rotacion)
            animacionActiva = true
        }
    }

    private fun detenerRotacion() {
        imgAlbum.clearAnimation()
        animacionActiva = false
    }

    // ---------------- SEEKBAR ----------------

    private val actualizarSeekBar = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    seekBar.progress = mp.currentPosition
                    tvTiempoActual.text = formatearTiempo(mp.currentPosition)
                }
            }
            handler.postDelayed(this, 500)
        }
    }

    private fun iniciarActualizacionSeekBar() {
        handler.removeCallbacks(actualizarSeekBar)
        handler.post(actualizarSeekBar)
    }

    // ---------------- UTILIDADES ----------------

    private fun actualizarIconoPlay(reproduciendo: Boolean) {
        if (reproduciendo) {
            playBtn.setImageResource(R.drawable.outline_pause_circle_24)
        } else {
            playBtn.setImageResource(R.drawable.outline_play_circle_24)
        }
    }

    private fun formatearTiempo(ms: Int): String {
        val seg = ms / 1000
        val min = seg / 60
        val sec = seg % 60
        return "%d:%02d".format(min, sec)
    }

    private fun detenerYLiberar() {
        handler.removeCallbacks(actualizarSeekBar)
        detenerRotacion()
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        seekBar.progress = 0
        tvTiempoActual.text = "0:00"
        tvDuracion.text = "0:00"
    }

    // ---------------- CICLO DE VIDA ----------------
    // ---------------- CICLO DE VIDA ----------------

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        detenerRotacion()
        actualizarIconoPlay(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        detenerYLiberar()
    }
}