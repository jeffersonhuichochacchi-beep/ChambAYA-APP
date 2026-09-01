package com.example.chambaya

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
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
    private val bottomNavPopInterpolator = OvershootInterpolator(1.12f)
    private val bottomNavSettleInterpolator = DecelerateInterpolator()
    private var hasShownInitialFragment = false

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
            binding.bottomNavigation.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            animateBottomNavSelection(item.itemId)

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

        binding.bottomNavigation.setOnItemReselectedListener { item ->
            binding.bottomNavigation.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            animateBottomNavSelection(item.itemId)
        }

        binding.bottomNavigation.post {
            val selectedItemId = binding.bottomNavigation.selectedItemId.takeIf { it != View.NO_ID }
                ?: R.id.nav_jobs
            animateBottomNavSelection(selectedItemId, animate = false)
        }
    }

    private fun animateBottomNavSelection(selectedItemId: Int, animate: Boolean = true) {
        val menuView = binding.bottomNavigation.getChildAt(0) as? ViewGroup ?: return

        for (index in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(index)
            val isSelected = itemView.id == selectedItemId
            val targetScale = if (isSelected) 1.06f else 1f
            val targetAlpha = if (isSelected) 1f else 0.78f

            itemView.animate().cancel()

            if (!animate) {
                itemView.scaleX = targetScale
                itemView.scaleY = targetScale
                itemView.alpha = targetAlpha
                continue
            }

            itemView.animate()
                .scaleX(targetScale)
                .scaleY(targetScale)
                .alpha(targetAlpha)
                .setDuration(if (isSelected) 230L else 170L)
                .setInterpolator(if (isSelected) bottomNavPopInterpolator else bottomNavSettleInterpolator)
                .start()
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
        val transaction = supportFragmentManager.beginTransaction()

        if (hasShownInitialFragment) {
            transaction.setCustomAnimations(
                R.anim.nav_fragment_enter,
                R.anim.nav_fragment_exit
            )
        }

        transaction
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        hasShownInitialFragment = true
    }
}
