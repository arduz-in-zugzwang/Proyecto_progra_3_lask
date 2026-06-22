package com.example.proyecto_lask

import android.content.Context
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
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var tvNombre: EditText
    private lateinit var tvDescripcion: EditText
    private lateinit var ivEditarPerfil: ImageView
    private lateinit var ivAvatar: ImageView

    private var userId: Int = -1
    private var modoEdicion = false
    private var base64NuevaFoto: String? = null  // guardamos la foto nueva aquí

    // Lanzador para pedir permiso de galería
    private val pedirPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirGaleria()
        else Toast.makeText(requireContext(),
            "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    // Lanzador para abrir galería
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

        tvNombre = view.findViewById(R.id.tvNombreUsuario)
        tvDescripcion = view.findViewById(R.id.tvDescripcion)
        ivEditarPerfil = view.findViewById(R.id.ivEditarPerfil)
        ivAvatar = view.findViewById(R.id.ivAvatar)

        tvNombre.isEnabled = false
        tvDescripcion.isEnabled = false

        val prefs = requireContext()
            .getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
        userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(),
                "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        cargarPerfil()

        // Toca el avatar → dialog con dos opciones
        ivAvatar.setOnClickListener {
            mostrarDialogAvatar()
        }

        // Lápiz → activa edición o guarda
        ivEditarPerfil.setOnClickListener {
            if (!modoEdicion) {
                modoEdicion = true
                tvNombre.isEnabled = true
                tvDescripcion.isEnabled = true
                tvNombre.requestFocus()
                ivEditarPerfil.setImageResource(android.R.drawable.ic_menu_save)
                Toast.makeText(requireContext(),
                    "Editando perfil", Toast.LENGTH_SHORT).show()
            } else {
                guardarCambios()
            }
        }
    }

    // Dialog: "Ver perfil" o "Actualizar perfil"
    private fun mostrarDialogAvatar() {
        AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(arrayOf("Ver perfil", "Actualizar foto")) { _, opcion ->
                when (opcion) {
                    0 -> mostrarFotoCompleta()
                    1 -> mostrarDialogActualizarFoto()
                }
            }
            .show()
    }

    // Muestra la foto en grande (dialog simple)
    private fun mostrarFotoCompleta() {
        val dialogView = ImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(600, 600)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        Glide.with(this).load(ivAvatar.drawable).into(dialogView)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    // Dialog con botón "Insertar desde galería"
    private fun mostrarDialogActualizarFoto() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(android.R.layout.activity_list_item, null)

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

    // Convierte la imagen a Base64 comprimida y la muestra
    private fun procesarImagenSeleccionada(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)

            // Redimensionar a máximo 300x300 para que el Base64 no sea enorme
            val bitmapRedimensionado = Bitmap.createScaledBitmap(
                bitmapOriginal,
                300, 300,
                true
            )

            // Comprimir a JPEG calidad 70
            val outputStream = ByteArrayOutputStream()
            bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            base64NuevaFoto = Base64.encodeToString(bytes, Base64.DEFAULT)

            // Mostrar preview inmediato en el avatar
            Glide.with(this)
                .load(bitmapRedimensionado)
                .circleCrop()
                .into(ivAvatar)

            Toast.makeText(requireContext(),
                "Foto lista, guarda los cambios con el lápiz",
                Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                "Error al procesar imagen: ${e.message}",
                Toast.LENGTH_SHORT).show()
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

                        // Cargar foto si existe
                        val pfp = usuario?.pfp?.toString()
                        if (!pfp.isNullOrEmpty()) {
                            val bytes = Base64.decode(pfp, Base64.DEFAULT)
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

    private fun guardarCambios() {
        val nuevoNombre = tvNombre.text.toString().trim()
        val nuevaBio = tvDescripcion.text.toString().trim()

        if (nuevoNombre.isEmpty()) {
            tvNombre.error = "El nombre no puede estar vacío"
            return
        }

        val prefs = requireContext()
            .getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
        val idPaisGuardado = prefs.getInt("user_id_pais", 1)
        val idRolGuardado = prefs.getInt("user_id_rol", 1)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuestaGet = RetrofitClient.create().getUser(userId)
                if (!respuestaGet.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(),
                            "No se pudo obtener datos actuales",
                            Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val actual = respuestaGet.body()!!

                // Si no seleccionaron foto nueva, mantener la que ya había
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
                        prefs.edit().putString("user_name", nuevoNombre).apply()

                        modoEdicion = false
                        base64NuevaFoto = null  // limpiar foto pendiente
                        tvNombre.isEnabled = false
                        tvDescripcion.isEnabled = false
                        ivEditarPerfil.setImageResource(android.R.drawable.ic_menu_edit)

                        Toast.makeText(requireContext(),
                            "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(),
                            "Error al guardar: ${respuestaPatch.code()}",
                            Toast.LENGTH_SHORT).show()
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
}