package com.example.proyecto_lask

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var tvNombre: EditText
    private lateinit var tvDescripcion: EditText
    private lateinit var ivEditarPerfil: ImageView

    private var userId: Int = -1
    private var modoEdicion = false

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

        // Campos deshabilitados al inicio (solo lectura)
        tvNombre.isEnabled = false
        tvDescripcion.isEnabled = false

        // Leer userId guardado al hacer login
        val prefs = requireContext()
            .getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
        userId = prefs.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        cargarPerfil()

        ivEditarPerfil.setOnClickListener {
            if (!modoEdicion) {
                // Activar modo edición
                modoEdicion = true
                tvNombre.isEnabled = true
                tvDescripcion.isEnabled = true
                tvNombre.requestFocus()
                ivEditarPerfil.setImageResource(android.R.drawable.ic_menu_save)
                Toast.makeText(requireContext(), "Editando perfil", Toast.LENGTH_SHORT).show()
            } else {
                // Guardar cambios
                guardarCambios()
            }
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
                            else
                                "Escribe algo sobre ti..."
                        )
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

        // Leer prefs en Main thread ANTES de entrar a la coroutine
        val prefs = requireContext()
            .getSharedPreferences("sesion_lask", Context.MODE_PRIVATE)
        val idPaisGuardado = prefs.getInt("user_id_pais", 1)
        val idRolGuardado = prefs.getInt("user_id_rol", 1)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // GET primero para no pisar campos que no editamos (password, email, pfp)
                val respuestaGet = RetrofitClient.create().getUser(userId)
                if (!respuestaGet.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(),
                            "No se pudo obtener datos actuales", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val actual = respuestaGet.body()!!

                val respuestaPatch = RetrofitClient.create().updateUser(
                    id       = userId,
                    name     = nuevoNombre,
                    password = actual.password,
                    idPais   = idPaisGuardado,
                    idRol    = idRolGuardado,
                    email    = actual.email,
                    pfp      = actual.pfp?.toString() ?: "",
                    bio      = nuevaBio
                )

                withContext(Dispatchers.Main) {
                    if (respuestaPatch.isSuccessful) {
                        // Actualizar nombre en SharedPreferences
                        prefs.edit().putString("user_name", nuevoNombre).apply()

                        // Volver a modo lectura
                        modoEdicion = false
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