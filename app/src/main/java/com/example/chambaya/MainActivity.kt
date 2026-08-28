package com.example.chambaya

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.chambaya.databinding.ActividadPrincipalBinding
import com.example.chambaya.ui.chat.FragmentoListaChats
import com.example.chambaya.ui.dialogs.DialogoFragmentoAnunciosNegocios
import com.example.chambaya.ui.dialogs.DialogoInferiorNotificaciones
import com.example.chambaya.ui.jobs.FragmentoFeedTrabajos
import com.example.chambaya.ui.map.FragmentoMapaChambas
import com.example.chambaya.ui.profile.FragmentoPerfil
import com.example.chambaya.ui.publish.FragmentoPublicarTrabajo

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActividadPrincipalBinding

    private val jobsFeedFragment by lazy { FragmentoFeedTrabajos() }
    private val mapFragment by lazy { FragmentoMapaChambas() }
    private val publishFragment by lazy { FragmentoPublicarTrabajo() }
    private val chatFragment by lazy { FragmentoListaChats() }
    private val profileFragment by lazy { FragmentoPerfil() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActividadPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupBottomNavigation()
        setupHeaderActions()

        if (savedInstanceState == null) {
            switchFragment(jobsFeedFragment)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_jobs -> {
                    switchFragment(jobsFeedFragment)
                    true
                }
                R.id.nav_map -> {
                    switchFragment(mapFragment)
                    true
                }
                R.id.nav_publish -> {
                    switchFragment(publishFragment)
                    true
                }
                R.id.nav_chat -> {
                    switchFragment(chatFragment)
                    true
                }
                R.id.nav_profile -> {
                    switchFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupHeaderActions() {
        binding.btnHeaderAds.setOnClickListener {
            val dialog = DialogoFragmentoAnunciosNegocios()
            dialog.show(supportFragmentManager, "AdsDialog")
        }

        binding.btnHeaderNotifications.setOnClickListener {
            binding.viewNotifBadge.visibility = View.GONE
            val dialog = DialogoInferiorNotificaciones()
            dialog.show(supportFragmentManager, "NotifsDialog")
        }
    }

    fun navigateToTab(tabId: Int) {
        binding.bottomNavigation.selectedItemId = tabId
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
