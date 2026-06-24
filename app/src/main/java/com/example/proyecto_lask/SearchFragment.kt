package com.example.proyecto_lask

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.text.Editable
import android.text.TextWatcher

class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var rvAlbumes: RecyclerView
    private lateinit var rvCanciones: RecyclerView

    private var albumesOriginales =
        listOf<com.example.proyecto_lask.albumes.Data>()

    private var cancionesOriginales =
        listOf<com.example.proyecto_lask.canciones.DataX>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etBuscar = view.findViewById<EditText>(R.id.etBuscar)
        rvAlbumes = view.findViewById(R.id.rvAlbumes)

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getAlbumes()

                if (respuesta.isSuccessful) {

                    albumesOriginales =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {

                        rvAlbumes.layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

                        rvAlbumes.adapter =
                            AlbumAdapter(albumesOriginales) { album ->

                                val intent =
                                    Intent(requireContext(), AlbumDetail::class.java)

                                intent.putExtra("id_album", album.id)

                                startActivity(intent)
                            }
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        rvCanciones =
            view.findViewById(R.id.rvCancionesBuscar)

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val respuesta =
                    RetrofitClient.create().getCanciones()

                if (respuesta.isSuccessful) {

                    cancionesOriginales =
                        respuesta.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {

                        rvCanciones.layoutManager =
                            LinearLayoutManager(requireContext())

                        rvCanciones.adapter =
                            SongAdapter(cancionesOriginales) { cancion ->

                                val intent =
                                    Intent(requireContext(), DetailSong::class.java)

                                intent.putExtra("id_album", cancion.id_album)
                                intent.putExtra("id_cancion", cancion.id)

                                startActivity(intent)
                            }
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        etBuscar.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    filtrar(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )

    }
    private fun filtrar(texto: String) {

        if (texto.isBlank()) {

            rvAlbumes.adapter =
                AlbumAdapter(albumesOriginales) { album ->

                    val intent =
                        Intent(requireContext(), AlbumDetail::class.java)

                    intent.putExtra("id_album", album.id)

                    startActivity(intent)
                }

            rvCanciones.adapter =
                SongAdapter(cancionesOriginales) { cancion ->

                    val intent =
                        Intent(requireContext(), DetailSong::class.java)

                    intent.putExtra("id_album", cancion.id_album)
                    intent.putExtra("id_cancion", cancion.id)

                    startActivity(intent)
                }

            return
        }

        val albumesFiltrados =
            albumesOriginales.filter {
                it.nombre_album.contains(
                    texto,
                    ignoreCase = true
                )
            }

        val cancionesFiltradas =
            cancionesOriginales.filter {
                it.nombre_cancion.contains(
                    texto,
                    ignoreCase = true
                )
            }

        rvAlbumes.adapter =
            AlbumAdapter(albumesFiltrados) { album ->

                val intent =
                    Intent(requireContext(), AlbumDetail::class.java)

                intent.putExtra("id_album", album.id)

                startActivity(intent)
            }

        rvCanciones.adapter =
            SongAdapter(cancionesFiltradas) { cancion ->

                val intent =
                    Intent(requireContext(), DetailSong::class.java)

                intent.putExtra("id_album", cancion.id_album)
                intent.putExtra("id_cancion", cancion.id)

                startActivity(intent)
            }
    }
}