package com.example.chambaya.data.auth

import android.content.Context
import com.example.chambaya.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirebaseGestorAutenticacion private constructor(private val context: Context) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    val estaAutenticado: Boolean
        get() = auth.currentUser != null

    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Registra un nuevo usuario con Correo y Contraseña, actualiza su perfil,
     * envía correo de verificación a su bandeja personal y guarda en Firestore.
     */
    fun registrarConCorreo(
        nombre: String,
        email: String,
        password: String,
        rol: String,
        onExito: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    // Actualizar DisplayName en Firebase Auth
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre)
                        .build()

                    user.updateProfile(profileUpdates)

                    // Enviar correo de autorización/verificación a su bandeja personal
                    user.sendEmailVerification()

                    // Guardar datos en Firestore
                    guardarUsuarioEnFirestore(user, nombre, rol) {
                        onExito(user)
                    }
                } else {
                    onError("No se pudo obtener el usuario creado.")
                }
            }
            .addOnFailureListener { exception ->
                onError(traducirErrorFirebase(exception.message))
            }
    }

    /**
     * Inicia sesión con Correo y Contraseña, comprobando que el correo esté verificado.
     */
    fun iniciarSesionConCorreo(
        email: String,
        password: String,
        onExito: (FirebaseUser) -> Unit,
        onEmailNoVerificado: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    // Recargar datos del usuario para tener el estado más reciente de isEmailVerified
                    user.reload().addOnCompleteListener {
                        if (user.isEmailVerified) {
                            onExito(user)
                        } else {
                            onEmailNoVerificado(user)
                        }
                    }
                } else {
                    onError("No se pudo iniciar sesión.")
                }
            }
            .addOnFailureListener { exception ->
                onError(traducirErrorFirebase(exception.message))
            }
    }

    /**
     * Reenvía el correo de verificación a la bandeja personal del usuario.
     */
    fun reenviarCorreoVerificacion(
        user: FirebaseUser,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        user.sendEmailVerification()
            .addOnSuccessListener { onExito() }
            .addOnFailureListener { exception ->
                onError(traducirErrorFirebase(exception.message))
            }
    }

    /**
     * Autentica con credencial de Google (IdToken) y sincroniza en Firestore.
     */
    fun autenticarConGoogle(
        idToken: String,
        rolPorDefecto: String,
        onExito: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    // Verificar si ya existe en Firestore para no sobreescribir su rol
                    val docRef = firestore.collection("usuarios").document(user.uid)
                    docRef.get().addOnSuccessListener { snapshot ->
                        if (!snapshot.exists()) {
                            // Crear nuevo documento en Firestore
                            guardarUsuarioEnFirestore(
                                user = user,
                                nombre = user.displayName ?: "Usuario Google",
                                rol = rolPorDefecto
                            ) {
                                onExito(user)
                            }
                        } else {
                            onExito(user)
                        }
                    }.addOnFailureListener {
                        onExito(user)
                    }
                } else {
                    onError("No se pudo autenticar con Google.")
                }
            }
            .addOnFailureListener { exception ->
                onError(traducirErrorFirebase(exception.message))
            }
    }

    /**
     * Guarda la información del usuario en Firestore (Colección "usuarios")
     */
    private fun guardarUsuarioEnFirestore(
        user: FirebaseUser,
        nombre: String,
        rol: String,
        onCompletado: () -> Unit
    ) {
        val datosUsuario = hashMapOf(
            "id" to user.uid,
            "nombre" to nombre,
            "email" to (user.email ?: ""),
            "rol" to rol,
            "fotoUrl" to (user.photoUrl?.toString() ?: ""),
            "fechaRegistro" to System.currentTimeMillis()
        )

        firestore.collection("usuarios")
            .document(user.uid)
            .set(datosUsuario, SetOptions.merge())

        onCompletado()
    }

    fun cerrarSesion() {
        auth.signOut()
        try {
            getGoogleSignInClient().signOut()
        } catch (_: Exception) {}
    }

    private fun traducirErrorFirebase(mensaje: String?): String {
        val error = mensaje ?: ""
        return when {
            error.contains("supplied auth credential", ignoreCase = true) ||
            error.contains("malformed or has expired", ignoreCase = true) ||
            error.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
            error.contains("invalid-credential", ignoreCase = true) ||
            error.contains("invalid credential", ignoreCase = true) ||
            error.contains("wrong-password", ignoreCase = true) ||
            error.contains("wrong password", ignoreCase = true) ->
                "Contraseña o correo incorrectos. Por favor, verifica tus datos."

            error.contains("There is no user record", ignoreCase = true) ||
            error.contains("user-not-found", ignoreCase = true) ||
            error.contains("user not found", ignoreCase = true) ->
                "No existe una cuenta con este correo electrónico."

            error.contains("already in use", ignoreCase = true) ||
            error.contains("email-already-in-use", ignoreCase = true) ->
                "Este correo ya está registrado. Inicia sesión o recupera tu contraseña."

            error.contains("badly formatted", ignoreCase = true) ||
            error.contains("invalid-email", ignoreCase = true) ->
                "El correo electrónico no tiene un formato válido."

            error.contains("at least 6 characters", ignoreCase = true) ||
            error.contains("weak-password", ignoreCase = true) ->
                "La contraseña debe tener al menos 6 caracteres."

            error.contains("too-many-requests", ignoreCase = true) ||
            error.contains("blocked all requests", ignoreCase = true) ||
            error.contains("unusual activity", ignoreCase = true) ->
                "Demasiados intentos fallidos. Por seguridad, espera unos minutos."

            error.contains("user-disabled", ignoreCase = true) ||
            error.contains("account has been disabled", ignoreCase = true) ->
                "Esta cuenta ha sido desactivada temporalmente."

            error.contains("network error", ignoreCase = true) ||
            error.contains("timeout", ignoreCase = true) ||
            error.contains("interrupted connection", ignoreCase = true) ->
                "Sin conexión a internet. Revisa tus datos móviles o Wi-Fi."

            else -> "Contraseña o correo incorrectos. Por favor, verifica tus datos."
        }
    }

    companion object {
        @Volatile
        private var instance: FirebaseGestorAutenticacion? = null

        fun getInstance(context: Context): FirebaseGestorAutenticacion {
            return instance ?: synchronized(this) {
                instance ?: FirebaseGestorAutenticacion(context.applicationContext).also { instance = it }
            }
        }
    }
}
