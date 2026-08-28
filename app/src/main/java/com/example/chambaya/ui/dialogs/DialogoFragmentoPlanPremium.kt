package com.example.chambaya.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.chambaya.databinding.DialogoPlanPremiumBinding

class DialogoFragmentoPlanPremium : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogoPlanPremiumBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnCancelPremium.setOnClickListener {
            dismiss()
        }

        binding.btnSubscribePremium.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "¡Suscripción al Plan Premium activada con éxito! Ahora tienes publicaciones ilimitadas y estadísticas.",
                Toast.LENGTH_LONG
            ).show()
            dismiss()
        }

        return dialog
    }
}
