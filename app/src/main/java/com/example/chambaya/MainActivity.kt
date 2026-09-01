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
import androidx.lifecycle.Lifecycle
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
    private lateinit var profileFragment: FragmentoPerfil
    private val bottomNavPopInterpolator = OvershootInterpolator(1.12f)
    private val bottomNavSettleInterpolator = DecelerateInterpolator()
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        IdiomaManager.applySavedLanguage(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActividadPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Leer si el usuario entró como invitado (sin cuenta)
        val isGuestMode = intent.getBooleanExtra(EXTRA_GUEST_MODE, false)
        profileFragment = FragmentoPerfil.newInstance(isGuestMode)

        setupBottomNavigation()
        setupHeaderActions()

        if (savedInstanceState == null) {
            switchToTab(R.id.nav_jobs)
        } else {
            activeFragment = supportFragmentManager.fragments.firstOrNull {
                it.id == R.id.fragmentContainer && !it.isHidden
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            binding.bottomNavigation.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            animateBottomNavSelection(item.itemId)

            when (item.itemId) {
                R.id.nav_jobs -> {
                    switchToTab(R.id.nav_jobs)
                    true
                }
                R.id.nav_map -> {
                    switchToTab(R.id.nav_map)
                    true
                }
                R.id.nav_publish -> {
                    switchToTab(R.id.nav_publish)
                    true
                }
                R.id.nav_chat -> {
                    switchToTab(R.id.nav_chat)
                    true
                }
                R.id.nav_profile -> {
                    switchToTab(R.id.nav_profile)
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

    private fun switchToTab(tabId: Int) {
        val tag = tabTag(tabId)
        val fragment = supportFragmentManager.findFragmentByTag(tag) ?: createFragmentForTab(tabId)
        if (fragment == activeFragment) return

        val transaction = supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)

        activeFragment?.let { current ->
            transaction
                .hide(current)
                .setMaxLifecycle(current, Lifecycle.State.STARTED)
        }

        if (fragment.isAdded) {
            transaction.show(fragment)
        } else {
            transaction.add(R.id.fragmentContainer, fragment, tag)
        }

        transaction
            .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            .commit()

        activeFragment = fragment
    }

    private fun createFragmentForTab(tabId: Int): Fragment {
        return when (tabId) {
            R.id.nav_jobs -> jobsFeedFragment
            R.id.nav_map -> mapFragment
            R.id.nav_publish -> publishFragment
            R.id.nav_chat -> chatFragment
            R.id.nav_profile -> profileFragment
            else -> jobsFeedFragment
        }
    }

    private fun tabTag(tabId: Int): String {
        return when (tabId) {
            R.id.nav_jobs -> TAG_JOBS
            R.id.nav_map -> TAG_MAP
            R.id.nav_publish -> TAG_PUBLISH
            R.id.nav_chat -> TAG_CHAT
            R.id.nav_profile -> TAG_PROFILE
            else -> TAG_JOBS
        }
    }

    companion object {
        const val EXTRA_GUEST_MODE = "extra_guest_mode"
        private const val TAG_JOBS = "tab_jobs"
        private const val TAG_MAP = "tab_map"
        private const val TAG_PUBLISH = "tab_publish"
        private const val TAG_CHAT = "tab_chat"
        private const val TAG_PROFILE = "tab_profile"
    }
}
