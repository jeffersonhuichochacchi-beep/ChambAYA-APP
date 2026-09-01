package com.example.chambaya.ui.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chambaya.RegistroActivity
import com.example.chambaya.databinding.DialogoSeleccionarRolRegistroBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogoSeleccionarRolRegistro : BottomSheetDialogFragment() {

    private var _binding: DialogoSeleccionarRolRegistroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogoSeleccionarRolRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardSelectWorker.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), RegistroActivity::class.java).apply {
                putExtra(RegistroActivity.EXTRA_USER_TYPE, RegistroActivity.USER_TYPE_WORKER)
            }
            startActivity(intent)
        }

        binding.cardSelectEmployer.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), RegistroActivity::class.java).apply {
                putExtra(RegistroActivity.EXTRA_USER_TYPE, RegistroActivity.USER_TYPE_EMPLOYER)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DialogoSeleccionarRolRegistro"

        fun newInstance(): DialogoSeleccionarRolRegistro {
            return DialogoSeleccionarRolRegistro()
        }
    }
}
