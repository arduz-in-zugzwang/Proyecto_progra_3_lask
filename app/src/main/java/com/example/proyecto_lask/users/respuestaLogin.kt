package com.example.proyecto_lask.users

data class respuestaLogin(
    val bio: Any,
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val id_pais: Int,
    val id_rol: Int,
    val name: String,
    val password: String,
    val pfp: Any,
    val remember_token: Any,
    val updated_at: String
)