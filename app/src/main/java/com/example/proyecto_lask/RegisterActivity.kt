package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etArtisticName: TextInputEditText
    private lateinit var spCountry: Spinner
    private lateinit var rgRole: RadioGroup
    private lateinit var rbListener: RadioButton
    private lateinit var rbArtist: RadioButton

    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button
    private lateinit var imgLogo: ImageView
    private var listaPaises = mutableListOf<com.example.proyecto_lask.paises.Data>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Referencias
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etArtisticName = findViewById(R.id.etArtisticName)

        spCountry = findViewById(R.id.spCountry)
        rgRole = findViewById(R.id.rgRole)
        rbListener = findViewById(R.id.rbListener)
        rbArtist = findViewById(R.id.rbArtist)

        cbTerms = findViewById(R.id.cbTerms)
        btnRegister = findViewById(R.id.btnRegister)
        imgLogo=findViewById(R.id.imgLogo)

        //cargar paises
        cargarPaises()


        imgLogo.setOnClickListener {
            startActivity(Intent(
                this, Bienvenido::class.java))
            finish()
        }

        // Botón Registrar
        btnRegister.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val artisticName = etArtisticName.text.toString().trim()
            val country = spCountry.selectedItem.toString()

            // Obtener rol seleccionado
            val selectedRoleId = rgRole.checkedRadioButtonId

            if (username.isEmpty()) {
                etUsername.error = "Ingresa un nombre de usuario"
                etUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Ingresa una contraseña"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (country == "Selecciona un país") {
                Toast.makeText(
                    this,
                    "Selecciona un país",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (selectedRoleId == -1) {
                Toast.makeText(
                    this,
                    "Selecciona un rol",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (rbArtist.isChecked && artisticName.isEmpty()) {
                etArtisticName.error = "Ingresa tu nombre artístico"
                etArtisticName.requestFocus()
                return@setOnClickListener
            }

            if (!cbTerms.isChecked) {
                Toast.makeText(
                    this,
                    "Debes aceptar los términos y condiciones",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val idRol = if (rbListener.isChecked) 1 else 2
            CoroutineScope(Dispatchers.IO).launch {
                val idPais =
                    listaPaises[spCountry.selectedItemPosition].id

                try {

                    val respuesta =
                        RetrofitClient.create().createUser(
                            name = username,
                            password = password,
                            idPais = idPais,
                            idRol = idRol,
                            email = "${username}@lask.com"
                        )

                    if (respuesta.isSuccessful) {

                        val usuario = respuesta.body()

                        if (usuario != null && idRol == 2) {
                            val respuestaArtista = RetrofitClient.create().createArtista(
                                usuario.id,
                                artisticName
                            )
                            if (respuestaArtista.isSuccessful) {
                                val idArtista = respuestaArtista.body()?.id ?: -1
                                getSharedPreferences("sesion_lask", MODE_PRIVATE)
                                    .edit()
                                    .putInt("artista_id", idArtista)
                                    .apply()
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Error al crear perfil artista: ${respuestaArtista.code()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Cuenta creada correctamente",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(
                                this@RegisterActivity,
                                Bienvenido::class.java
                            )

                            startActivity(intent)

                            finish()
                        }
                    }

                } catch (e: Exception) {

                    withContext(Dispatchers.Main) {

                        Toast.makeText(
                            this@RegisterActivity,
                            e.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }
    private fun cargarPaises() {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getPaises()

                if (respuesta.isSuccessful) {

                    val paises =
                        respuesta.body()?.data ?: emptyList()

                    listaPaises.clear()
                    listaPaises.addAll(paises)

                    val nombresPaises =
                        paises.map { it.nombre_pais }

                    withContext(Dispatchers.Main) {

                        val adapter = ArrayAdapter(
                            this@RegisterActivity,
                            android.R.layout.simple_spinner_item,
                            nombresPaises
                        )

                        adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                        )

                        spCountry.adapter = adapter
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        this@RegisterActivity,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}