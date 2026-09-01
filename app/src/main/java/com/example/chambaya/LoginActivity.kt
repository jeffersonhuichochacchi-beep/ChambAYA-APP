package com.example.chambaya

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chambaya.data.auth.FirebaseGestorAutenticacion
import com.example.chambaya.databinding.ActividadLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActividadLoginBinding
    private lateinit var authGestor: FirebaseGestorAutenticacion

    // Launcher para Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    mostrarCarga(true)
                    authGestor.autenticarConGoogle(
                        idToken = idToken,
                        rolPorDefecto = "worker",
                        onExito = {
                            mostrarCarga(false)
                            Toast.makeText(this, "¡Sesión iniciada con Google!", Toast.LENGTH_SHORT).show()
                            openMain()
                        },
                        onError = { error ->
                            mostrarCarga(false)
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    Toast.makeText(this, "No se pudo obtener el token de Google.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error de inicio de sesión con Google: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        IdiomaManager.applySavedLanguage(this)
        super.onCreate(savedInstanceState)

        window.statusBarColor = getColor(R.color.login_red)
        window.navigationBarColor = getColor(R.color.surface_light)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true

        binding = ActividadLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authGestor = FirebaseGestorAutenticacion.getInstance(this)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSignIn.setOnClickListener { realizarLoginCorreo() }
        binding.btnGoogleSignIn.setOnClickListener { iniciarFlujoGoogle() }
        binding.tvSignupLink.setOnClickListener { openRegister() }
        binding.tvForgotPassword.setOnClickListener { recuperarContrasena() }

        setupPasswordToggle(binding.etPassword, binding.ivTogglePassword)
    }

    private fun realizarLoginCorreo() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Ingresa un correo electrónico válido."
            binding.etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Ingresa tu contraseña."
            binding.etPassword.requestFocus()
            return
        }

        mostrarCarga(true)
        authGestor.iniciarSesionConCorreo(
            email = email,
            password = password,
            onExito = {
                mostrarCarga(false)
                Toast.makeText(this, "¡Bienvenido a ChambAYA!", Toast.LENGTH_SHORT).show()
                openMain()
            },
            onError = { error ->
                mostrarCarga(false)
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun iniciarFlujoGoogle() {
        val signInIntent = authGestor.getGoogleSignInClient().signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun recuperarContrasena() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingresa tu correo en el campo para enviarte el enlace de recuperación.", Toast.LENGTH_LONG).show()
            binding.etEmail.requestFocus()
            return
        }

        authGestor.auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Se envió un enlace a tu correo para restablecer tu contraseña.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo enviar el correo de recuperación: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun mostrarCarga(cargando: Boolean) {
        binding.btnSignIn.isEnabled = !cargando
        binding.btnGoogleSignIn.isEnabled = !cargando
    }

    private fun openMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun openRegister() {
        startActivity(Intent(this, RegistroActivity::class.java))
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
