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
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "Por favor, ingresa tu nombre completo.",
                campoErroneo = binding.etFullName
            )
            binding.etFullName.requestFocus()
            return
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "Ingresa un correo electrónico válido.",
                campoErroneo = binding.etEmail
            )
            binding.etEmail.requestFocus()
            return
        }

        if (password.length < 6) {
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "La contraseña debe tener al menos 6 caracteres.",
                campoErroneo = binding.etPassword
            )
            binding.etPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "Las contraseñas no coinciden. Verifícalas.",
                campoErroneo = binding.etConfirmPassword
            )
            binding.etConfirmPassword.requestFocus()
            return
        }

        if (!aceptaTerminos) {
            com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                activity = this,
                rootLayout = binding.root,
                mensaje = "Debes aceptar los términos y condiciones de uso.",
                campoErroneo = binding.cbAgreement
            )
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
                com.example.chambaya.ui.common.AlertaAuth.mostrarErrorFlotante(
                    activity = this,
                    rootLayout = binding.root,
                    mensaje = error,
                    campoErroneo = binding.etPassword
                )
            }
        )
    }

    private var verificationHandler: android.os.Handler? = null
    private var verificationRunnable: Runnable? = null
    private var verificationDialog: androidx.appcompat.app.AlertDialog? = null

    private fun mostrarDialogoEsperandoVerificacion(user: com.google.firebase.auth.FirebaseUser, email: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_esperando_verificacion, null)

        val ivBadgeIcon = dialogView.findViewById<android.widget.ImageView>(R.id.ivBadgeIcon)
        val progressWaiting = dialogView.findViewById<android.widget.ProgressBar>(R.id.progressWaiting)
        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogTitle)
        val tvSubtitle = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogSubtitle)
        val tvEmail = dialogView.findViewById<android.widget.TextView>(R.id.tvDialogEmail)
        val btnDoneAction = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDoneAction)
        val layoutSecondary = dialogView.findViewById<android.view.View>(R.id.layoutSecondaryActions)
        val btnResend = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnResendVerification)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelVerification)

        tvEmail.text = email
        ivBadgeIcon.setImageResource(R.drawable.ic_mail_waiting_circle)
        progressWaiting.visibility = View.VISIBLE
        btnDoneAction.visibility = View.GONE

        verificationDialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        verificationDialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.70f) // Fondo oscuro / opaco como en la imagen
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                addFlags(android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                attributes = attributes.apply {
                    blurBehindRadius = 20
                }
            }
        }
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

        // Iniciar comprobación en tiempo real (cada 2 segundos)
        verificationHandler = android.os.Handler(android.os.Looper.getMainLooper())
        verificationRunnable = object : Runnable {
            override fun run() {
                user.reload().addOnCompleteListener { task ->
                    if (isFinishing || isDestroyed) return@addOnCompleteListener

                    if (user.isEmailVerified) {
                        detenerComprobacionVerificacion()

                        // Estado verificado exitoso (viñeta verde y botón idéntico a la imagen)
                        ivBadgeIcon.setImageResource(R.drawable.ic_success_seal_badge)
                        progressWaiting.visibility = View.GONE
                        tvTitle.text = "¡Verificación Exitosa!"
                        tvSubtitle.text = "Tu cuenta ha sido activada correctamente.\nRedirigiendo al inicio de sesión..."
                        tvEmail.visibility = View.GONE
                        layoutSecondary.visibility = View.GONE

                        btnDoneAction.visibility = View.VISIBLE
                        btnDoneAction.text = "Listo"
                        btnDoneAction.setOnClickListener {
                            verificationDialog?.dismiss()
                            authGestor.cerrarSesion()
                            openLogin(email)
                        }

                        // Redirigir al Login automáticamente después de 1.8 segundos
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            if (!isFinishing && !isDestroyed && verificationDialog?.isShowing == true) {
                                verificationDialog?.dismiss()
                                authGestor.cerrarSesion()
                                openLogin(email)
                            }
                        }, 1800L)
                    } else {
                        // Seguir consultando cada 2 segundos
                        verificationHandler?.postDelayed(this, 2000L)
                    }
                }
            }
        }
        verificationHandler?.postDelayed(verificationRunnable!!, 2000L)
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
        mostrarCarga(true)
        val client = authGestor.getGoogleSignInClient()
        client.signOut().addOnCompleteListener {
            mostrarCarga(false)
            val signInIntent = client.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
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
