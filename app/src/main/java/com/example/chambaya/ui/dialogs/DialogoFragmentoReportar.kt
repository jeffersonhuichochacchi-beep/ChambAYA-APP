package com.example.chambaya.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.chambaya.databinding.DialogoReportarBinding

class DialogoFragmentoReportar : DialogFragment() {

    private var targetTitle: String = ""

    companion object {
        fun newInstance(targetTitle: String): DialogoFragmentoReportar {
            return DialogoFragmentoReportar().apply {
                this.targetTitle = targetTitle
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogoReportarBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnCancelReport.setOnClickListener {
            dismiss()
        }

        binding.btnSubmitReport.setOnClickListener {
            val selectedId = binding.rgReportReason.checkedRadioButtonId
            val reason = binding.root.findViewById<RadioButton>(selectedId)?.text ?: "Reporte general"
            val details = binding.etReportDetails.text.toString().trim()

            Toast.makeText(
                requireContext(),
                "Reporte recibido: '$reason'. El equipo de ChambAYA tomará medidas de seguridad.",
                Toast.LENGTH_LONG
            ).show()
            dismiss()
        }

        return dialog
    }
}
