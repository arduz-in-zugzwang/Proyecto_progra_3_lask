package com.example.proyecto_lask

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Manejo de los insets (barra de estado, etc.)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el NavController del NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Conectar el bottom navigation con el NavController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setupWithNavController(navController)

        // Logo del toolbar: siempre manda al Home
        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        ivLogo.setOnClickListener {
            irAInicio()
        }
    }

    private fun irAInicio() {
        // Si ya estamos en Home no hacemos nada
        if (navController.currentDestination?.id == R.id.homeFragment) return

        // Si Home está en el historial, regresamos a él (sin perder su estado)
        val regreso = navController.popBackStack(R.id.homeFragment, false)

        // Si no estaba en el historial (ej. venimos del login), navegamos directo
        if (!regreso) {
            navController.navigate(R.id.homeFragment)
        }
    }
}