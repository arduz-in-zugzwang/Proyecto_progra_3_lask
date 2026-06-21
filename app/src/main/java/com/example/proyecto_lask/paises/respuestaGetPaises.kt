package com.example.proyecto_lask.paises

data class respuestaGetPaises(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)