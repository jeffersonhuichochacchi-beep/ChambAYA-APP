package com.example.chambaya

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.chambaya.data.auth.FirebaseGestorAutenticacion
import com.example.chambaya.databinding.ActividadRegistroBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActividadRegistroBinding
    private lateinit var authGestor: FirebaseGestorAutenticacion
    private var currentUserRole: String = USER_TYPE_WORKER

    companion object {
        const val EXTRA_USER_TYPE = "extra_user_type"
        const val USER_TYPE_WORKER = "worker"
        const val USER_TYPE_EMPLOYER = "employer"
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
                    rolPorDefecto = currentUserRole,
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

        binding = ActividadRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authGestor = FirebaseGestorAutenticacion.getInstance(this)
        currentUserRole = intent.getStringExtra(EXTRA_USER_TYPE) ?: USER_TYPE_WORKER
        setupRoleUI(currentUserRole)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCreateAccount.setOnClickListener { realizarRegistroCorreo() }
        binding.btnGoogleSignIn.setOnClickListener { iniciarFlujoGoogle() }
        binding.tvBottomSignin.setOnClickListener { openLogin() }

        setupPasswordToggle(binding.etPassword, binding.ivTogglePassword)
        setupPasswordToggle(binding.etConfirmPassword, binding.ivToggleConfirmPassword)
    }

    private fun setupRoleUI(userType: String) {
        val isEmployer = userType == USER_TYPE_EMPLOYER

        if (isEmployer) {
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

    private fun realizarRegistroCorreo() {
        val nombre = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val aceptaTerminos = binding.cbAgreement.isChecked

        if (nombre.isEmpty()) {
            binding.etFullName.error = "Por favor ingresa tu nombre."
            binding.etFullName.requestFocus()
            return
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Ingresa un correo electrónico válido."
            binding.etEmail.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres."
            binding.etPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Las contraseñas no coinciden."
            binding.etConfirmPassword.requestFocus()
            return
        }

        if (!aceptaTerminos) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones.", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCarga(true)
        authGestor.registrarConCorreo(
            nombre = nombre,
            email = email,
            password = password,
            rol = currentUserRole,
            onExito = { user ->
                mostrarCarga(false)
                mostrarDialogoEsperandoVerificacion(user, email)
            },
            onError = { error ->
                mostrarCarga(false)
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )
    }

    private var verificationHandler: android.os.Handler? = null
    private var verificationRunnable: Runnable? = null
    private var verificationDialog: androidx.appcompat.app.AlertDialog? = null

    private fun mostrarDialogoEsperandoVerificacion(user: com.google.firebase.auth.FirebaseUser, email: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_esperando_verificacion, null)

        val tvIcon = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogIcon)
        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogTitle)
        val tvEmail = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogEmail)
        val tvInstruction = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogInstruction)
        val progressChecking = dialogView.findViewById<android.widget.ProgressBar>(R.id.progressChecking)
        val tvStatusLabel = dialogView.findViewById<android.widget.TextView>(R.id.tvStatusLabel)
        val btnResend = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnResendVerification)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelVerification)

        tvEmail.text = email

        verificationDialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        verificationDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        verificationDialog?.show()

        // Reenviar correo
        btnResend.setOnClickListener {
            btnResend.isEnabled = false
            authGestor.reenviarCorreoVerificacion(
                user = user,
                onExito = {
                    Toast.makeText(this, "¡Correo de verificación reenviado a $email!", Toast.LENGTH_SHORT).show()
                    btnResend.postDelayed({ btnResend.isEnabled = true }, 5000L)
                },
                onError = { error ->
                    Toast.makeText(this, "Error al reenviar: $error", Toast.LENGTH_SHORT).show()
                    btnResend.isEnabled = true
                }
            )
        }

        // Cancelar y cerrar
        btnCancel.setOnClickListener {
            detenerComprobacionVerificacion()
            verificationDialog?.dismiss()
            authGestor.cerrarSesion()
        }

        // Iniciar comprobación en tiempo real (cada 2.5 segundos)
        verificationHandler = android.os.Handler(android.os.Looper.getMainLooper())
        verificationRunnable = object : Runnable {
            override fun run() {
                user.reload().addOnCompleteListener { task ->
                    if (isFinishing || isDestroyed) return@addOnCompleteListener

                    if (user.isEmailVerified) {
                        detenerComprobacionVerificacion()

                        // Estado verificado
                        tvIcon.text = "✅"
                        tvTitle.text = "¡Correo Verificado!"
                        tvInstruction.text = "Tu cuenta ha sido autorizada con éxito. Redirigiendo al inicio de sesión..."
                        progressChecking.visibility = View.GONE
                        tvStatusLabel.text = "¡Verificación exitosa!"
                        tvStatusLabel.setTextColor(getColor(R.color.success))
                        btnResend.visibility = View.GONE
                        btnCancel.visibility = View.GONE

                        // Redirigir al Login después de 1.5 segundos
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            verificationDialog?.dismiss()
                            authGestor.cerrarSesion()
                            openLogin(email)
                        }, 1500L)
                    } else {
                        // Seguir consultando cada 2.5 segundos
                        verificationHandler?.postDelayed(this, 2500L)
                    }
                }
            }
        }
        verificationHandler?.postDelayed(verificationRunnable!!, 2500L)
    }

    private fun detenerComprobacionVerificacion() {
        verificationRunnable?.let { verificationHandler?.removeCallbacks(it) }
        verificationHandler = null
        verificationRunnable = null
    }

    override fun onDestroy() {
        detenerComprobacionVerificacion()
        verificationDialog?.dismiss()
        super.onDestroy()
    }

    private fun iniciarFlujoGoogle() {
        val signInIntent = authGestor.getGoogleSignInClient().signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun mostrarCarga(cargando: Boolean) {
        binding.btnCreateAccount.isEnabled = !cargando
        binding.btnGoogleSignIn.isEnabled = !cargando
    }

    private fun openMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun openLogin(prefillEmail: String? = null) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            if (!prefillEmail.isNullOrBlank()) {
                putExtra(LoginActivity.EXTRA_PREFILL_EMAIL, prefillEmail)
            }
        }
        startActivity(intent)
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
