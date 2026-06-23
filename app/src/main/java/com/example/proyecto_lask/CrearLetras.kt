package com.example.proyecto_lask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CrearLetras : AppCompatActivity() {

    private lateinit var etLetraContenido: EditText
    private lateinit var etLetraFoneticaContenido: EditText
    private lateinit var btnSubirLetraAccion: Button

    private var idCancion: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_letras)

        idCancion = intent.getIntExtra("id_cancion", -1)

        etLetraContenido = findViewById(R.id.etLetraContenido)
        etLetraFoneticaContenido = findViewById(R.id.etLetraFoneticaContenido)
        btnSubirLetraAccion = findViewById(R.id.btnSubirLetraAccion)

        btnSubirLetraAccion.setOnClickListener {

            val letra = etLetraContenido.text.toString().trim()
            val fonetica = etLetraFoneticaContenido.text.toString().trim()

            val result = Intent()

            // 🔥 IMPORTANTE: aunque esté vacío lo mandamos igual
            result.putExtra("letra_cancion", letra)
            result.putExtra("texto_fonetico", fonetica)

            setResult(Activity.RESULT_OK, result)

            Toast.makeText(
                this,
                if (letra.isEmpty() && fonetica.isEmpty())
                    "Sin letra agregada"
                else
                    "Letra agregada correctamente",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}