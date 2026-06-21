package com.example.proyecto_lask

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
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

                        data.forEach { tag ->

                            val row =
                                TableRow(this@Tarea)

                            val tvId =
                                TextView(this@Tarea)

                            tvId.text =
                                tag.id.toString()

                            val tvNombre =
                                TextView(this@Tarea)

                            tvNombre.text =
                                tag.nombre_tag

                            val tvDescripcion =
                                TextView(this@Tarea)

                            tvDescripcion.text =
                                tag.descripcion_tag.toString()

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
}