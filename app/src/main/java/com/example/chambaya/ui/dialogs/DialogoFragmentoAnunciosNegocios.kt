package com.example.chambaya.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.chambaya.data.repository.ChambayaRepository
import com.example.chambaya.databinding.DialogoAnunciosNegociosBinding
import com.example.chambaya.ui.adapters.AdaptadorAnunciosNegocios

class DialogoFragmentoAnunciosNegocios : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogoAnunciosNegociosBinding.inflate(LayoutInflater.from(requireContext()))
        val repository = ChambayaRepository.getInstance(requireContext())

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val ads = repository.getBusinessAds()
        val adapter = AdaptadorAnunciosNegocios(ads)
        binding.rvBusinessAds.adapter = adapter

        binding.btnCloseAds.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}
