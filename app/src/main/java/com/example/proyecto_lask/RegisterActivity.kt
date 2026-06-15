package com.example.proyecto_lask

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

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

        // Cargar países desde strings.xml
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.countries_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spCountry.adapter = adapter

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

            val role = if (rbListener.isChecked) {
                "Listener"
            } else {
                "Artista"
            }

            // Por ahora solo muestra la información
            Toast.makeText(
                this,
                "Usuario: $username\nRol: $role\nPaís: $country",
                Toast.LENGTH_LONG
            ).show()

            // Aquí después podrás guardar los datos en Firebase o SQLite.
        }
    }
}