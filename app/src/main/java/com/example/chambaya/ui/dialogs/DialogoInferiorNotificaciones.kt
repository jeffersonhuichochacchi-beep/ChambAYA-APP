package com.example.chambaya.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.DialogoNotificacionesBinding
import com.example.chambaya.ui.adapters.AdaptadorNotificaciones

class DialogoInferiorNotificaciones : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogoNotificacionesBinding.inflate(LayoutInflater.from(requireContext()))
        val repository = ChambayaRepository.getInstance(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val notifs = repository.getNotifications()
        val adapter = AdaptadorNotificaciones(notifs)
        binding.rvNotifications.adapter = adapter

        binding.btnCloseNotifs.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}
