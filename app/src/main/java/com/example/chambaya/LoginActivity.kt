package com.example.chambaya

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chambaya.databinding.ActividadLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActividadLoginBinding
    private var isRegisterView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.login_red)
        window.navigationBarColor = getColor(R.color.surface_light)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true

        binding = ActividadLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener { openMain() }
        binding.tvBottomSignup.setOnClickListener {
            if (isRegisterView) {
                showLoginView()
            } else {
                showRegisterView()
            }
        }
    }

    private fun showRegisterView() {
        isRegisterView = true
        binding.tvNameLabel.visibility = View.VISIBLE
        binding.etFullName.visibility = View.VISIBLE
        binding.tvConfirmPasswordLabel.visibility = View.VISIBLE
        binding.confirmPasswordContainer.visibility = View.VISIBLE
        binding.tvForgotPassword.visibility = View.GONE
        binding.btnSignIn.text = "Sign up"
        binding.tvBottomPrompt.text = "Already have an account? "
        binding.tvBottomSignup.text = "Sign in"
        binding.loginPanel.smoothScrollTo(0, 0)
    }

    private fun showLoginView() {
        isRegisterView = false
        binding.tvNameLabel.visibility = View.GONE
        binding.etFullName.visibility = View.GONE
        binding.tvConfirmPasswordLabel.visibility = View.GONE
        binding.confirmPasswordContainer.visibility = View.GONE
        binding.tvForgotPassword.visibility = View.VISIBLE
        binding.btnSignIn.text = "Sign in"
        binding.tvBottomPrompt.text = "Don't have an account? "
        binding.tvBottomSignup.text = "Sign up"
        binding.loginPanel.smoothScrollTo(0, 0)
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
