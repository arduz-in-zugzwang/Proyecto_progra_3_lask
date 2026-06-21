package com.example.proyecto_lask.escuchan

data class respuestaGetListeners(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)