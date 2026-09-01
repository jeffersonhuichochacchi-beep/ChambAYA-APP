package com.example.chambaya

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chambaya.databinding.ActividadRegistroBinding

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActividadRegistroBinding

    companion object {
        const val EXTRA_USER_TYPE = "extra_user_type"
        const val USER_TYPE_WORKER = "worker"
        const val USER_TYPE_EMPLOYER = "employer"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        IdiomaManager.applySavedLanguage(this)
        super.onCreate(savedInstanceState)

        binding = ActividadRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userType = intent.getStringExtra(EXTRA_USER_TYPE) ?: USER_TYPE_WORKER
        setupRoleUI(userType)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCreateAccount.setOnClickListener { openMain() }
        binding.tvBottomSignin.setOnClickListener { openLogin() }
        setupPasswordToggle(binding.etPassword, binding.ivTogglePassword)
        setupPasswordToggle(binding.etConfirmPassword, binding.ivToggleConfirmPassword)
    }

    private fun setupRoleUI(userType: String) {
        val isEmployer = userType == USER_TYPE_EMPLOYER

        if (isEmployer) {
            // Estilo y color para Empresa / Contratante (#0891B2)
            val employerColor = ContextCompat.getColor(this, R.color.secondary)
            binding.registerRoot.setBackgroundColor(employerColor)
            window.statusBarColor = employerColor
            window.navigationBarColor = getColor(R.color.surface_light)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true

            binding.tvRoleBadge.text = getString(R.string.register_employer_badge)
            binding.tvRegisterTitle.text = getString(R.string.register_employer_title)
            binding.tvRegisterSubtitle.text = getString(R.string.register_employer_subtitle)
            binding.tvFullNameLabel.text = getString(R.string.register_employer_name_label)
            binding.etFullName.hint = getString(R.string.register_employer_name_hint)
            binding.btnCreateAccount.text = getString(R.string.register_employer_btn)
            binding.btnCreateAccount.backgroundTintList = ColorStateList.valueOf(employerColor)
            binding.cbAgreement.buttonTintList = ColorStateList.valueOf(employerColor)
            binding.tvBottomSignin.setTextColor(employerColor)
        } else {
            // Estilo y color para Trabajador (#5046E5)
            val workerColor = ContextCompat.getColor(this, R.color.primary)
            binding.registerRoot.setBackgroundColor(workerColor)
            window.statusBarColor = workerColor
            window.navigationBarColor = getColor(R.color.surface_light)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true

            binding.tvRoleBadge.text = getString(R.string.register_worker_badge)
            binding.tvRegisterTitle.text = getString(R.string.register_worker_title)
            binding.tvRegisterSubtitle.text = getString(R.string.register_worker_subtitle)
            binding.tvFullNameLabel.text = getString(R.string.register_worker_name_label)
            binding.etFullName.hint = getString(R.string.register_worker_name_hint)
            binding.btnCreateAccount.text = getString(R.string.register_worker_btn)
            binding.btnCreateAccount.backgroundTintList = ColorStateList.valueOf(workerColor)
            binding.cbAgreement.buttonTintList = ColorStateList.valueOf(workerColor)
            binding.tvBottomSignin.setTextColor(workerColor)
        }
    }

    private fun openMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setupPasswordToggle(editText: EditText, toggle: ImageView) {
        var isPasswordVisible = false
        editText.transformationMethod = PasswordTransformationMethod.getInstance()
        updatePasswordToggleIcon(toggle, isPasswordVisible)

        toggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            editText.transformationMethod = if (isPasswordVisible) {
                null
            } else {
                PasswordTransformationMethod.getInstance()
            }
            editText.setSelection(editText.text?.length ?: 0)
            updatePasswordToggleIcon(toggle, isPasswordVisible)
        }
    }

    private fun updatePasswordToggleIcon(toggle: ImageView, isPasswordVisible: Boolean) {
        toggle.setImageResource(
            if (isPasswordVisible) R.drawable.ic_login_eye_on else R.drawable.ic_login_eye_off
        )
        toggle.contentDescription = getString(
            if (isPasswordVisible) R.string.auth_hide_password else R.string.auth_show_password
        )
    }
}
