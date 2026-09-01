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

    companion object {
        const val EXTRA_PREFILL_EMAIL = "extra_prefill_email"
    }

    // Launcher para Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
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
            val mensaje = when (e.statusCode) {
                10 -> "Error 10 (DEVELOPER_ERROR): Falta registrar la huella SHA-1 en Firebase Console."
                12500 -> "Error 12500: Revisa tu conexión y servicios de Google Play."
                12501 -> "Inicio de sesión con Google cancelado."
                else -> "Error de Google (${e.statusCode}): ${e.localizedMessage}"
            }
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
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

        // Pre-llenar email si viene de una pantalla de registro exitoso
        val prefillEmail = intent.getStringExtra(EXTRA_PREFILL_EMAIL)
        if (!prefillEmail.isNullOrBlank()) {
            binding.etEmail.setText(prefillEmail)
            binding.etPassword.requestFocus()
        }

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
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "Por favor, ingresa un correo electrónico válido.",
                campoErroneo = binding.etEmail
            )
            binding.etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "Por favor, ingresa tu contraseña.",
                campoErroneo = binding.etPassword
            )
            binding.etPassword.requestFocus()
            return
        }

        mostrarCarga(true)
        authGestor.iniciarSesionConCorreo(
            email = email,
            password = password,
            onExito = {
                mostrarCarga(false)
                openMain()
            },
            onEmailNoVerificado = { unverifiedUser ->
                mostrarCarga(false)
                com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("⚠️ Correo aún no verificado")
                    .setMessage("Tu cuenta con el correo:\n\n$email\n\naún no ha sido autorizada. Abre tu correo personal y haz clic en el enlace de verificación para activarla.\n\n¿Deseas que te reenviemos el enlace?")
                    .setPositiveButton("Reenviar Enlace") { _, _ ->
                        authGestor.reenviarCorreoVerificacion(
                            user = unverifiedUser,
                            onExito = {
                                com.example.chambaya.ui.common.AlertaAuth.mostrarExitoFlotante(
                                    activity = this,
                                    rootLayout = binding.root,
                                    mensaje = "¡Enlace reenviado! Revisa tu bandeja de entrada o spam."
                                )
                                authGestor.cerrarSesion()
                            },
                            onError = { error ->
                                com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                                    activity = this,
                                    rootLayout = binding.root,
                                    mensaje = "Error al reenviar: $error"
                                )
                                authGestor.cerrarSesion()
                            }
                        )
                    }
                    .setNegativeButton("Entendido") { _, _ ->
                        authGestor.cerrarSesion()
                    }
                    .show()
            },
            onError = { error ->
                mostrarCarga(false)
                com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                    activity = this,
                    rootLayout = binding.root,
                    mensaje = error,
                    campoErroneo = binding.etPassword
                )
            }
        )
    }

    private fun iniciarFlujoGoogle() {
        mostrarCarga(true)
        val client = authGestor.getGoogleSignInClient()
        client.signOut().addOnCompleteListener {
            mostrarCarga(false)
            val signInIntent = client.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun recuperarContrasena() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "Ingresa tu correo para enviarte el enlace de recuperación.",
                campoErroneo = binding.etEmail
            )
            binding.etEmail.requestFocus()
            return
        }

        authGestor.auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                com.example.chambaya.ui.common.AlertaAuth.mostrarExitoFlotante(
                    activity = this,
                    rootLayout = binding.root,
                    mensaje = "Se envió un enlace a tu correo para restablecer tu contraseña."
                )
            }
            .addOnFailureListener {
                com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                    activity = this,
                    rootLayout = binding.root,
                    mensaje = "No se pudo enviar el correo de recuperación: ${it.localizedMessage}"
                )
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
