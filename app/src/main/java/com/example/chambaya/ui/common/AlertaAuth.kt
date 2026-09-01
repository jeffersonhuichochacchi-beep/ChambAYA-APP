package com.example.chambaya.ui.common

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chambaya.R
import com.google.android.material.snackbar.Snackbar

object AlertaAuth {

    /**
     * Hace vibrar suavemente y sacude la vista del campo con error (Shake Animation estilo iOS/Mac).
     */
    fun sacudirCampo(view: View) {
        val shake = TranslateAnimation(0f, 16f, 0f, 0f).apply {
            duration = 450
            interpolator = CycleInterpolator(3.5f)
        }
        view.startAnimation(shake)
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }

    /**
     * Muestra una notificación flotante elegante (estilo Uber / Airbnb / Nubank)
     * con esquinas redondeadas, icono de advertencia y animación suave.
     */
    fun mostrarErrorFlotante(activity: Activity, rootLayout: View, mensaje: String, campoErroneo: View? = null) {
        // Sacudir el campo si se proporcionó
        campoErroneo?.let { sacudirCampo(it) }

        val snackbar = Snackbar.make(rootLayout, "", Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view

        // Estilo moderno tipo píldora flotante
        val params = snackbarView.layoutParams as? ViewGroup.MarginLayoutParams
        params?.let {
            it.setMargins(32, 0, 32, 48)
            snackbarView.layoutParams = it
        }

        snackbarView.setBackgroundResource(R.drawable.bg_pill_chip)
        snackbarView.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#18181B"))
        snackbarView.elevation = 16f

        // Personalizar texto con icono de alerta
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.text = "⚠️  $mensaje"
        textView.setTextColor(Color.WHITE)
        textView.textSize = 14f
        textView.maxLines = 3
        textView.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)

        // Botón de acción rápido
        snackbar.setAction("OK") { snackbar.dismiss() }
        snackbar.setActionTextColor(Color.parseColor("#FCA5A5"))

        snackbar.show()
    }

    /**
     * Notificación flotante de éxito
     */
    fun mostrarExitoFlotante(activity: Activity, rootLayout: View, mensaje: String) {
        val snackbar = Snackbar.make(rootLayout, "", Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view

        val params = snackbarView.layoutParams as? ViewGroup.MarginLayoutParams
        params?.let {
            it.setMargins(32, 0, 32, 48)
            snackbarView.layoutParams = it
        }

        snackbarView.setBackgroundResource(R.drawable.bg_pill_chip)
        snackbarView.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#15803D"))
        snackbarView.elevation = 16f

        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.text = "✅  $mensaje"
        textView.setTextColor(Color.WHITE)
        textView.textSize = 14f
        textView.typeface = android.graphics.Typeface.create("sans-serif-bold", android.graphics.Typeface.NORMAL)

        snackbar.show()
    }
}
