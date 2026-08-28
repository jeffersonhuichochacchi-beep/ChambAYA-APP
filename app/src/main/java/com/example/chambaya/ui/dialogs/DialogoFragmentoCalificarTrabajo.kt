package com.example.chambaya.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.chambaya.data.model.Review
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.DialogoCalificarUsuarioBinding

class DialogoFragmentoCalificarTrabajo : DialogFragment() {

    private var onReviewSubmitted: ((Review) -> Unit)? = null

    companion object {
        fun newInstance(onReviewSubmitted: (Review) -> Unit): DialogoFragmentoCalificarTrabajo {
            return DialogoFragmentoCalificarTrabajo().apply {
                this.onReviewSubmitted = onReviewSubmitted
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogoCalificarUsuarioBinding.inflate(LayoutInflater.from(requireContext()))
        val repository = ChambayaRepository.getInstance(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnCancelReview.setOnClickListener {
            dismiss()
        }

        binding.btnSubmitReview.setOnClickListener {
            val name = binding.etReviewerName.text.toString().trim()
            val jobTitle = binding.etReviewJobTitle.text.toString().trim()
            val comment = binding.etReviewComment.text.toString().trim()
            val rating = binding.dialogRatingBar.rating

            if (comment.isEmpty()) {
                binding.etReviewComment.error = "Escribe un comentario"
                return@setOnClickListener
            }

            val newReview = repository.addReview(
                reviewerName = if (name.isEmpty()) "Contratante Ayacucho" else name,
                reviewerRole = "Contratante",
                rating = rating,
                comment = comment,
                jobTitle = if (jobTitle.isEmpty()) "Trabajo finalizado" else jobTitle
            )

            Toast.makeText(requireContext(), "¡Calificación enviada! Gracias por aportar a la confianza en ChambAYA.", Toast.LENGTH_LONG).show()
            onReviewSubmitted?.invoke(newReview)
            dismiss()
        }

        return dialog
    }
}
