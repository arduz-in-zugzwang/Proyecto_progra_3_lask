package com.example.proyecto_lask

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tarea : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarea)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnInsertarTag = findViewById<Button>(R.id.btnInsertarTag)
        val etNombreTag = findViewById<EditText>(R.id.etNombreTag)
        val etDescripcionTag = findViewById<EditText>(R.id.etDescripcionTag)

        btnInsertarTag.setOnClickListener {

            val nombre = etNombreTag.text.toString()
            val descripcion = etDescripcionTag.text.toString()

            createTag(nombre, descripcion)
        }

        getTags()
    }
    private fun getTags() {

        val tableTags =
            findViewById<TableLayout>(R.id.tableTags)

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val call =
                    RetrofitClient.create().getTags()

                val response =
                    call.body()

                withContext(Dispatchers.Main) {

                    if(call.isSuccessful){

                        val data =
                            response?.data ?: emptyList()

                        val childCount = tableTags.childCount

                        if (childCount > 1) {
                            tableTags.removeViews(1, childCount - 1)
                        }

                        data.forEach { tag ->
                            val row = TableRow(this@Tarea).apply {
                                layoutParams = TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                                setPadding(0, 8, 0, 8) // Un poco de espacio entre filas
                            }

                            // Celda ID (Peso: 1)
                            val tvId = TextView(this@Tarea).apply {
                                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                                text = tag.id.toString()
                            }

                            // Celda Nombre (Peso: 2)
                            val tvNombre = TextView(this@Tarea).apply {
                                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f)
                                text = tag.nombre_tag
                            }

                            // Celda Descripción (Peso: 3)
                            val tvDescripcion = TextView(this@Tarea).apply {
                                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f)
                                // Controlamos los nulos de tu base de datos para que no muestre la palabra "null"
                                text = tag.descripcion_tag?.toString() ?: ""
                            }

                            row.addView(tvId)
                            row.addView(tvNombre)
                            row.addView(tvDescripcion)

                            tableTags.addView(row)
                        }
                    }
                }

            } catch(e: Exception){

                e.printStackTrace()

            }
        }
    }

    private fun createTag(
        nombre: String,
        descripcion: String
    ){
        CoroutineScope(Dispatchers.IO).launch {

            try {

                val call =
                    RetrofitClient.create()
                        .createTags(
                            nombre,
                            descripcion
                        )

                withContext(Dispatchers.Main){

                    if(call.isSuccessful){
                        findViewById<EditText>(R.id.etNombreTag).text.clear()
                        findViewById<EditText>(R.id.etDescripcionTag).text.clear()

                        Toast.makeText(
                            this@Tarea,
                            "Tag creado correctamente",
                            Toast.LENGTH_LONG
                        ).show()

                        getTags()

                    }else{

                        Toast.makeText(
                            this@Tarea,
                            "Error al crear tag",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }catch(e:Exception){

                withContext(Dispatchers.Main){

                    Toast.makeText(
                        this@Tarea,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}