package com.example.proyecto_lask

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private lateinit var rvPlaylistsPublicas: RecyclerView
    private lateinit var rvPlaylistsPrivadas: RecyclerView
    private lateinit var rvMisCanciones: RecyclerView
    private lateinit var rvMisAlbumes: RecyclerView
    private lateinit var tvMisCanciones: TextView
    private lateinit var tvMisAlbumes: TextView
    private lateinit var tvNombre: EditText
    private lateinit var tvDescripcion: EditText
    private lateinit var ivEditarPerfil: ImageView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNombreArtistico: EditText
    private lateinit var rvComentarios: RecyclerView
    private lateinit var tvMuroLabel: TextView
    private lateinit var llComentarioInput: LinearLayout
    private lateinit var etComentario: EditText
    private lateinit var btnEnviarComentario: Button

    private var userId: Int = -1
    private var modoEdicion = false
    private var base64NuevaFoto: String? = null
    private var artistaId: Int = -1

    private lateinit var btnCrearPlaylist: Button
    private lateinit var btnCrearAlbum: Button
    // btnSubirCancion eliminado — CrearCancion solo se abre desde CrearAlbum

    private val pedirPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirGaleria()
        else Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    private val galeria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { procesarImagenSeleccionada(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPlaylistsPublicas = view.findViewById(R.id.rvPlaylistsPublicas)
        rvPlaylistsPrivadas = view.findViewById(R.id.rvPlaylistsPrivadas)

        tvNombre       = view.findViewById(R.id.tvNombreUsuario)
        tvDescripcion  = view.findViewById(R.id.tvDescripcion)
        ivEditarPerfil = view.findViewById(R.id.ivEditarPerfil)
        ivAvatar       = view.findViewById(R.id.ivAvatar)

        rvMisCanciones = view.findViewById(R.id.rvMisCanciones)
        rvMisAlbumes   = view.findViewById(R.id.rvMisAlbumes)
        tvMisCanciones = view.findViewById(R.id.tvMisCanciones)
        tvMisAlbumes   = view.findViewById(R.id.tvMisAlbumes)
        tvMuroLabel    = view.findViewById(R.id.tvMuroLabel)
        rvComentarios  = view.findViewById(R.id.rvComentarios)
        llComentarioInput   = view.findViewById(R.id.llComentarioInput)
        etComentario        = view.findViewById(R.id.etComentario)
        btnEnviarComentario = view.findViewById(R.id.btnEnviarComentario)

        tvNombreArtistico = view.findViewById(R.id.tvNombreArtistico)
        tvNombreArtistico.isEnabled = false
        tvNombre.isEnabled      = false
        tvDescripcion.isEnabled = false
        val prefs  = requireContext().getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
        userId     = prefs.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        cargarPerfil()

        btnCrearPlaylist = view.findViewById(R.id.btnCrearPlaylist)
        btnCrearAlbum    = view.findViewById(R.id.btnCrearAlbum)

        val idRol = prefs.getInt("user_id_rol", 1)
        configurarBotonesSegunRol(idRol)

        ivAvatar.setOnClickListener { mostrarDialogAvatar() }

        ivEditarPerfil.setOnClickListener {
            if (!modoEdicion) {
                modoEdicion                  = true
                tvNombre.isEnabled           = true
                tvDescripcion.isEnabled      = true
                // Solo si es artista
                if (tvNombreArtistico.visibility == View.VISIBLE) {
                    tvNombreArtistico.isEnabled = true
                }
                tvNombre.requestFocus()
                ivEditarPerfil.setImageResource(android.R.drawable.ic_menu_save)
                Toast.makeText(requireContext(), "Editando perfil", Toast.LENGTH_SHORT).show()
            } else {
                guardarCambios()
            }
        }
    }

    private fun configurarBotonesSegunRol(idRol: Int) {
        // Solo artistas (rol 2) ven el botón de crear álbum
        if (idRol == 2) {
            btnCrearAlbum.visibility = View.VISIBLE
        } else {
            btnCrearAlbum.visibility = View.GONE
        }

        btnCrearPlaylist.setOnClickListener {
            startActivity(Intent(requireContext(), CrearPlaylist::class.java))
        }

        // Crear canción ya no tiene botón propio — se accede desde CrearAlbum
        btnCrearAlbum.setOnClickListener {
            startActivity(Intent(requireContext(), CrearAlbum::class.java))
        }
    }

    private fun mostrarDialogAvatar() {
        AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(arrayOf("Ver foto", "Actualizar foto", "Cerrar sesión")) { _, opcion ->
                when (opcion) {
                    0 -> mostrarFotoCompleta()
                    1 -> mostrarDialogActualizarFoto()
                    2 -> cerrarSesion()
                }
            }
            .show()
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Seguro que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                requireContext()
                    .getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
                    .edit().clear().apply()

                val intent = Intent(requireContext(), Loguin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun mostrarFotoCompleta() {
        val dialogView = ImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(600, 600)
            scaleType    = ImageView.ScaleType.CENTER_CROP
        }
        Glide.with(this).load(ivAvatar.drawable).into(dialogView)
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun mostrarDialogActualizarFoto() {
        AlertDialog.Builder(requireContext())
            .setTitle("Actualizar foto de perfil")
            .setMessage("Selecciona una imagen de tu galería")
            .setPositiveButton("Insertar desde galería") { _, _ ->
                verificarPermisoYAbrir()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun verificarPermisoYAbrir() {
        val permiso = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.READ_MEDIA_IMAGES
        else
            android.Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), permiso)
            == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria()
        } else {
            pedirPermiso.launch(permiso)
        }
    }

    private fun abrirGaleria() {
        galeria.launch("image/*")
    }

    private fun procesarImagenSeleccionada(uri: Uri) {
        try {
            val inputStream      = requireContext().contentResolver.openInputStream(uri)
            val bitmapOriginal   = BitmapFactory.decodeStream(inputStream)
            val bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, 300, 300, true)

            val outputStream = ByteArrayOutputStream()
            bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            base64NuevaFoto = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

            Glide.with(this).load(bitmapRedimensionado).circleCrop().into(ivAvatar)
            Toast.makeText(requireContext(),
                "Foto lista, guarda los cambios con el lápiz", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                "Error al procesar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarPerfil() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.create().getUser(userId)
                withContext(Dispatchers.Main) {
                    if (respuesta.isSuccessful) {
                        val usuario = respuesta.body()
                        tvNombre.setText(usuario?.name ?: "")
                        tvDescripcion.setText(
                            if (!usuario?.bio?.toString().isNullOrEmpty())
                                usuario?.bio.toString()
                            else "Escribe algo sobre ti..."
                        )

                        val idRol = requireContext()
                            .getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
                            .getInt("user_id_rol", 1)
                        if (idRol == 2) cargarNombreArtistico()
                        cargarPlaylists()


                        val pfp = usuario?.pfp?.toString()
                        if (!pfp.isNullOrEmpty()) {
                            val bytes  = Base64.decode(pfp, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            Glide.with(this@ProfileFragment)
                                .load(bitmap)
                                .circleCrop()
                                .placeholder(R.drawable.listenerdefault)
                                .into(ivAvatar)
                        }
                    } else {
                        Toast.makeText(requireContext(),
                            "Error al cargar perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(),
                        "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun cargarPlaylists() {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val response =
                    RetrofitClient.create()
                        .getPlaylists()

                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {

                        val playlists =
                            response.body()?.data ?: emptyList()

                        val publicas =
                            playlists.filter {
                                it.id_usuario == userId &&
                                        it.privacidad_playlist == 0
                            }

                        val privadas =
                            playlists.filter {
                                it.id_usuario == userId &&
                                        it.privacidad_playlist == 1
                            }

                        rvPlaylistsPublicas.layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

                        rvPlaylistsPrivadas.layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

                        rvPlaylistsPublicas.adapter =
                            PlaylistAdapter(publicas) { playlist ->

                                val intent = Intent(
                                    requireContext(),
                                    PlaylistDetalle::class.java
                                )

                                intent.putExtra(
                                    "id_playlist",
                                    playlist.id
                                )

                                intent.putExtra(
                                    "nombre_playlist",
                                    playlist.nombre_playlist
                                )

                                startActivity(intent)
                            }

                        rvPlaylistsPrivadas.adapter =
                            PlaylistAdapter(privadas) { playlist ->

                                val intent = Intent(
                                    requireContext(),
                                    PlaylistDetalle::class.java
                                )

                                intent.putExtra(
                                    "id_playlist",
                                    playlist.id
                                )

                                intent.putExtra(
                                    "nombre_playlist",
                                    playlist.nombre_playlist
                                )

                                startActivity(intent)
                            }
                    }
                }

            } catch (e: Exception) {

                android.util.Log.e(
                    "PLAYLISTS",
                    e.message ?: "error"
                )
            }
        }
    }
    private fun cargarNombreArtistico() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.create().getArtistas()
                withContext(Dispatchers.Main) {
                    if (respuesta.isSuccessful) {
                        val artista = respuesta.body()?.data
                            ?.firstOrNull { it.id_usuario == userId }
                        if (artista != null) {
                            artistaId = artista.id
                            tvNombreArtistico.setText(artista.nombre_artistico)
                            tvNombreArtistico.visibility = View.VISIBLE
                            cargarContenidoArtista(artista.id) // <- agregar esta línea
                        }
                    }
                }
            } catch (e: Exception) {
                // silencioso
            }
        }
    }

    private fun cargarContenidoArtista(idArtista: Int) {
        tvMisCanciones.visibility = View.VISIBLE
        rvMisCanciones.visibility = View.VISIBLE
        tvMisAlbumes.visibility   = View.VISIBLE
        rvMisAlbumes.visibility   = View.VISIBLE
        tvMuroLabel.visibility    = View.VISIBLE
        rvComentarios.visibility  = View.VISIBLE
        llComentarioInput.visibility = View.VISIBLE

        cargarComentarios(idArtista)

        btnEnviarComentario.setOnClickListener {
            val texto = etComentario.text.toString().trim()
            if (texto.isEmpty()) return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resp = RetrofitClient.create().createComentario(
                        id_artista = idArtista,
                        id_usuario = userId,
                        texto      = texto
                    )
                    withContext(Dispatchers.Main) {
                        if (resp.isSuccessful) {
                            etComentario.setText("")
                            cargarComentarios(idArtista)
                            Toast.makeText(requireContext(), "Mensaje publicado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) { }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respCanciones = RetrofitClient.create().getCanciones()
                val respAlbumes   = RetrofitClient.create().getAlbumes()

                withContext(Dispatchers.Main) {
                    if (respCanciones.isSuccessful) {
                        val todas = respCanciones.body()?.data ?: emptyList()
                        // LOG TEMPORAL
                        android.util.Log.d("PERFIL", "idArtista buscado: $idArtista")
                        android.util.Log.d("PERFIL", "Canciones totales: ${todas.size}")
                        todas.forEach { android.util.Log.d("PERFIL", "  cancion id_artista: ${it.id_artista}") }

                        val canciones = todas.filter { it.id_artista == idArtista }
                        android.util.Log.d("PERFIL", "Canciones filtradas: ${canciones.size}")

                        rvMisCanciones.layoutManager = LinearLayoutManager(requireContext())
                        rvMisCanciones.adapter = SongAdapter(canciones) { cancion ->
                            val intent = Intent(requireContext(), DetailSong::class.java)
                            intent.putExtra("id_album", cancion.id_album)
                            intent.putExtra("id_cancion", cancion.id)
                            startActivity(intent)
                        }
                    }

                    if (respAlbumes.isSuccessful) {
                        val todos = respAlbumes.body()?.data ?: emptyList()
                        android.util.Log.d("PERFIL", "Albumes totales: ${todos.size}")
                        // necesito ver el data class de albumes para confirmar el campo

                        rvMisAlbumes.layoutManager = LinearLayoutManager(
                            requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        rvMisAlbumes.adapter = AlbumAdapter(todos) { album ->
                            val intent = Intent(requireContext(), AlbumDetail::class.java)
                            intent.putExtra("id_album", album.id)
                            startActivity(intent)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.util.Log.e("PERFIL", "Error: ${e.message}")
                }
            }
        }
    }

    private fun guardarCambios() {
        val nuevoNombre = tvNombre.text.toString().trim()
        val nuevaBio    = tvDescripcion.text.toString().trim()


        if (nuevoNombre.isEmpty()) {
            tvNombre.error = "El nombre no puede estar vacío"
            return
        }

        val prefs         = requireContext().getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
        val idPaisGuardado = prefs.getInt("user_id_pais", 1)
        val idRolGuardado  = prefs.getInt("user_id_rol", 1)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuestaGet = RetrofitClient.create().getUser(userId)
                if (!respuestaGet.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(),
                            "No se pudo obtener datos actuales", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val actual   = respuestaGet.body()!!
                val pfpFinal = base64NuevaFoto ?: actual.pfp?.toString() ?: ""

                val respuestaPatch = RetrofitClient.create().updateUser(
                    id       = userId,
                    name     = nuevoNombre,
                    password = actual.password,
                    idPais   = idPaisGuardado,
                    idRol    = idRolGuardado,
                    email    = actual.email,
                    pfp      = pfpFinal,
                    bio      = nuevaBio
                )

                withContext(Dispatchers.Main) {
                    if (respuestaPatch.isSuccessful) {
                        if (tvNombreArtistico.visibility == View.VISIBLE && artistaId != -1) {
                            val nuevoNombreArtistico = tvNombreArtistico.text.toString().trim()
                            if (nuevoNombreArtistico.isNotEmpty()) {
                                RetrofitClient.create().actualizarArtista(artistaId, nuevoNombreArtistico)
                            }
                        }
                        prefs.edit().putString("user_name", nuevoNombre).apply()
                        modoEdicion             = false
                        base64NuevaFoto         = null
                        tvNombre.isEnabled      = false
                        tvDescripcion.isEnabled = false
                        tvNombreArtistico.isEnabled      = false
                        ivEditarPerfil.setImageResource(android.R.drawable.ic_menu_edit)
                        Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(),
                            "Error al guardar: ${respuestaPatch.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(),
                        "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun cargarComentarios(idArtista: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respComentarios = RetrofitClient.create().getComentarios()
                val respUsuarios    = RetrofitClient.create().getUsers()

                withContext(Dispatchers.Main) {
                    val nombres = respUsuarios.body()?.data
                        ?.associate { it.id.toString() to it.name } ?: emptyMap()

                    val comentarios = respComentarios.body()?.data
                        ?.filter { it.id_artista == idArtista.toString() }
                        ?.reversed() ?: emptyList()

                    rvComentarios.layoutManager = LinearLayoutManager(requireContext())
                    rvComentarios.adapter = ComentarioAdapter(comentarios, nombres)
                }
            } catch (e: Exception) {
                // silencioso
            }
        }
    }
}
